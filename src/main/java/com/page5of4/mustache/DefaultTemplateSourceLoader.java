package com.page5of4.mustache;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.LocalizedResourceHelper;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.support.ServletContextResourcePatternResolver;

import com.google.common.collect.Lists;

public class DefaultTemplateSourceLoader implements TemplateSourceLoader, ApplicationContextAware, ServletContextAware {
   private ApplicationContext applicationContext;
   private ServletContext servletContext;
   private ServletContextResourcePatternResolver resolver;
   private String basePath = File.separator + "WEB-INF" + File.separator + "views" + File.separator;

   @Override
   public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
      this.applicationContext = applicationContext;
   }

   public String getBasePath() {
      return basePath;
   }

   public void setBasePath(String basePath) {
      this.basePath = basePath;
   }

   @Override
   public boolean containsSource(String path) {
      return getResource(getViewURI(path)).exists();
   }

   @Override
   public String getSource(String path) {
      String url = getViewURI(path);
      try {
         return IOUtils.toString(getResource(url).getInputStream());
      }
      catch(Exception e) {
         throw new RuntimeException(String.format("Error resolving: %s (%s)", url, path), e);
      }
   }

   @Override
   public String getPartial(String view, String path) {
      File file = new File(basePath, view);
      String partialPath = (file.getParent() + File.separator + path).replace(basePath, "");
      return getSource(partialPath);
   }

   private Resource getResource(String url) {
      try {
         LocalizedResourceHelper helper = new LocalizedResourceHelper(applicationContext);
         // Going to need to cache this, we could be called from another thread and won't have access to that Request.
         // Locale userLocale = RequestContextUtils.getLocale(servletRequest);
         Locale userLocale = Locale.US;
         Resource resource = helper.findLocalizedResource(url, ".html", userLocale);
         return resource;
      }
      catch(Exception e) {
         throw new RuntimeException(String.format("Error resolving: %s", url), e);
      }
   }

   private String getViewURI(String view) {
      if(view.startsWith("/") || view.startsWith("\\")) return view;
      return basePath + view;
   }

   private String getNormalizedBasePath() {
      String path = basePath.replace("\\", "/");
      if(path.endsWith("/")) return path;
      return path + "/";
   }

   @Override
   public Collection<TemplateSource> getTemplates() {
      try {
         List<TemplateSource> templates = Lists.newArrayList();
         Pattern pattern = Pattern.compile(String.format("%s(\\S+).html", getNormalizedBasePath()));
         for(Resource resource : resolver.getResources(String.format("%s**/*.html", getNormalizedBasePath()))) {
            Matcher matcher = pattern.matcher(resource.getURI().toString());
            if(matcher.find()) {
               templates.add(new TemplateSource(matcher.group(1), resource));
            }
         }
         return templates;
      }
      catch(IOException e) {
         throw new RuntimeException(e);
      }
   }

   @Override
   public void setServletContext(ServletContext servletContext) {
      this.servletContext = servletContext;
      this.resolver = new ServletContextResourcePatternResolver(servletContext);
   }
}
