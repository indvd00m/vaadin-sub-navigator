package com.indvd00m.vaadin.demo;

import com.github.peholmst.i18n4vaadin.annotations.Message;
import com.github.peholmst.i18n4vaadin.annotations.Messages;
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
	SimpleViewBundle l10n = new SimpleViewBundle();

	public SimpleView(String viewName) {
		this.viewName = viewName;
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

		addComponent(new Label(viewName));
	}

	@Messages({
			@Message(key = "view1", value = "View 1"),
			@Message(key = "view2", value = "View 2"),
			@Message(key = "view3", value = "View 3"),
			@Message(key = "view4", value = "View 4"),
			@Message(key = "view5", value = "View 5"),
	})
	@Override
	protected void localize() {
		((Label) getComponent(0)).setValue(l10n.getMessage(viewName));
	}

}