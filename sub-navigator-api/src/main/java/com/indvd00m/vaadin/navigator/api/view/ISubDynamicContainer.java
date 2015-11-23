package com.indvd00m.vaadin.navigator.api.view;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Nov 17, 2015 5:37:25 PM
 *
 */
public interface ISubDynamicContainer extends ISubContainer {

	ISubView createView(String viewPathAndParameters);

}
