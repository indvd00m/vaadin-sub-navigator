package com.indvd00m.vaadin.demo;

import com.indvd00m.vaadin.demo.loggable.LTabSubContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.TabSheet;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Nov 16, 2015 2:12:03 PM
 *
 */
@SuppressWarnings("serial")
public class Level2Container1 extends LTabSubContainer {

	protected Accordion accord;

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

	@Override
	protected void build() {
		setSizeFull();
		setSpacing(true);
		setMargin(true);

		accord = new Accordion();
		accord.setCaption("Level 2 container");
		accord.setSizeFull();
		accord.setImmediate(true);
		addComponent(accord);
		setExpandRatio(accord, 1f);

		addView(new AnotherContainer("level3_1"), "Level 3 container 1", FontAwesome.ANDROID);
		addView(new AnotherContainer("level3_2"), "Level 3 container 2", FontAwesome.APPLE);
	}

}
