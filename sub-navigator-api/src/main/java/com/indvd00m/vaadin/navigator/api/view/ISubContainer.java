package com.indvd00m.vaadin.navigator.api.view;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Nov 17, 2015 5:34:40 PM
 *
 */
public interface ISubContainer extends ISubView {

	ISubView getSelectedView();

	void setSelectedView(ISubView view);

	void deselectView(ISubView view);

}
