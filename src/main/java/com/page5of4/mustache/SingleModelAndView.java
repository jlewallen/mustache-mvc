package com.page5of4.mustache;

import java.util.Map;

import org.springframework.web.servlet.ModelAndView;

public class SingleModelAndView extends ModelAndView {
   public static final String DEFAULT_BODY_MODEL_NAME = "model";
   public static final String DEFAULT_LAYOUT_MODEL_NAME = "layoutModel";
   public static final String APPLICATION_MODEL_NAME = "applicationModel";

   public static Object getLayoutModel(Map<String, Object> map) {
      if(map.containsKey(DEFAULT_LAYOUT_MODEL_NAME)) {
         return map.get(DEFAULT_LAYOUT_MODEL_NAME);
      }
      return map;
   }

   public static Object getBodyModel(Map<String, Object> map) {
      if(map.containsKey(DEFAULT_BODY_MODEL_NAME)) {
         return map.get(DEFAULT_BODY_MODEL_NAME);
      }
      return map;
   }

   public SingleModelAndView(String view) {
      this(view, null, null);
   }

   public SingleModelAndView(Object bodyModel) {
      this(null, bodyModel, null);
   }

   public SingleModelAndView(String view, Object bodyModel) {
      this(view, bodyModel, null);
   }

   public SingleModelAndView(String view, Object bodyModel, Object layoutModel) {
      super(view, DEFAULT_BODY_MODEL_NAME, bodyModel);
      addObject(DEFAULT_LAYOUT_MODEL_NAME, layoutModel);
   }

}
