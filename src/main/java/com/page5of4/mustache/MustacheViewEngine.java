package com.page5of4.mustache;

import java.io.File;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.LocalizedResourceHelper;

import com.page5of4.mustache.spring.LayoutAndView;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Mustache.TemplateLoader;
import com.samskivert.mustache.Template;

public class MustacheViewEngine implements ApplicationContextAware {
   private static Logger logger = LoggerFactory.getLogger(MustacheViewEngine.class);
   private final ConcurrentHashMap<String, Template> cache = new ConcurrentHashMap<String, Template>();
   private final LayoutViewModelFactory layoutViewModelFactory;
   private ApplicationContext applicationContext;
   private boolean cacheEnabled;
   private String basePath = "/WEB-INF/views/";

   public String getBasePath() {
      return basePath;
   }

   public void setBasePath(String basePath) {
      this.basePath = basePath;
   }

   @Autowired
   public MustacheViewEngine(LayoutViewModelFactory layoutViewModelFactory, HttpServletRequest servletRequest) {
      this.layoutViewModelFactory = layoutViewModelFactory;
   }

   public boolean isCacheEnabled() {
      return cacheEnabled;
   }

   public void setCacheEnabled(boolean cacheEnabled) {
      this.cacheEnabled = cacheEnabled;
   }

   public boolean containsView(String url) {
      return getResource(url) != null;
   }

   public Template createMustache(String view) {
      String uri = getViewURI(view);
      String template = getSource(uri);
      return createMustache(uri, template);
   }

   public void render(final String view, final Object scope, final Object parentScope, final PrintWriter writer) {
      try {
         Template template = createMustache(view);
         template.execute(scope, parentScope, writer);
      }
      catch(Exception e) {
         throw new RuntimeException("Error rendering: " + view, e);
      }
   }

   public void render(final LayoutAndView lav, final Map<String, Object> model, final PrintWriter writer) {
      final Object parentBodyScope = layoutViewModelFactory.createLayoutViewModel(model);
      final Object bodyScope = SingleModelAndView.getBodyModel(model);
      if(lav.getLayout() == null) {
         render(lav.getView(), bodyScope, parentBodyScope, writer);
      }
      else {
         LayoutViewModel layoutViewModel = layoutViewModelFactory.createLayoutViewModel(model, new LayoutBodyFunction() {
            @Override
            public String getBody() {
               StringWriter sw = new StringWriter();
               render(lav.getView(), bodyScope, parentBodyScope, new PrintWriter(sw));
               return sw.toString();
            }
         });
         render(lav.getLayout(), layoutViewModel, null, writer);
      }
   }

   private Template createMustache(final String view, String template) {
      if(cache.containsKey(view)) {
         return cache.get(view);
      }
      try {
         Template compiled = Mustache.compiler().withLoader(new TemplateLoader() {
            @Override
            public Reader getTemplate(String name) throws Exception {
               File file = new File(view);
               String path = file.getParent() + File.separator + name;
               String uri = getViewURI(path);
               return new StringReader(getSource(uri));
            }
         }).compile(template);
         if(isCacheEnabled()) {
            cache.put(view, compiled);
         }
         return compiled;
      }
      catch(Exception e) {
         throw new RuntimeException("Error compiling: " + view, e);
      }
   }

   private String getSource(String url) {
      try {
         return IOUtils.toString(getResource(url).getInputStream());
      }
      catch(Exception e) {
         throw new RuntimeException("Error resolving: " + url, e);
      }
   }

   private String getViewURI(String view) {
      if(view.startsWith("/") || view.startsWith("\\")) return view;
      return basePath + view;
   }

   private Resource getResource(String url) {
      try {
         LocalizedResourceHelper helper = new LocalizedResourceHelper(applicationContext);
         // Going to need to cache this, we could be called from another thread and won't have access to that Request.
         // Locale userLocale = RequestContextUtils.getLocale(servletRequest);
         Locale userLocale = Locale.US;
         return helper.findLocalizedResource(url, ".html", userLocale);
      }
      catch(Exception e) {
         throw new RuntimeException("Error resolving: " + url, e);
      }
   }

   @Override
   public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
      this.applicationContext = applicationContext;
   }

}
