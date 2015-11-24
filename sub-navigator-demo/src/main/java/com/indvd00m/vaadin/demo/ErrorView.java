package com.indvd00m.vaadin.demo;

import com.indvd00m.vaadin.navigator.api.ISubNavigator;
import com.indvd00m.vaadin.navigator.api.view.ISubContainer;
import com.indvd00m.vaadin.navigator.api.view.ISubTitled;
import com.indvd00m.vaadin.navigator.api.view.ISubView;
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
public class ErrorView extends VerticalLayout implements ISubView, ISubTitled {

	ISubNavigator subNavigator;

	String caption;
	String viewPath;
	String errorMessage;

	public ErrorView(String viewPath, String errorPath) {
		this.caption = "404 Not Found";
		this.viewPath = viewPath;
		this.errorMessage = "Path: " + errorPath;
	}

	@Override
	public String getRelativePath() {
		return viewPath;
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

		Label captionLabel = new Label(caption);
		captionLabel.setSizeUndefined();
		vl.addComponent(captionLabel);
		vl.setComponentAlignment(captionLabel, Alignment.MIDDLE_CENTER);

		ISubContainer container = subNavigator.getContainer(this);
		String containerPath = subNavigator.getPath(container);
		Label handlerLabel = new Label("Handled by: \"" + containerPath + "\"");
		handlerLabel.setSizeUndefined();
		vl.addComponent(handlerLabel);
		vl.setComponentAlignment(handlerLabel, Alignment.MIDDLE_CENTER);

		Label urlLabel = new Label(errorMessage);
		urlLabel.setSizeUndefined();
		vl.addComponent(urlLabel);
		vl.setComponentAlignment(urlLabel, Alignment.MIDDLE_CENTER);

		Link link = new Link("Home", new ExternalResource("./"));
		vl.addComponent(link);
		vl.setComponentAlignment(link, Alignment.MIDDLE_CENTER);
	}

	@Override
	public String getRelativeTitle() {
		return "Error: " + caption;
	}

}
