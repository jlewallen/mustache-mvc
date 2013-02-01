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
import org.springframework.web.context.support.ServletContextResource;

import com.google.common.collect.Lists;

public class ClassPathTemplateSourceLoader implements TemplateSourceLoader, ApplicationContextAware {
   private PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
   private String basePath = "classpath:/views/";
   private ApplicationContext applicationContext;
   private final List<String> excludedPatterns = Lists.newArrayList();
   private final List<String> includedPatterns = Lists.newArrayList();

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

   public void addExcludedPattern(String pattern) {
      excludedPatterns.add(pattern);
   }

   public void addIncludePattern(String pattern) {
      includedPatterns.add(pattern);
   }

   @Override
   public boolean containsSource(String path) {
      return getResource(getViewURI(path)).exists();
   }

   @Override
   public String getSource(String path) {
      String url = getViewURI(path);
      try {
         assertUrlIsAllowed(url);
         return IOUtils.toString(getResource(url).getInputStream(), "UTF-8");
      }
      catch(Exception e) {
         throw new RuntimeException(String.format("Error resolving: %s (%s)", url, path), e);
      }
   }

   protected boolean urlAllowed(String url) {
      for(String pattern : excludedPatterns) {
         if(url.matches(pattern)) {
            return false;
         }
      }
      if(includedPatterns.size() > 0) {
         for(String pattern : includedPatterns) {
            if(url.matches(pattern)) {
               return true;
            }
         }
         return false;
      }
      return true;
   }

   protected void assertUrlIsAllowed(String url) throws Exception {
      if(!urlAllowed(url)) {
         throw new Exception("Path was excluded");
      }
   }

   @Override
   public String getPartial(String view, String path) {
      File file = new File(basePath, view);
      String partialPath = (file.getParent() + File.separator + path).replace(File.separator, "/").replace(basePath, "");
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
            ServletContextResource contextResource = (ServletContextResource)resource;
            if(urlAllowed(contextResource.getPath())) {
               String key = getClientSideKeyForResource(resource);
               if(key != null) {
                  templates.add(new TemplateSource(key, resource));
               }

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
