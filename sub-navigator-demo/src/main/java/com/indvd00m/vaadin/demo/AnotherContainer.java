package com.indvd00m.vaadin.demo;

import com.indvd00m.vaadin.demo.loggable.LSubContainer;
import com.indvd00m.vaadin.navigator.SubView;
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
public class AnotherContainer extends LSubContainer {

	String containerName = "test-containers";

	VerticalLayout container;

	public AnotherContainer() {
		// TODO Auto-generated constructor stub
	}

	public AnotherContainer(String name) {
		this.containerName = name;
	}

	@Override
	protected SubView getSelectedView() {
		if (container == null || container.getComponentCount() == 0)
			return null;
		return (SubView) container.getComponent(0);
	}

	@Override
	protected void setSelectedView(SubView view) {
		container.removeAllComponents();
		container.addComponent(view);
	}

	@Override
	protected void clean() {
		removeAllComponents();
	}

	@Override
	public String getViewName() {
		return containerName;
	}

	SimpleView v1;
	SimpleView v2;
	SimpleView v3;
	SimpleView v4;
	SimpleView v5;

	Button b1;
	Button b2;
	Button b3;
	Button b4;
	Button b5;

	@Override
	protected void build() {
		v1 = new SimpleView("view1", "View 1");
		v2 = new SimpleView("view2", "View 2");
		v3 = new SimpleView("view3", "View 3");
		v4 = new SimpleView("view4", "View 4");
		v5 = new SimpleView("view5", "View 5");

		addView(v1);
		addView(v2);
		addView(v3);
		addView(v4);
		addView(v5);

		b1 = new Button("Button 1 (direct selecting)");
		b1.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				setSelectedView(v1);
				selectedViewChangeDirected();
			}

		});
		addComponent(b1);

		b2 = new Button("Button 2 (direct selecting)");
		b2.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				setSelectedView(v2);
				selectedViewChangeDirected();
			}

		});
		addComponent(b2);

		b3 = new Button("Button 3 (direct selecting)");
		b3.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				setSelectedView(v3);
				selectedViewChangeDirected();
			}

		});
		addComponent(b3);

		b4 = new Button("Button 4 (navigator selecting)");
		b4.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				getNavigator().navigateTo(v4.getFullPath());
			}

		});
		addComponent(b4);

		b5 = new Button("Button 5 (navigator selecting)");
		b5.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				getNavigator().navigateTo(v5.getFullPath());
			}

		});
		addComponent(b5);

		container = new VerticalLayout();
		container.setSizeFull();
		addComponent(container);
	}

}
