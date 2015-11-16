package com.indvd00m.vaadin.demo;

import com.github.peholmst.i18n4vaadin.annotations.Message;
import com.github.peholmst.i18n4vaadin.annotations.Messages;
import com.indvd00m.vaadin.navigator.LocalizableNavigatableTabContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Nov 16, 2015 2:12:11 PM
 *
 */
@SuppressWarnings("serial")
public class Level1Container extends LocalizableNavigatableTabContainer {

	TabSheet ts;
	Level1ContainerBundle l10n = new Level1ContainerBundle();

	@Override
	public TabSheet getTabSheet() {
		return ts;
	}

	@Override
	protected void clean() {
		removeAllComponents();
	}

	@Override
	public String getViewName() {
		return "level1";
	}

	@Override
	protected void build() {
		setMargin(true);
		setSpacing(true);
		setSizeFull();

		ts = new TabSheet();
		ts.setImmediate(true);
		ts.setSizeFull();
		addComponent(ts);
		setExpandRatio(ts, 1f);

		addView(new Level2Container1(), FontAwesome.LEVEL_UP);
		addView(new Level2DynamicContainer1(), FontAwesome.LEVEL_DOWN);
	}

	@Messages({
			@Message(key = "Level1Container", value = "Level 1 container"),
			@Message(key = "Level2Container1", value = "Level 2 container 1"),
			@Message(key = "Level2DynamicContainer1", value = "Level 2 dynamic container 2"),
	})
	@Override
	protected void localize() {
		ts.setCaption(l10n.Level1Container());
		for (int i = 0; i < ts.getComponentCount(); i++) {
			Tab tab = ts.getTab(i);
			if (tab == null)
				continue;
			Component component = tab.getComponent();
			if (component == null)
				continue;
			tab.setCaption(l10n.getMessage(component.getClass().getSimpleName()));
		}
	}

}
