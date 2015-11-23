package com.indvd00m.vaadin.demo;

import javax.servlet.annotation.WebServlet;

import com.indvd00m.vaadin.navigator.SubNavigator;
import com.indvd00m.vaadin.navigator.api.ISubNavigator;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Nov 16, 2015 8:39:09 PM
 *
 */
@Theme("valo")
@Title("SubNavigator Add-on Demo")
@SuppressWarnings("serial")
public class SubNavigatorUI extends UI {
	
	// TODO: status history as description
	// TODO: main view with info
	// TODO: error page
	// TODO: link to unknown url

	ISubNavigator subNavigator;

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = SubNavigatorUI.class)
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {
		Level1Container root = new Level1Container();
		subNavigator = new SubNavigator(this, root, root, true);
		setContent(root);
	}

	public ISubNavigator getSubNavigator() {
		return subNavigator;
	}

}
