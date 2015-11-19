package com.indvd00m.vaadin.navigator;

import com.indvd00m.vaadin.navigator.SubNavigator.ContainerHolder;
import com.indvd00m.vaadin.navigator.api.ISubContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewProvider;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Nov 19, 2015 2:15:59 PM
 *
 */
@SuppressWarnings("serial")
public class SubViewProvider implements ViewProvider {

	SubNavigator subNavigator;

	public SubViewProvider(SubNavigator subNavigator) {
		this.subNavigator = subNavigator;
	}

	@Override
	public String getViewName(String viewAndParameters) {
		if (viewAndParameters == null) {
			return null;
		}
		ISubContainer rootContainer = subNavigator.getRootContainer();
		String path = subNavigator.getPath(rootContainer);
		if (subNavigator.isSubPath(viewAndParameters, path)) {
			return viewAndParameters;
		}
		if (viewAndParameters.isEmpty())
			return path;
		return null;
	}

	@Override
	public View getView(String viewName) {
		ISubContainer rootContainer = subNavigator.getRootContainer();
		ContainerHolder rootHolder = subNavigator.getHolder(rootContainer);
		return subNavigator.getView(rootHolder, viewName);
	}

}
