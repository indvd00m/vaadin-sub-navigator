package com.indvd00m.vaadin.demo;

import com.indvd00m.vaadin.navigator.SubView;
import com.vaadin.ui.Label;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Nov 16, 2015 2:14:09 PM
 *
 */
@SuppressWarnings("serial")
public class SimpleView extends SubView {

	String viewName;
	String label;

	public SimpleView(String viewName) {
		this.viewName = viewName;
		this.label = viewName;
	}

	public SimpleView(String viewName, String label) {
		this.viewName = viewName;
		this.label = label;
	}

	@Override
	protected void clean() {
		removeAllComponents();
	}

	@Override
	public String getViewName() {
		return viewName;
	}

	@Override
	protected void build() {
		setSpacing(true);
		setMargin(true);

		addComponent(new Label(label));
	}

}