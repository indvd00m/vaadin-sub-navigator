package com.indvd00m.vaadin.demo;

import java.util.List;

import com.indvd00m.vaadin.navigator.api.ISubContainer;
import com.indvd00m.vaadin.navigator.api.ISubNavigator;
import com.indvd00m.vaadin.navigator.api.ISubView;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Nov 23, 2015 5:33:44 PM
 *
 */
@SuppressWarnings("serial")
public class InfoView extends VerticalLayout implements ISubView {

	ISubNavigator subNavigator;

	@Override
	public String getRelativePath() {
		return "info";
	}

	@Override
	public void clean() {
		removeAllComponents();
	}

	@Override
	public void build() {
		subNavigator = ((SubNavigatorUI) getUI()).getSubNavigator();

		setSizeFull();
		setSpacing(true);
		setMargin(true);

		VerticalLayout vl = new VerticalLayout();
		vl.setSpacing(true);
		vl.setMargin(true);
		vl.setSizeUndefined();
		addComponent(vl);
		setComponentAlignment(vl, Alignment.MIDDLE_CENTER);

		String info = "SubNavigator Add-on for Vaadin 7";
		Label label = new Label(info);
		vl.addComponent(label);
		vl.setComponentAlignment(label, Alignment.MIDDLE_CENTER);

		ISubContainer root = subNavigator.getRoot();
		List<ISubView> subViews = subNavigator.getSubViews(root);
		ISubView demoView = subViews.get(1);
		String url = subNavigator.getURL(demoView);

		Link link = new Link("Show demo", new ExternalResource(url));
		vl.addComponent(link);
		vl.setComponentAlignment(link, Alignment.MIDDLE_CENTER);
	}

}
