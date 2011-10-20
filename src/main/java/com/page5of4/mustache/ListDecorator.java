package com.page5of4.mustache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ListDecorator {

   public static <T> List<Row<T>> decorate(Collection<T> items) {
      List<Row<T>> rows = new ArrayList<Row<T>>();
      long index = 0;
      for(T item : items) {
         rows.add(new Row<T>(index++, item));
      }
      return rows;
   }

   public static class Row<T> {
      private long index;
      private T item;

      public long getIndex() {
         return index;
      }

      public T getItem() {
         return item;
      }

      public Row(long index, T item) {
         super();
         this.index = index;
         this.item = item;
      }
   }

}
