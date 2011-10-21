package com.page5of4.mustache.spring;

import java.util.Map;

public class ModelOnlyJacksonJsonView extends org.springframework.web.servlet.view.json.MappingJacksonJsonView {

   @Override
   @SuppressWarnings("unchecked")
   protected Object filterModel(Map<String, Object> model) {
      Map<String, Object> filtered = (Map<String, Object>)super.filterModel(model);
      if(filtered.isEmpty() || filtered.size() > 1) {
         return filtered;
      }
      return filtered.values().iterator().next();
   }

}
