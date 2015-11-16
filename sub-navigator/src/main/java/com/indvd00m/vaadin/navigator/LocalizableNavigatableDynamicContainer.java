package com.indvd00m.vaadin.navigator;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Oct 27, 2015 7:57:52 PM
 *
 */
@SuppressWarnings("serial")
public abstract class LocalizableNavigatableDynamicContainer extends LocalizableNavigatableContainer {

	Map<String, LocalizableNavigatableView> views = new HashMap<String, LocalizableNavigatableView>();

	protected abstract LocalizableNavigatableView createView(String viewName);

	protected Map<String, LocalizableNavigatableView> getViews() {
		return views;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		setSelectedView(null);
		super.enter(event);
	}

	public void addView(LocalizableNavigatableView view) {
		views.clear();
		view.setViewContainer(this);
		views.put(view.getFullPath(), view);
	}

	@Override
	public void showView(View view) {
		super.showView(view);
		views.clear();
	}

	@Override
	public View getView(String state) {
		View view = super.getView(state);
		if (view == this) {
			String path = getPath(this);
			if (state == null || state.trim().isEmpty())
				return null;
			if (!isSubPath(state, path))
				return null;
			if (equalsPath(state, path))
				return this;
			String subPath = trimDivider(state.replaceFirst("\\Q" + path + "\\E", ""));
			String viewName = subPath.replaceAll("/+.*", "");
			LocalizableNavigatableView subView = createView(viewName);
			if (subView == null)
				return null;
			addView(subView);
			if (viewState != ViewState.Attached) {
				if (isSelected())
					return getView(state);
			}
		}
		return view;
	}

}
