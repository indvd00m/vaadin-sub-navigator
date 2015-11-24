package com.indvd00m.vaadin.demo;

import javax.servlet.annotation.WebServlet;

import com.indvd00m.vaadin.navigator.SubNavigator;
import com.indvd00m.vaadin.navigator.api.ISubNavigator;
import com.indvd00m.vaadin.navigator.api.view.ISubErrorContainer;
import com.indvd00m.vaadin.navigator.api.view.ISubTitled;
import com.indvd00m.vaadin.navigator.api.view.ISubView;
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
public class SubNavigatorUI extends UI implements ISubErrorContainer, ISubTitled {

	ISubNavigator subNavigator;

	DemoContainer demoView;
	InfoView infoView;

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = SubNavigatorUI.class)
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {
		demoView = new DemoContainer();
		infoView = new InfoView();
		subNavigator = new SubNavigator(this, this, demoView, true);
		subNavigator.addView(this, infoView);
		subNavigator.addView(this, demoView);
		subNavigator.setEnabledSubTitles(true);
	}

	public ISubNavigator getSubNavigator() {
		return subNavigator;
	}

	@Override
	public String getRelativePath() {
		return "";
	}

	@Override
	public void clean() {
		setContent(null);
	}

	@Override
	public void build() {

	}

	@Override
	public ISubView getSelectedView() {
		return (ISubView) getContent();
	}

	@Override
	public void setSelectedView(ISubView view) {
		setContent(view);
	}

	@Override
	public ISubView createErrorView(String viewPath, String errorPath) {
		return new ErrorView(viewPath, errorPath);
	}

	@Override
	public String getRelativeTitle() {
		return "SubNavigator Add-on";
	}

}
