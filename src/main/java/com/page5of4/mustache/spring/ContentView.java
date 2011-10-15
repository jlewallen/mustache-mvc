package com.page5of4.mustache.spring;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.AbstractView;

import com.google.common.io.CharStreams;
import com.google.common.io.Closeables;

public class ContentView extends AbstractView {
   private static final String CONTENT_TYPE = "Content-type";
   private final String json;
   private final String contentType;

   public ContentView(final String json, String contentType) {
      this.json = json;
      this.contentType = contentType;
   }

   @Override
   protected void renderMergedOutputModel(final Map<String, Object> model, final HttpServletRequest request, final HttpServletResponse response) {
      PrintWriter writer = null;
      try {
         response.setHeader(CONTENT_TYPE, contentType);
         writer = response.getWriter();
         CharStreams.copy(CharStreams.newReaderSupplier(json), writer);
      }
      catch(final IOException e) {
         throw new IllegalStateException(e);
      }
      finally {
         Closeables.closeQuietly(writer);
      }
   }
}