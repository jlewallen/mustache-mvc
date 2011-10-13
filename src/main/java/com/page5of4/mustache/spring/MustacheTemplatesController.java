package com.page5of4.mustache.spring;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.support.ServletContextResourcePatternResolver;

public class MustacheTemplatesController implements ServletContextAware {

   private ServletContext servletContext;
   private ServletContextResourcePatternResolver resolver;

   @RequestMapping(method = RequestMethod.GET)
   public void index(HttpServletResponse servletResponse) throws IOException {
      servletResponse.setContentType("text/javascript");
      templates(servletResponse.getWriter());
   }

   public void templates(Writer writer) throws IOException {
      Writer sb = writer;
      sb.write("var templates = {};\n");
      sb.write("templates.bodies = {};\n");
      sb.write("\n");
      for(Map.Entry<String, String> entry : all().entrySet()) {
         String key = entry.getKey();
         StringBuilder path = new StringBuilder();
         String[] parts = key.split("\\.");
         for(int i = 0; i < parts.length - 1; ++i) {
            String part = parts[i];
            if(path.length() != 0) {
               path.append(".");
            }
            path.append(part);
            sb.append(String.format("if (typeof(templates.bodies.%s) == 'undefined') templates.bodies.%s = {};\n", path.toString(), path.toString()));
            sb.append(String.format("if (typeof(templates.%s) == 'undefined') templates.%s = {};\n", path.toString(), path.toString()));
         }
         sb.write(String.format("templates.bodies.%s = \"%s\";\n", key, StringEscapeUtils.escapeJavaScript(entry.getValue())));
         sb.write(String.format("templates.%s = function(model) {\n", key));
         sb.write(String.format("  var template = templates.bodies.%s;\n", key));
         sb.write("  return Mustache.to_html(template, model, templates.bodies);\n");
         sb.write("}\n\n");
      }
   }

   private Map<String, String> all() {
      try {
         Map<String, String> all = new HashMap<String, String>();
         Pattern pattern = Pattern.compile("/WEB-INF/views/(\\S+).html");
         for(Resource resource : resolver.getResources("/WEB-INF/views/**/*.html")) {
            Matcher matcher = pattern.matcher(resource.getURI().toString());
            if(matcher.find()) {
               InputStream stream = resource.getInputStream();
               try {
                  all.put(matcher.group(1).replace("/", ".").replace("-", "_"), IOUtils.toString(stream));
               }
               finally {
                  stream.close();
               }
            }
         }
         return all;
      }
      catch(Exception e) {
         throw new RuntimeException(e);
      }
   }

   @Override
   public void setServletContext(ServletContext servletContext) {
      this.servletContext = servletContext;
      this.resolver = new ServletContextResourcePatternResolver(servletContext);
   }
}
