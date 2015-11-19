package com.indvd00m.vaadin.demo;

import com.indvd00m.vaadin.navigator.api.ISubView;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Nov 16, 2015 2:14:09 PM
 *
 */
@SuppressWarnings("serial")
public class SimpleView extends VerticalLayout implements ISubView {

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
	public void clean() {
		removeAllComponents();
	}

	@Override
	public String getViewName() {
		return viewName;
	}

	@Override
	public void build() {
		setSpacing(true);
		setMargin(true);

		addComponent(new Label(label));
	}

}