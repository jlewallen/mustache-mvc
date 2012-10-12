package com.page5of4.mustache;

import java.util.Collection;

import org.springframework.core.io.Resource;

public interface TemplateSourceLoader {
   boolean containsSource(String path);

   String getSource(String path);

   String getPartial(String view, String path);

   Collection<TemplateSource> getTemplates();

   public static class TemplateSource {
      private final String relativePath;
      private final Resource resource;

      public String getRelativePath() {
         return relativePath;
      }

      public Resource getResource() {
         return resource;
      }

      public TemplateSource(String relativePath, Resource resource) {
         super();
         this.relativePath = relativePath;
         this.resource = resource;
      }
   }
}
