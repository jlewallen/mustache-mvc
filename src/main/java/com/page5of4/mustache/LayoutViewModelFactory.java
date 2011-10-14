package com.page5of4.mustache;

import java.util.Map;

public interface LayoutViewModelFactory {

   public LayoutViewModel createLayoutViewModel(Map<String, Object> bodyModel, LayoutBodyFunction bodyFunction);

}
