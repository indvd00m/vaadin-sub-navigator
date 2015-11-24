package com.indvd00m.vaadin.demo.views;

import com.indvd00m.vaadin.demo.SubNavigatorUI;
import com.indvd00m.vaadin.navigator.api.ISubNavigator;
import com.indvd00m.vaadin.navigator.api.view.ISubDynamicContainer;
import com.indvd00m.vaadin.navigator.api.view.ISubView;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Nov 16, 2015 8:38:24 PM
 *
 */
@SuppressWarnings("serial")
public class DynamicContainer3 extends VerticalLayout implements ISubDynamicContainer, CloseListener {

	protected ISubNavigator subNavigator;
	String viewName;
	AnotherContainer selectedView;
	DynamicContainer3 thisView = this;

	Label info;
	TextField id;
	Button button;

	public DynamicContainer3() {
		this("dc3");
	}

	public DynamicContainer3(String viewName) {
		this.viewName = viewName;
	}

	@Override
	public ISubView createView(String viewPathAndParameters) {
		if (!viewPathAndParameters.matches("\\d+"))
			return null;
		AnotherContainer view = new AnotherContainer(viewPathAndParameters);
		return view;
	}

	@Override
	public ISubView getSelectedView() {
		return selectedView;
	}

	@Override
	public void setSelectedView(ISubView view) {
		if (view == null) {
			Window window = (Window) selectedView.getParent();
			window.removeCloseListener(this);
			window.close();
			selectedView = null;
		} else {
			selectedView = (AnotherContainer) view;
			Window window = new Window();
			window.setModal(true);
			window.setWidth(300, Unit.PIXELS);
			window.setHeight(500, Unit.PIXELS);
			window.setContent(selectedView);
			window.setCaption("Dynamically created window");
			window.addCloseListener(this);
			getUI().addWindow(window);
		}
	}

	@Override
	public void windowClose(CloseEvent e) {
		selectedView = null;
		subNavigator.notifySelectedChangeDirected(thisView);
	}

	@Override
	public void clean() {
		removeAllComponents();
	}

	@Override
	public String getRelativePath() {
		return viewName;
	}

	@Override
	public void build() {
		subNavigator = ((SubNavigatorUI) getUI()).getSubNavigator();

		setSizeUndefined();
		setSpacing(true);
		setMargin(true);

		info = new Label("This is dynamic container with name " + viewName);
		addComponent(info);

		id = new TextField("Enter object id");
		id.setConverter(new StringToIntegerConverter());
		id.setValue("789");
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
				if (id.isValid()) {
					String sId = id.getValue().replaceAll("\\D+", "");
					subNavigator.navigateTo(thisView, sId);
				}
			}
		});
		addComponent(button);
	}

}
