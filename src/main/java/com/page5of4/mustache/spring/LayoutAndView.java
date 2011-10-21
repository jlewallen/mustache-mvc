package com.page5of4.mustache.spring;

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

   public static LayoutAndView getLayoutAndView(String url, String defaultLayout, boolean forceNoLayout) {
      String[] parts = url.split(":");
      String layout = defaultLayout;
      String view = url;
      if(parts.length == 2) {
         layout = parts[0];
         view = parts[1];
      }
      if(forceNoLayout) {
         layout = null;
      }
      return new LayoutAndView(layout, view);
   }
}
