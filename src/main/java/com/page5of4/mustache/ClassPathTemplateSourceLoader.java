package com.page5of4.mustache;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.LocalizedResourceHelper;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.google.common.collect.Lists;

public class ClassPathTemplateSourceLoader implements TemplateSourceLoader, ApplicationContextAware {
   private PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
   private String basePath = "classpath:/views/";
   private ApplicationContext applicationContext;

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

   public PathMatchingResourcePatternResolver getResolver() {
      return resolver;
   }

   public void setResolver(PathMatchingResourcePatternResolver resolver) {
      this.resolver = resolver;
   }

   public ApplicationContext getApplicationContext() {
      return applicationContext;
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

   protected Resource getResource(String url) {
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

   protected String getViewURI(String view) {
      if(view.startsWith("/") || view.startsWith("\\")) return view;
      return basePath + view;
   }

   protected String getNormalizedBasePath() {
      String path = basePath.replace("\\", "/");
      if(path.endsWith("/")) return path;
      return path + "/";
   }

   @Override
   public Collection<TemplateSource> getTemplates() {
      try {
         List<TemplateSource> templates = Lists.newArrayList();
         for(Resource resource : resolver.getResources(String.format("%s**/*.html", getNormalizedBasePath()))) {
            String key = getClientSideKeyForResource(resource);
            if(key != null) {
               templates.add(new TemplateSource(key, resource));
            }
         }
         return templates;
      }
      catch(IOException e) {
         throw new RuntimeException(e);
      }
   }

   protected String getClientSideKeyForResource(Resource resource) {
      try {
         String path = resource.getURI().toString();
         String directory = getNormalizedBasePath().replace("classpath:", "").replace("classpath*:", "");
         int i = path.indexOf(directory);
         if(i < 0) {
            return null;
         }
         String relative = path.substring(i + directory.length());
         return relative.replace(".html", "");
      }
      catch(IOException e) {
         throw new RuntimeException(e);
      }
   }
}
