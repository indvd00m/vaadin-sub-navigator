package com.indvd00m.vaadin.demo;

import com.github.peholmst.i18n4vaadin.annotations.Message;
import com.github.peholmst.i18n4vaadin.annotations.Messages;
import com.indvd00m.vaadin.navigator.LocalizableNavigatableDynamicContainer;
import com.indvd00m.vaadin.navigator.LocalizableNavigatableView;
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

@SuppressWarnings("serial")
public class Level2DynamicContainer1 extends LocalizableNavigatableDynamicContainer {

	SimpleView selectedView;
	boolean autoRemove = false;

	Label info;
	TextField id;
	Button button;

	Level2DynamicContainer1Bundle l10n = new Level2DynamicContainer1Bundle();

	@Override
	protected LocalizableNavigatableView createView(String viewName) {
		SimpleView view = new SimpleView(viewName);
		return view;
	}

	@Override
	protected LocalizableNavigatableView getSelectedView() {
		return selectedView;
	}

	@Override
	protected void setSelectedView(LocalizableNavigatableView view) {
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
		return "dynamic-container";
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
		id.setValue("123");
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
