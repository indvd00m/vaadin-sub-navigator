package com.indvd00m.vaadin.navigator.api;

import com.vaadin.ui.Component;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Nov 17, 2015 5:29:05 PM
 *
 */
public interface ISubView extends Component {

	String getViewName();

	void clean();

	void build();

}
