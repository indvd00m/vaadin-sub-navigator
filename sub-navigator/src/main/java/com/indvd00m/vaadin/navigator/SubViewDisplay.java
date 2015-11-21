package com.indvd00m.vaadin.navigator;

import com.indvd00m.vaadin.navigator.SubNavigator.ViewHolder;
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

		boolean changed = false;
		if (!subNavigator.processing) {
			subNavigator.processing = true;
			changed = true;
		}
		try {
			ViewHolder viewHolder = (ViewHolder) view;
			subNavigator.showView(viewHolder);
		} finally {
			if (changed) {
				subNavigator.processing = false;
				changed = false;
			}
		}
	}

}
