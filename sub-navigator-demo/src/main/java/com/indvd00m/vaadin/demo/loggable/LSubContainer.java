package com.indvd00m.vaadin.demo.loggable;

import com.indvd00m.vaadin.demo.SubNavigatorUI;
import com.indvd00m.vaadin.navigator.SubContainer;
import com.indvd00m.vaadin.navigator.SubView;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Nov 17, 2015 1:39:46 AM
 *
 */
@SuppressWarnings("serial")
public abstract class LSubContainer extends SubContainer {

	@Override
	public void addView(SubView view) {
		super.addView(view);
		((SubNavigatorUI) getUI()).registerListener(view);
	}

}
