package com.page5of4.mustache;

public interface TemplateSourceLoader {
   boolean containsSource(String path);

   String getSource(String path);
}
