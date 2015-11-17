package com.indvd00m.vaadin.navigator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.navigator.View;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Oct 27, 2015 7:57:52 PM
 *
 */
@SuppressWarnings("serial")
public abstract class DynamicSubContainer extends SubContainer {

	Map<String, SubView> views = new HashMap<String, SubView>();

	protected abstract SubView createView(String viewName);

	protected Map<String, SubView> getViews() {
		return views;
	}

	@Override
	public void addView(SubView view) {
		if (!views.isEmpty())
			throw new IllegalStateException(getClass().getSimpleName() + " can contain only one element!");
		view.setViewContainer(this);
		views.put(view.getFullPath(), view);
	}

	@Override
	public void showView(View view) {
		super.showView(view);
		SubView subView = (SubView) view;
		String subPath = subView.getFullPath();
		String state = getNavigator().getState();
		if (equalsPath(state, subPath)) {
			List<SubView> path = subView.getPath();
			for (int i = path.size() - 1; i >= 0; i--) {
				SubView pathElement = path.get(i);
				if (pathElement instanceof DynamicSubContainer) {
					DynamicSubContainer dynamicContainer = (DynamicSubContainer) pathElement;
					dynamicContainer.views.clear();
				}
			}
		}
	}

	@Override
	protected void deselect(SubContainer container) {
		SubView selectedView = container.getSelectedView();
		super.deselect(container);
		if (selectedView != null) {
			selectedView.setViewContainer(null);
			container.setSelectedView(null);
		}
	}

	@Override
	public View getView(String state) {
		View view = super.getView(state);
		if (view == this) {
			if (!isSelected())
				return this;
			String path = getPath(this);
			if (equalsPath(state, path))
				return this;
			String subPath = trimDivider(state.replaceFirst("\\Q" + path + "\\E", ""));
			String viewName = subPath.replaceAll("/+.*", "");
			SubView subView = createView(viewName);
			if (subView == null)
				return null;
			addView(subView);
			String fullSubPath = subView.getFullPath();
			if (!isSubPath(state, fullSubPath))
				return null;
		}
		return view;
	}

}
