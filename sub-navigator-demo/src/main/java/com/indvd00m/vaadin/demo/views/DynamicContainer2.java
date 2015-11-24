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
 * @date Nov 16, 2015 8:39:01 PM
 *
 */
@SuppressWarnings("serial")
public class DynamicContainer2 extends VerticalLayout implements ISubDynamicContainer, CloseListener {

	protected ISubNavigator subNavigator;
	DynamicContainer1 selectedView;

	Label info;
	TextField id;
	Button button;

	DynamicContainer2 thisView = this;

	@Override
	public ISubView createView(String viewPathAndParameters) {
		if (!viewPathAndParameters.matches("\\d+"))
			return null;
		DynamicContainer1 view = new DynamicContainer1(viewPathAndParameters);
		return view;
	}

	@Override
	public ISubView getSelectedView() {
		return selectedView;
	}

	@Override
	public void setSelectedView(ISubView view) {
		selectedView = (DynamicContainer1) view;
		Window window = new Window();
		window.setModal(false);
		window.setWidth(300, Unit.PIXELS);
		window.setHeight(500, Unit.PIXELS);
		window.setContent(selectedView);
		window.setCaption("Dynamically created window");
		window.addCloseListener(this);
		getUI().addWindow(window);
	}

	@Override
	public void deselectView(ISubView view) {
		Window window = (Window) selectedView.getParent();
		window.removeCloseListener(this);
		window.close();
		selectedView = null;
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
		return "dc2";
	}

	@Override
	public void build() {
		subNavigator = ((SubNavigatorUI) getUI()).getSubNavigator();

		setSizeUndefined();
		setSpacing(true);
		setMargin(true);

		info = new Label("This is dynamic container");
		addComponent(info);

		id = new TextField("Enter object id");
		id.setConverter(new StringToIntegerConverter());
		id.setValue("456");
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
