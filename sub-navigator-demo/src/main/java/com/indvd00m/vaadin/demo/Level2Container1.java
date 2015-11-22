package com.indvd00m.vaadin.demo;

import com.indvd00m.vaadin.navigator.api.ISubContainer;
import com.indvd00m.vaadin.navigator.api.ISubNavigator;
import com.indvd00m.vaadin.navigator.api.ISubView;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.VerticalLayout;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Nov 16, 2015 2:12:03 PM
 *
 */
@SuppressWarnings("serial")
public class Level2Container1 extends VerticalLayout implements ISubContainer, SelectedTabChangeListener {

	protected ISubNavigator subNavigator;
	protected Accordion accord;

	@Override
	public void clean() {
		removeAllComponents();
	}

	@Override
	public String getRelativePath() {
		return "level2";
	}

	@Override
	public void build() {
		subNavigator = ((SubNavigatorUI) getUI()).getSubNavigator();

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

		accord.addSelectedTabChangeListener(this);
	}

	void addView(ISubView view, String caption, Resource icon) {
		subNavigator.addView(this, view);
		accord.addTab(view, caption, icon);
	}

	@Override
	public ISubView getSelectedView() {
		return (ISubView) accord.getSelectedTab();
	}

	@Override
	public void setSelectedView(ISubView view) {
		accord.setSelectedTab(view);
	}

	@Override
	public void selectedTabChange(SelectedTabChangeEvent event) {
		subNavigator.notifySelectedChangeDirected(this);
	}

}
