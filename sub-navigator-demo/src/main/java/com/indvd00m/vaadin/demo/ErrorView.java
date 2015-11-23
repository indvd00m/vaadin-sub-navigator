package com.indvd00m.vaadin.demo;

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
public class ErrorView extends VerticalLayout implements ISubView {

	String viewPath;
	String errorPath;

	public ErrorView(String viewPath, String errorPath) {
		this.viewPath = viewPath;
		this.errorPath = errorPath;
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
		setSizeFull();
		setSpacing(true);
		setMargin(true);

		VerticalLayout vl = new VerticalLayout();
		vl.setSpacing(true);
		vl.setMargin(true);
		vl.setSizeUndefined();
		addComponent(vl);
		setComponentAlignment(vl, Alignment.MIDDLE_CENTER);

		String info = "404 Not Found";
		Label infoLabel = new Label(info);
		infoLabel.setSizeUndefined();
		vl.addComponent(infoLabel);
		vl.setComponentAlignment(infoLabel, Alignment.MIDDLE_CENTER);

		Label urlLabel = new Label(errorPath);
		urlLabel.setSizeUndefined();
		vl.addComponent(urlLabel);
		vl.setComponentAlignment(urlLabel, Alignment.MIDDLE_CENTER);

		Link link = new Link("Home", new ExternalResource("./"));
		vl.addComponent(link);
		vl.setComponentAlignment(link, Alignment.MIDDLE_CENTER);
	}

}
