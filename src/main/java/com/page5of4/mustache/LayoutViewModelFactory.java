package com.page5of4.mustache;

import java.util.Map;

public interface LayoutViewModelFactory {

   LayoutViewModel createLayoutViewModel(Map<String, Object> bodyModel);

   LayoutViewModel createLayoutViewModel(Map<String, Object> bodyModel, LayoutBodyFunction bodyFunction);

   LayoutViewModel createLayoutViewModel(Map<String, Object> bodyModel, LayoutBodyFunction bodyFunction, LayoutHeadersFunction headersFunction);

}
