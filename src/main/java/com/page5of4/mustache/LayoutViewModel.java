package com.page5of4.mustache;


public class LayoutViewModel {

   private final ApplicationModel applicationModel;
   private final LayoutBodyFunction layoutBodyFunction;
   private final Object bodyModel;

   public ApplicationModel getApplicationModel() {
      return applicationModel;
   }

   public String getBody() {
      return layoutBodyFunction.getBody();
   }

   public Object getBodyModel() {
      return bodyModel;
   }

   public LayoutViewModel(ApplicationModel applicationModel, Object bodyModel, LayoutBodyFunction layoutBodyFunction) {
      super();
      this.applicationModel = applicationModel;
      this.bodyModel = bodyModel;
      this.layoutBodyFunction = layoutBodyFunction;
   }

}
