package com.indvd00m.vaadin.navigator;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Resource;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Oct 27, 2015 7:57:52 PM
 *
 */
@SuppressWarnings("serial")
public abstract class TabSubContainer extends SubContainer implements SelectedTabChangeListener {

	abstract public TabSheet getTabSheet();

	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
		if (!getTabSheet().getListeners(SelectedTabChangeEvent.class).contains(this))
			getTabSheet().addSelectedTabChangeListener(this);
	}

	@Override
	public void selectedTabChange(SelectedTabChangeEvent event) {
		selectedViewChangeDirected();
	}

	protected void addView(SubView view, String caption, Resource icon) {
		addView(view);
		getTabSheet().addTab(view, caption, icon);
	}

	protected void addView(SubView view, Resource icon) {
		addView(view, "", icon);
	}

	@Override
	protected SubView getSelectedView() {
		TabSheet ts = getTabSheet();
		if (ts != null)
			return (SubView) ts.getSelectedTab();
		return null;
	}

	@Override
	protected void setSelectedView(SubView view) {
		getTabSheet().setSelectedTab(view);
	}

}
