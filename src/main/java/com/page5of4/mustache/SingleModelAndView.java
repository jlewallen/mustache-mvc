package com.page5of4.mustache;

import org.springframework.web.servlet.ModelAndView;

public class SingleModelAndView extends ModelAndView {

   public SingleModelAndView(String view, Object model) {
      super(view, "model", model);
   }

}
