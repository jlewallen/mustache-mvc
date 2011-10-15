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

   public boolean containsView(String url) {
      return getResource(url) != null;
   }

   public Mustache createMustache(String view) {
      String template = getSource(view);
      return createMustache(view, template);
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
      if(lav.getLayout() == null) {
         render(lav.getView(), new Scope(model), writer);
      }
      else {
         LayoutViewModel layoutViewModel = layoutViewModelFactory.createLayoutViewModel(model, new LayoutBodyFunction() {
            @Override
            public String getBody() {
               StringWriter sw = new StringWriter();
               render(lav.getView(), new Scope(model), new PrintWriter(sw));
               return sw.toString();
            }
         });
         render(lav.getLayout(), new Scope(layoutViewModel), writer);
      }
   }

   private Mustache createMustache(String key, String template) {
      if(cache.containsKey(key)) {
         return cache.get(key);
      }
      try {
         MustacheBuilder builder = new MustacheBuilder();
         builder.setSuperclass(MvcMustache.class.getName());
         MvcMustache mustache = (MvcMustache)builder.parse(template);
         mustache.setViewEngine(this);
         if(isCacheEnabled()) {
            cache.put(key, mustache);
         }
         return mustache;
      }
      catch(Exception e) {
         throw new RuntimeException("Error compiling: " + key, e);
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

   private Resource getResource(String url) {
      try {
         LocalizedResourceHelper helper = new LocalizedResourceHelper(applicationContext);
         // Going to need to cache this, we could be called from another thread and won't have access to that Request.
         // Locale userLocale = RequestContextUtils.getLocale(servletRequest);
         Locale userLocale = Locale.US;
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
