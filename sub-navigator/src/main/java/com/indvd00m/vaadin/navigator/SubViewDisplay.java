package com.indvd00m.vaadin.navigator;

import com.indvd00m.vaadin.navigator.SubNavigator.ContainerHolder;
import com.indvd00m.vaadin.navigator.SubNavigator.ViewHolder;
import com.indvd00m.vaadin.navigator.api.ISubContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Nov 19, 2015 2:10:04 PM
 *
 */
@SuppressWarnings("serial")
public class SubViewDisplay implements ViewDisplay {

	SubNavigator subNavigator;

	public SubViewDisplay(SubNavigator subNavigator) {
		this.subNavigator = subNavigator;
	}

	@Override
	public void showView(View view) {
		if (!(view instanceof ViewHolder))
			return;
		ViewHolder viewHolder = (ViewHolder) view;
		subNavigator.showingView = true;
		try {
			ISubContainer rootContainer = subNavigator.getRootContainer();
			ContainerHolder rootHolder = subNavigator.getHolder(rootContainer);
			subNavigator.showView(rootHolder, viewHolder);
		} finally {
			subNavigator.showingView = false;
		}
	}

}
