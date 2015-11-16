package com.indvd00m.vaadin.demo;

import com.github.peholmst.i18n4vaadin.annotations.Message;
import com.github.peholmst.i18n4vaadin.annotations.Messages;
import com.indvd00m.vaadin.navigator.LocalizableNavigatableTabContainer;
import com.indvd00m.vaadin.navigator.LocalizableNavigatableView;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Nov 16, 2015 2:12:03 PM
 *
 */
@SuppressWarnings("serial")
public class Level2Container1 extends LocalizableNavigatableTabContainer {

	protected Accordion accord;
	Level2Container1Bundle l10n = new Level2Container1Bundle();

	@Override
	public TabSheet getTabSheet() {
		return accord;
	}

	@Override
	protected void clean() {
		removeAllComponents();
	}

	@Override
	public String getViewName() {
		return "level2";
	}

	@Messages({

	})
	@Override
	protected void build() {
		setSizeFull();
		setSpacing(true);
		setMargin(true);

		accord = new Accordion();
		accord.setSizeFull();
		accord.setImmediate(true);
		addComponent(accord);
		setExpandRatio(accord, 1f);

		addView(new AnotherContainer("level3_1"), FontAwesome.ANDROID);
		addView(new AnotherContainer("level3_2"), FontAwesome.APPLE);
	}

	@Messages({
			@Message(key = "Level2Container", value = "Level 2 container"),
			@Message(key = "level3_1", value = "Level 3 container 1"),
			@Message(key = "level3_2", value = "Level 3 container 2"),
	})
	@Override
	protected void localize() {
		accord.setCaption(l10n.Level2Container());
		for (int i = 0; i < accord.getComponentCount(); i++) {
			Tab tab = accord.getTab(i);
			if (tab == null)
				continue;
			Component component = tab.getComponent();
			if (!(component instanceof LocalizableNavigatableView))
				continue;
			LocalizableNavigatableView view = (LocalizableNavigatableView) component;
			tab.setCaption(l10n.getMessage(view.getViewName()));
		}
	}

}
