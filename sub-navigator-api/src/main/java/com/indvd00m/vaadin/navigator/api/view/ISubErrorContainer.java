package com.indvd00m.vaadin.navigator.api.view;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Nov 23, 2015 8:42:48 PM
 *
 */
public interface ISubErrorContainer extends ISubContainer {

	ISubView createErrorView(String viewPath, String errorPath);

}
