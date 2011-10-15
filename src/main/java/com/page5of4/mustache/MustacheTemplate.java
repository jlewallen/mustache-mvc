package com.page5of4.mustache;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sampullara.mustache.Mustache;
import com.sampullara.mustache.MustacheException;
import com.sampullara.mustache.MustacheTrace;

public class MustacheTemplate extends Mustache {
   private static final Logger logger = LoggerFactory.getLogger(MustacheTemplate.class);
   private MustacheViewEngine viewEngine;
   private String view;

   public void setViewEngine(MustacheViewEngine viewEngine) {
      this.viewEngine = viewEngine;
   }

   public void setViewName(String view) {
      this.view = view;
   }

   @Override
   protected Mustache compilePartial(String name) throws MustacheException {
      MustacheTrace.Event event = null;
      if(trace) {
         event = MustacheTrace.addEvent("compile partial: " + name, getRoot() == null ? "classpath" : getRoot().getName());
      }
      File file = new File(view);
      String path = file.getParent() + "/" + name;
      Mustache mustache = viewEngine.createMustache(path);
      mustache.setMustacheJava(mj);
      mustache.setRoot(getRoot());
      if(trace) {
         event.end();
      }
      return mustache;
   }
}
