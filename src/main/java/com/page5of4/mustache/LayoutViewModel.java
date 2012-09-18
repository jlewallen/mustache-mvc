package com.page5of4.mustache;

public interface LayoutViewModel {
	ApplicationModel getApplicationModel();

	String getBody();

	String getHeaders();

	Object getBodyModel();

	String getBodyModelAsJSON();

	Object getLayoutModel();
}
