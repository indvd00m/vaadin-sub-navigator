package com.indvd00m.vaadin.navigator.holder;

import com.indvd00m.vaadin.navigator.api.view.ISubDynamicContainer;
import com.indvd00m.vaadin.navigator.api.view.ISubView;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Nov 22, 2015 8:44:38 PM
 *
 */
@SuppressWarnings("serial")
public class DynamicContainerHolder extends ContainerHolder {

	public DynamicContainerHolder(ISubDynamicContainer container) {
		super(container);
	}

	@Override
	public ISubDynamicContainer getView() {
		return (ISubDynamicContainer) view;
	}

	public ISubView createView(String viewPathAndParameters) {
		ISubView created = null;
		if (isBuilt()) {
			created = getView().createView(viewPathAndParameters);
		} else {
			String containerName = getView().getRelativePath();
			throw new IllegalStateException(
					String.format("Trying to create view \"%s\" on dynamic container \"%s\" which not built yet", viewPathAndParameters, containerName));
		}
		return created;
	}

}
