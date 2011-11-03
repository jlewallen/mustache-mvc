package com.page5of4.mustache;

public class LayoutViewModel {
   private final ApplicationModel applicationModel;
   private final LayoutBodyFunction layoutBodyFunction;
   private final Object bodyModel;
   private final Object layoutModel;
   private final String bodyModelAsJSON;

   public ApplicationModel getApplicationModel() {
      return applicationModel;
   }

   public String getBody() {
      if(layoutBodyFunction == null) {
         throw new RuntimeException("{{body}} is only valid from within a Layout.");
      }
      return layoutBodyFunction.getBody();
   }

   public Object getBodyModel() {
      return bodyModel;
   }

   public String getBodyModelAsJSON() {
      return bodyModelAsJSON;
   }

   public Object getLayoutModel() {
      return layoutModel;
   }

   public LayoutViewModel(ApplicationModel applicationModel, Object bodyModel, String bodyModelAsJSON, Object layoutModel, LayoutBodyFunction layoutBodyFunction) {
      super();
      this.applicationModel = applicationModel;
      this.bodyModel = bodyModel;
      this.bodyModelAsJSON = bodyModelAsJSON;
      this.layoutModel = layoutModel;
      this.layoutBodyFunction = layoutBodyFunction;
   }
}
