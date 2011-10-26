package com.page5of4.mustache;

import java.io.PrintWriter;
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
import com.sampullara.mustache.Mustache;
import com.sampullara.mustache.MustacheBuilder;
import com.sampullara.mustache.Scope;

public class MustacheViewEngine implements ApplicationContextAware {

   private static Logger logger = LoggerFactory.getLogger(MustacheViewEngine.class);
   private final ConcurrentHashMap<String, Mustache> cache = new ConcurrentHashMap<String, Mustache>();
   private final LayoutViewModelFactory layoutViewModelFactory;
   private final MustacheBuilder builder;
   private ApplicationContext applicationContext;
   private boolean cacheEnabled;

   @Autowired
   public MustacheViewEngine(LayoutViewModelFactory layoutViewModelFactory, HttpServletRequest servletRequest) {
      this.layoutViewModelFactory = layoutViewModelFactory;
      builder = new MustacheBuilder();
      builder.setSuperclass(MustacheTemplate.class.getName());
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

   public Mustache createMustache(String view) {
      String uri = getViewURI(view);
      String template = getSource(uri);
      return createMustache(uri, template);
   }

   public void render(final String view, final Scope scope, final PrintWriter writer) {
      try {
         Mustache mustache = createMustache(view);
         mustache.execute(writer, scope);
      }
      catch(Exception e) {
         throw new RuntimeException("Error rendering: " + view, e);
      }
   }

   public void render(final LayoutAndView lav, final Map<String, Object> model, final PrintWriter writer) {
      final Scope scope = createScope(model);
      if(lav.getLayout() == null) {
         render(lav.getView(), scope, writer);
      }
      else {
         LayoutViewModel layoutViewModel = layoutViewModelFactory.createLayoutViewModel(model, new LayoutBodyFunction() {
            @Override
            public String getBody() {
               StringWriter sw = new StringWriter();
               render(lav.getView(), scope, new PrintWriter(sw));
               return sw.toString();
            }
         });
         render(lav.getLayout(), new Scope(layoutViewModel), writer);
      }
   }

   private Scope createScope(final Map<String, Object> model) {
      return new Scope(SingleModelAndView.getBodyModel(model));
   }

   private Mustache createMustache(String view, String template) {
      if(cache.containsKey(view)) {
         return cache.get(view);
      }
      try {
         MustacheTemplate mustache = (MustacheTemplate)builder.parse(template);
         mustache.setViewEngine(this);
         mustache.setViewName(view);
         if(isCacheEnabled()) {
            cache.put(view, mustache);
         }
         return mustache;
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
      return "/WEB-INF/views/" + view;
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
