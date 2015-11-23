package com.indvd00m.vaadin.navigator.holder;

import java.util.LinkedHashMap;
import java.util.Map;

import com.indvd00m.vaadin.navigator.api.view.ISubContainer;
import com.indvd00m.vaadin.navigator.api.view.ISubView;
import com.indvd00m.vaadin.navigator.api.view.ViewStatus;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Nov 22, 2015 8:17:56 PM
 *
 */
@SuppressWarnings("serial")
public class ContainerHolder extends ViewHolder {

	boolean hasSelectedValue = false;
	Map<String, ISubView> views = new LinkedHashMap<String, ISubView>();

	public ContainerHolder(ISubContainer container) {
		super(container);
	}

	@Override
	public ISubContainer getView() {
		return (ISubContainer) view;
	}

	public Map<String, ISubView> getViews() {
		return views;
	}

	public ISubView getSelectedView() {
		ISubView selected = null;
		if (isBuilt() && isHasSelectedValue())
			selected = getView().getSelectedView();
		return selected;
	}

	public void setSelectedView(ISubView view) {
		if (isBuilt()) {
			if (getSelectedView() != view) {
				getView().setSelectedView(view);
				hasSelectedValue = view != null;
			}
		} else {
			hasSelectedValue = false;
			String containerName = getView().getRelativePath();
			String viewName = "null";
			if (view != null)
				viewName = view.getRelativePath();
			throw new IllegalStateException(String.format("Trying to select view \"%s\" on container \"%s\" which not built yet", viewName, containerName));
		}
	}

	public boolean isHasSelectedValue() {
		return hasSelectedValue;
	}

	@Override
	public void setViewStatus(ViewStatus viewStatus) {
		if (viewStatus == ViewStatus.Cleaned)
			hasSelectedValue = false;
		super.setViewStatus(viewStatus);
	}

}