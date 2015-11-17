package com.indvd00m.vaadin.demo;

import com.github.peholmst.i18n4vaadin.annotations.Message;
import com.github.peholmst.i18n4vaadin.annotations.Messages;
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
 * @date Nov 16, 2015 8:39:01 PM
 *
 */
@SuppressWarnings("serial")
public class Level2DynamicContainer2 extends LDynamicSubContainer {

	Level2DynamicContainer1 selectedView;
	boolean autoRemove = false;

	Label info;
	TextField id;
	Button button;

	Level2DynamicContainer2Bundle l10n = new Level2DynamicContainer2Bundle();

	@Override
	protected SubView createView(String viewName) {
		if (!viewName.matches("\\d+"))
			return null;
		Level2DynamicContainer1 view = new Level2DynamicContainer1(viewName);
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
		if (view instanceof Level2DynamicContainer1) {
			selectedView = (Level2DynamicContainer1) view;
			Window window = new Window();
			window.setModal(false);
			window.setWidth(300, Unit.PIXELS);
			window.setHeight(500, Unit.PIXELS);
			window.setContent(selectedView);
			window.setCaption(l10n.window());
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
		return "dynamic-container2";
	}

	@Override
	protected void build() {
		setSizeUndefined();
		setSpacing(true);
		setMargin(true);

		info = new Label();
		addComponent(info);

		id = new TextField();
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

		button = new Button();
		button.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if (id.isValid())
					getNavigator().navigateTo(getFullPath() + "/" + id.getValue().replaceAll("\\D+", ""));
			}
		});
		addComponent(button);
	}

	@Messages({
			@Message(key = "info", value = "This is dynamic container"),
			@Message(key = "id", value = "Enter object id"),
			@Message(key = "button", value = "Click to open object"),
			@Message(key = "window", value = "Dynamically created window"),
	})
	@Override
	protected void localize() {
		info.setValue(l10n.info());
		id.setCaption(l10n.id());
		button.setCaption(l10n.button());
	}

}
