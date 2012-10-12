package com.page5of4.mustache.spring;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.collect.Maps;
import com.page5of4.mustache.TemplateSourceLoader;
import com.page5of4.mustache.TemplateSourceLoader.TemplateSource;

public class MustacheTemplatesController {
   private static final Map<String, String> keywords = Maps.newHashMap();

   static {
      String[] reserved = new String[] {
            "break", "else", "new", "var",
            "case", "finally", "return", "void",
            "catch", "for", "switch", "while",
            "continue", "function", "this", "with",
            "default", "if", "throw",
            "delete", "in", "try",
            "do", "instanceof", "typeof", "true", "false"
      };
      for(String word : reserved) {
         keywords.put(word, "_" + word);
      }
   }

   @Autowired
   private TemplateSourceLoader templateSourceLoader;

   public void setTemplateSourceLoader(TemplateSourceLoader templateSourceLoader) {
      this.templateSourceLoader = templateSourceLoader;
   }

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
      sb.write("templates.prepare = function(model) {\n");
      sb.write("  return model;\n");
      sb.write("}\n");
      sb.write("\n");
      for(Map.Entry<String, String> entry : all().entrySet()) {
         String key = escapePathKeywords(entry.getKey());
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
         sb.write("  return Mustache.to_html(template, templates.prepare(model), templates.bodies);\n");
         sb.write("}\n\n");
      }
   }

   private String escapePathKeywords(String path) {
      StringBuilder escaped = new StringBuilder();
      for(String part : path.split("\\.")) {
         if(escaped.length() != 0) {
            escaped.append(".");
         }
         String escapedPart = part;
         if(keywords.containsKey(part)) {
            escapedPart = keywords.get(part);
         }
         escaped.append(escapedPart);
      }
      return escaped.toString();
   }

   private Map<String, String> all() {
      try {
         Map<String, String> all = new HashMap<String, String>();
         for(TemplateSource template : templateSourceLoader.getTemplates()) {
            InputStream stream = template.getResource().getInputStream();
            try {
               all.put(template.getRelativePath().replace("/", ".").replace("-", "_"), IOUtils.toString(stream));
            }
            finally {
               stream.close();
            }
         }
         return all;
      }
      catch(Exception e) {
         throw new RuntimeException(e);
      }
   }
}
