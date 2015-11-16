package com.indvd00m.vaadin.navigator;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.navigator.ViewProvider;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Oct 27, 2015 7:57:52 PM
 *
 */
@SuppressWarnings("serial")
public abstract class SubContainer extends SubView implements ViewDisplay, ViewProvider {

	private Map<String, SubView> views = new LinkedHashMap<String, SubView>();

	protected abstract SubView getSelectedView();

	protected abstract void setSelectedView(SubView view);

	protected Map<String, SubView> getViews() {
		return views;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
		restate();
	}

	@Override
	public void detach() {
		super.detach();
	}

	private void restate() {
		Navigator navigator = getNavigator();
		String state = navigator.getState();
		String path = getPath(this);
		Map<String, SubView> viewsByState = getViews();
		if (viewsByState.isEmpty() && equalsPath(state, path))
			return;
		if (isSubPath(path, state)) {
			// goto next level
			String viewPath = (String) viewsByState.keySet().toArray()[0];
			navigator.navigateTo(viewPath);
			return;
		}
		if (isSubPath(state, path)) {
			// restate after build
			for (String viewPath : viewsByState.keySet()) {
				if (isSubPath(state, viewPath)) {
					navigator.navigateTo(state);
					return;
				}
			}
			throw new IllegalArgumentException(
					"Trying to navigate to an unknown state '" + state + "' and an error view provider not present");
		}
	}

	/**
	 * View selected not by navigator.
	 */
	public void selectedViewChangeDirected() {
		Navigator navigator = getNavigator();
		SubView selectedView = getSelectedView();
		String path = getPath(selectedView);
		String state = navigator.getState();
		if (!equalsPath(state, path)) {
			if (isSelected())
				navigator.navigateTo(path);
		}
	}

	public void addView(SubView view) {
		view.setViewContainer(this);
		Navigator navigator = getNavigator();
		String viewPath = getPath(view);
		navigator.addView(viewPath, view);
		getViews().put(viewPath, view);
	}

	String getPath(SubView view) {
		String thisViewName = getViewName();
		if (viewContainer != null) {
			thisViewName = viewContainer.getPath(this);
		}
		if (view == null)
			return thisViewName;
		if (view == this)
			return thisViewName;
		return thisViewName + "/" + view.getViewName();
	}

	@Override
	public void showView(View view) {
		if (view == this) {

		} else {
			Navigator navigator = getNavigator();
			String state = navigator.getState();
			Map<String, SubView> viewsByState = getViews();

			if (viewsByState.values().contains(view)) {
				SubView lnView = (SubView) view;
				List<SubView> pathElements = lnView.getFullPathElements();
				for (SubView pathElement : pathElements) {
					if (!pathElement.isSelected()) {
						SubContainer container = pathElement.getViewContainer();
						if (container != null)
							container.setSelectedView(pathElement);
					}
				}
			} else {
				String path = getFullPath();
				if (isSubPath(state, path)) {
					for (Entry<String, SubView> e : viewsByState.entrySet()) {
						SubView lnView = e.getValue();
						if (lnView instanceof ViewDisplay) {
							((ViewDisplay) lnView).showView(view);
						}
					}
				}
			}
		}
	}

	@Override
	public String getViewName(String navigationState) {
		if (null == navigationState) {
			return null;
		}
		String path = getPath(this);
		if (isSubPath(navigationState, path)) {
			return navigationState;
		}
		return null;
	}

	@Override
	public View getView(String navigationState) {
		for (Entry<String, SubView> e : getViews().entrySet()) {
			String path = e.getKey();
			SubView view = e.getValue();
			if (equalsPath(navigationState, path)) {
				return view;
			}
			if (isSubPath(navigationState, path)) {
				if (view instanceof SubContainer) {
					SubContainer container = (SubContainer) view;
					return container.getView(navigationState);
				}
				return view;
			}
		}
		String path = getPath(this);
		if (isSubPath(navigationState, path)) {
			return this;
		}
		return null;
	}

	protected String trimDividerRight(String path) {
		return path.replaceAll("/*$", "");
	}

	protected String trimDividerLeft(String path) {
		return path.replaceAll("^/*", "");
	}

	protected String trimDivider(String path) {
		return trimDividerLeft(trimDividerRight(path));
	}

	protected boolean equalsPath(String path1, String path2) {
		String trimmed1 = trimDivider(path1);
		String trimmed2 = trimDivider(path2);
		return trimmed1.equals(trimmed2);
	}

	protected boolean isSubPath(String sourcePath, String testPath) {
		if (equalsPath(sourcePath, testPath))
			return true;
		String trimmedSource = trimDivider(sourcePath);
		String trimmedTest = trimDivider(testPath);
		if (trimmedTest.isEmpty())
			return true;
		return trimmedSource.startsWith(trimmedTest + "/");
	}

}
