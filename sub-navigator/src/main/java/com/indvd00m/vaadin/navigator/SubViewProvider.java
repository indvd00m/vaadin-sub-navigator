package com.indvd00m.vaadin.navigator;

import com.indvd00m.vaadin.navigator.api.ISubContainer;
import com.indvd00m.vaadin.navigator.holder.ContainerHolder;
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
		ISubContainer root = subNavigator.getRoot();
		String path = subNavigator.getPath(root);
		if (subNavigator.isSubPath(viewAndParameters, path)) {
			return viewAndParameters;
		}
		if (viewAndParameters.isEmpty())
			return path;
		return null;
	}

	@Override
	public View getView(String viewName) {
		boolean changed = false;
		if (!subNavigator.processing) {
			subNavigator.processing = true;
			changed = true;
		}
		try {
			subNavigator.closeDynamicallyCreatedViews(viewName);
			ISubContainer root = subNavigator.getRoot();
			ContainerHolder rootHolder = subNavigator.getHolder(root);
			return subNavigator.getView(rootHolder, viewName);
		} finally {
			if (changed) {
				subNavigator.processing = false;
				changed = false;
			}
		}
	}

}
