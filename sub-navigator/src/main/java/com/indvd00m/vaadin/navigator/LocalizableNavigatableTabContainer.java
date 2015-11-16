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
public abstract class LocalizableNavigatableTabContainer extends LocalizableNavigatableContainer
		implements SelectedTabChangeListener {

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

	protected void addView(LocalizableNavigatableView view, Resource icon) {
		addView(view);
		getTabSheet().addTab(view, "", icon);
	}

	@Override
	protected LocalizableNavigatableView getSelectedView() {
		return (LocalizableNavigatableView) getTabSheet().getSelectedTab();
	}

	@Override
	protected void setSelectedView(LocalizableNavigatableView view) {
		getTabSheet().setSelectedTab(view);
	}

}
