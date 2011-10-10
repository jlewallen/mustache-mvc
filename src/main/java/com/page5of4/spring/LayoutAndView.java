package com.page5of4.spring;

public class LayoutAndView {
   private final String layout;
   private final String view;

   public String getLayout() {
      return layout;
   }

   public String getView() {
      return view;
   }

   public LayoutAndView(String layout, String view) {
      super();
      this.layout = layout;
      this.view = view;
   }

   public static LayoutAndView getLayoutAndView(String url, String defaultLayout) {
      String[] parts = url.split(":");
      if(parts.length == 1) {
         return new LayoutAndView(defaultLayout, parts[0]);
      }
      return new LayoutAndView(parts[0], parts[1]);
   }
}
