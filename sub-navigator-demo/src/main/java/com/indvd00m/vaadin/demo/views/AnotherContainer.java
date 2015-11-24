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
		final NPEView npeView = new NPEView();

		subNavigator.addView(this, v1);
		subNavigator.addView(this, v2);
		subNavigator.addView(this, v3);
		subNavigator.addView(this, npeView);

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

		Button b2 = new Button("Button 2 (navigator selecting)");
		b2.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				String path = subNavigator.getPath(v2);
				getUI().getNavigator().navigateTo(path);
			}

		});
		addComponent(b2);

		Button b3 = new Button("Button 3 (sub-navigator selecting)");
		b3.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				subNavigator.setSelected(v3);
			}

		});
		addComponent(b3);

		Button b4 = new Button("Button 4 (not existed view)");
		b4.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				subNavigator.navigateTo(thisView, "this/view/not/exist");
			}

		});
		addComponent(b4);

		Button b5 = new Button("Button 5 (view throw exception in build phase)");
		b5.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				subNavigator.setSelected(npeView);
			}

		});
		addComponent(b5);

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
