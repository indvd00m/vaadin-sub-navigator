package com.indvd00m.vaadin.navigator.holder;

import com.indvd00m.vaadin.navigator.api.ISubErrorContainer;
import com.indvd00m.vaadin.navigator.api.ISubView;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Nov 23, 2015 8:51:35 PM
 *
 */
@SuppressWarnings("serial")
public class ErrorContainerHolder extends ContainerHolder {

	public ErrorContainerHolder(ISubErrorContainer container) {
		super(container);
	}

	@Override
	public ISubErrorContainer getView() {
		return (ISubErrorContainer) view;
	}

	public ISubView createErrorView(String viewPath, String errorPath) {
		ISubView created = null;
		if (isBuilt()) {
			created = getView().createErrorView(viewPath, errorPath);
		} else {
			String containerName = getView().getRelativePath();
			throw new IllegalStateException(
					String.format("Trying to create view \"%s\" on error container \"%s\" which not built yet", viewPath, containerName));
		}
		return created;
	}

}
