package com.indvd00m.vaadin.demo.views;

import com.indvd00m.vaadin.demo.ErrorView;
import com.indvd00m.vaadin.demo.SubNavigatorUI;
import com.indvd00m.vaadin.navigator.api.ISubNavigator;
import com.indvd00m.vaadin.navigator.api.view.ISubContainer;
import com.indvd00m.vaadin.navigator.api.view.ISubErrorContainer;
import com.indvd00m.vaadin.navigator.api.view.ISubView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.VerticalLayout;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Nov 16, 2015 2:11:59 PM
 *
 */
@SuppressWarnings("serial")
public class AnotherContainer extends VerticalLayout implements ISubContainer, ISubErrorContainer {

	ISubNavigator subNavigator;
	String containerName = "test-containers";
	AnotherContainer thisView = this;

	VerticalLayout container;

	public AnotherContainer() {
		// TODO Auto-generated constructor stub
	}

	public AnotherContainer(String name) {
		this.containerName = name;
	}

	@Override
	public ISubView getSelectedView() {
		return (ISubView) container.getComponent(0);
	}

	@Override
	public void setSelectedView(ISubView view) {
		container.addComponent(view);
	}

	@Override
	public void deselectView(ISubView view) {
		container.removeComponent(view);
	}

	@Override
	public void clean() {
		container.removeAllComponents();
		removeAllComponents();
	}

	@Override
	public String getRelativePath() {
		return containerName;
	}

	@Override
	public void build() {
		subNavigator = ((SubNavigatorUI) getUI()).getSubNavigator();

		final SimpleView v1 = new SimpleView("view1", "View 1");
		final SimpleView v2 = new SimpleView("view2", "View 2");
		final SimpleView v3 = new SimpleView("view3", "View 3");
		final SimpleView v4 = new SimpleView("view4", "View 4");
		final SimpleView v5 = new SimpleView("view5", "View 5");

		subNavigator.addView(this, v1);
		subNavigator.addView(this, v2);
		subNavigator.addView(this, v3);
		subNavigator.addView(this, v4);
		subNavigator.addView(this, v5);

		Button b1 = new Button("Button 1 (direct selecting)");
		b1.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				deselectView(getSelectedView());
				setSelectedView(v1);
				subNavigator.notifySelectedChangeDirected(thisView);
			}

		});
		addComponent(b1);

		Button b2 = new Button("Button 2 (direct selecting)");
		b2.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				deselectView(getSelectedView());
				setSelectedView(v2);
				subNavigator.notifySelectedChangeDirected(thisView);
			}

		});
		addComponent(b2);

		Button b3 = new Button("Button 3 (direct selecting)");
		b3.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				deselectView(getSelectedView());
				setSelectedView(v3);
				subNavigator.notifySelectedChangeDirected(thisView);
			}

		});
		addComponent(b3);

		Button b4 = new Button("Button 4 (navigator selecting)");
		b4.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				String path = subNavigator.getPath(v4);
				getUI().getNavigator().navigateTo(path);
			}

		});
		addComponent(b4);

		Button b5 = new Button("Button 5 (sub-navigator selecting)");
		b5.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				subNavigator.setSelected(v5);
			}

		});
		addComponent(b5);

		Button b6 = new Button("Button 6 (not existed view)");
		b6.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				subNavigator.navigateTo(thisView, "this/view/not/exist");
			}

		});
		addComponent(b6);

		container = new VerticalLayout();
		container.setSizeFull();
		addComponent(container);
	}

	@Override
	public ISubView createErrorView(String viewPath, String errorPath) {
		return new ErrorView(viewPath, errorPath);
	}

	@Override
	public ISubView createErrorView(String viewPath, Throwable t) {
		return null;
	}

}
