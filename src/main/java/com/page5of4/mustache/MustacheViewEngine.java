package com.page5of4.mustache;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletConfig;
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
import org.springframework.web.servlet.support.RequestContextUtils;

import com.page5of4.spring.LayoutAndView;
import com.sampullara.mustache.Mustache;
import com.sampullara.mustache.MustacheBuilder;
import com.sampullara.mustache.Scope;

public class MustacheViewEngine implements ApplicationContextAware {

   private static Logger logger = LoggerFactory.getLogger(MustacheViewEngine.class);
   private final ConcurrentHashMap<String, Mustache> cache = new ConcurrentHashMap<String, Mustache>();
   private ApplicationContext applicationContext;
   private boolean cacheEnabled;
   @Autowired
   private HttpServletRequest servletRequest;
   @Autowired
   private LayoutViewModelFactory layoutViewModelFactory;

   public boolean isCacheEnabled() {
      return cacheEnabled;
   }

   public void setCacheEnabled(boolean cacheEnabled) {
      this.cacheEnabled = cacheEnabled;
   }

   public boolean contains(ServletConfig servletConfig, String url) {
      return getResource(servletConfig, url) != null;
   }

   public void render(final ServletConfig servletConfig, final String view, final Scope scope, final PrintWriter writer) {
      try {
         String template = getSource(servletConfig, view);
         Mustache mustache = createMustache(view, template);
         mustache.execute(writer, scope);
      }
      catch(Exception e) {
         throw new RuntimeException("Error rendering: " + view, e);
      }
   }

   public void render(final ServletConfig servletConfig, final LayoutAndView lav, final Map<String, Object> model, final PrintWriter writer) {
      if(lav.getLayout() == null) {
         render(servletConfig, lav.getView(), new Scope(model), writer);
      }
      else {
         LayoutViewModel layoutViewModel = layoutViewModelFactory.createLayoutViewModel(model, new LayoutBodyFunction() {
            @Override
            public String getBody() {
               StringWriter sw = new StringWriter();
               render(servletConfig, lav.getView(), new Scope(model), new PrintWriter(sw));
               return sw.toString();
            }
         });
         render(servletConfig, lav.getLayout(), new Scope(layoutViewModel), writer);
      }
   }

   private Mustache createMustache(String key, String template) {
      if(cache.containsKey(key)) {
         return cache.get(key);
      }
      try {
         MustacheBuilder builder = new MustacheBuilder();
         Mustache mustache = builder.parse(template);
         if(isCacheEnabled()) {
            cache.put(key, mustache);
         }
         return mustache;
      }
      catch(Exception e) {
         throw new RuntimeException("Error compiling: " + key, e);
      }
   }

   private String getSource(ServletConfig servletConfig, String url) {
      try {
         return IOUtils.toString(getResource(servletConfig, url).getInputStream());
      }
      catch(Exception e) {
         throw new RuntimeException("Error resolving: " + url, e);
      }
   }

   private Resource getResource(ServletConfig servletConfig, String url) {
      try {
         LocalizedResourceHelper helper = new LocalizedResourceHelper(applicationContext);
         Locale userLocale = RequestContextUtils.getLocale(servletRequest);
         return helper.findLocalizedResource("WEB-INF/views/" + url, ".html", userLocale);
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
