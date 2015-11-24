package com.indvd00m.vaadin.demo.views;

import com.indvd00m.vaadin.navigator.api.view.ISubView;
import com.vaadin.ui.VerticalLayout;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Nov 24, 2015 4:52:09 PM
 *
 */
@SuppressWarnings("serial")
public class NPEView extends VerticalLayout implements ISubView {

	@Override
	public String getRelativePath() {
		return "npe";
	}

	@Override
	public void clean() {

	}

	@Override
	public void build() {
		throw new NullPointerException("Error while build this view!");
	}

}
