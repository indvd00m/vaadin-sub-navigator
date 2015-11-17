package com.indvd00m.vaadin.demo;

import com.indvd00m.vaadin.demo.loggable.LDynamicSubContainer;
import com.indvd00m.vaadin.navigator.SubView;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Nov 16, 2015 8:38:24 PM
 *
 */
@SuppressWarnings("serial")
public class Level2DynamicContainer1 extends LDynamicSubContainer {

	String viewName;

	SimpleView selectedView;
	boolean autoRemove = false;

	Label info;
	TextField id;
	Button button;

	public Level2DynamicContainer1() {
		this("dynamic-container");
	}

	public Level2DynamicContainer1(String viewName) {
		this.viewName = viewName;
	}

	@Override
	protected SubView createView(String viewName) {
		if (!viewName.matches("\\d+"))
			return null;
		SimpleView view = new SimpleView(viewName);
		return view;
	}

	@Override
	protected SubView getSelectedView() {
		return selectedView;
	}

	@Override
	protected void setSelectedView(SubView view) {
		if (selectedView != view) {
			if (selectedView != null) {
				autoRemove = true;
				((Window) selectedView.getParent()).close();
				selectedView = null;
				autoRemove = false;
			}
		}
		if (view instanceof SimpleView) {
			selectedView = (SimpleView) view;
			Window window = new Window();
			window.setModal(true);
			window.setWidth(300, Unit.PIXELS);
			window.setHeight(500, Unit.PIXELS);
			window.setContent(selectedView);
			window.setCaption("Dynamically created window");
			window.addCloseListener(new CloseListener() {

				@Override
				public void windowClose(CloseEvent e) {
					selectedView = null;
					if (!autoRemove)
						selectedViewChangeDirected();
				}

			});
			getUI().addWindow(window);
		}
	}

	@Override
	protected void clean() {
		removeAllComponents();
	}

	@Override
	public String getViewName() {
		return viewName;
	}

	@Override
	protected void build() {
		setSizeUndefined();
		setSpacing(true);
		setMargin(true);

		info = new Label("This is dynamic container with name " + viewName);
		addComponent(info);

		id = new TextField("Enter object id");
		id.setConverter(new StringToIntegerConverter());
		id.setValue("123");
		id.setImmediate(true);
		id.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				button.setEnabled(id.isValid());
			}
		});
		addComponent(id);

		button = new Button("Click to open object");
		button.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (id.isValid())
					getNavigator().navigateTo(getFullPath() + "/" + id.getValue().replaceAll("\\D+", ""));
			}
		});
		addComponent(button);
	}

}
