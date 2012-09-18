package com.page5of4.mustache;

import com.samskivert.mustache.Mustache;

public class DefaultLayoutViewModel implements LayoutViewModel {
	protected final ApplicationModel applicationModel;
	protected final LayoutBodyFunction layoutBodyFunction;
	protected final LayoutHeadersFunction layoutHeadersFunction;
	protected final Object bodyModel;
	protected final Object layoutModel;
	protected final String bodyModelAsJSON;
	protected final Mustache.Lambda i18nLambda;

	@Override
	public ApplicationModel getApplicationModel() {
		return applicationModel;
	}

	@Override
	public String getBody() {
		if (layoutBodyFunction == null) {
			throw new RuntimeException("{{body}} is only valid from within a Layout.");
		}
		return layoutBodyFunction.getBody();
	}

	@Override
	public String getHeaders() {
		if (layoutHeadersFunction == null) {
			throw new RuntimeException("{{headers}} is only valid from within a Layout.");
		}
		return layoutHeadersFunction.getHeaders();
	}

	@Override
	public Object getBodyModel() {
		return bodyModel;
	}

	protected LayoutBodyFunction getLayoutBodyFunction() {
		return layoutBodyFunction;
	}

	protected LayoutHeadersFunction getLayoutHeadersFunction() {
		return layoutHeadersFunction;
	}

	@Override
	public String getBodyModelAsJSON() {
		return bodyModelAsJSON;
	}

	@Override
	public Object getLayoutModel() {
		return layoutModel;
	}

	public Mustache.Lambda getI18n() {
		return i18nLambda;
	}

	public Mustache.Lambda getM() {
		return i18nLambda;
	}

	public DefaultLayoutViewModel(ApplicationModel applicationModel, Object bodyModel, String bodyModelAsJSON, Object layoutModel, LayoutBodyFunction layoutBodyFunction, LayoutHeadersFunction layoutHeadersFunction, Mustache.Lambda i18nLambda) {
		super();
		this.applicationModel = applicationModel;
		this.bodyModel = bodyModel;
		this.bodyModelAsJSON = bodyModelAsJSON;
		this.layoutModel = layoutModel;
		this.layoutBodyFunction = layoutBodyFunction;
		this.layoutHeadersFunction = layoutHeadersFunction;
		this.i18nLambda = i18nLambda;
	}
}
