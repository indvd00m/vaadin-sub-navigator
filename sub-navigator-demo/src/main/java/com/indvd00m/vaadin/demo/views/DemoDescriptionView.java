package com.indvd00m.vaadin.demo.views;

import java.util.List;

import com.indvd00m.vaadin.demo.SubNavigatorUI;
import com.indvd00m.vaadin.navigator.api.ISubNavigator;
import com.indvd00m.vaadin.navigator.api.view.ISubContainer;
import com.indvd00m.vaadin.navigator.api.view.ISubView;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Nov 23, 2015 5:33:44 PM
 *
 */
@SuppressWarnings("serial")
public class DemoDescriptionView extends VerticalLayout implements ISubView {

	ISubNavigator subNavigator;

	@Override
	public String getRelativePath() {
		return "description";
	}

	@Override
	public void clean() {
		removeAllComponents();
	}

	@Override
	public void build() {
		subNavigator = ((SubNavigatorUI) getUI()).getSubNavigator();

		setSpacing(true);
		setMargin(true);

		String info = "Try to navigate by mouse clicking, back/forward browser buttons, reloading (F5) or manual url editing.";
		Label label = new Label(info);
		addComponent(label);

		ISubContainer root = subNavigator.getRoot();
		List<ISubView> subViews = subNavigator.getSubViews(root);
		ISubView infoView = subViews.get(0);
		String url = subNavigator.getURL(infoView);

		Link link = new Link("Show info", new ExternalResource(url));
		addComponent(link);

		Link failLink = new Link("Link to not existed view", new ExternalResource(subNavigator.getURL("not/existed/view")));
		addComponent(failLink);
	}

}
