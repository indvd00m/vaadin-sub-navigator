package com.indvd00m.vaadin.demo.views;

import com.indvd00m.vaadin.demo.SubNavigatorUI;
import com.indvd00m.vaadin.navigator.api.ISubNavigator;
import com.indvd00m.vaadin.navigator.api.view.ISubView;
import com.vaadin.ui.VerticalLayout;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Dec 7, 2015 7:15:46 PM
 *
 */
@SuppressWarnings("serial")
public class RedirectView extends VerticalLayout implements ISubView {

	ISubNavigator subNavigator;
	String redirect = null;
	String relativePath = null;

	public RedirectView(String redirect, String relativePath) {
		super();
		this.redirect = redirect;
		this.relativePath = relativePath;
	}

	@Override
	public String getRelativePath() {
		return relativePath;
	}

	@Override
	public void clean() {

	}

	@Override
	public void build() {
		subNavigator = ((SubNavigatorUI) getUI()).getSubNavigator();
		subNavigator.navigateTo(redirect);
	}

}
