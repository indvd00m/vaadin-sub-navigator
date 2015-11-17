package com.indvd00m.vaadin.demo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.annotation.WebServlet;

import com.indvd00m.vaadin.navigator.SubView;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.navigator.ViewProvider;
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

	List<SubView> listenedViews = new ArrayList<SubView>();

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = SubNavigatorUI.class)
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {
		Level1Container root = new Level1Container();
		root.addSubViewStateChangeListener(root);
		Navigator navigator = new Navigator(this, (ViewDisplay) root);
		setNavigator(navigator);
		setContent(root);
		navigator.addProvider((ViewProvider) root);
		navigator.addView("", root);
		navigator.addView(root.getViewName(), root);
	}

	@Override
	public void detach() {
		Level1Container root = (Level1Container) getContent();
		for (SubView view : listenedViews) {
			view.removeSubViewStateChangeListener(root);
		}
		listenedViews.clear();
		super.detach();
	}

	public <V extends SubView> V registerListener(V view) {
		Level1Container root = (Level1Container) getContent();
		view.addSubViewStateChangeListener(root);
		listenedViews.add(view);
		return view;
	}

}
