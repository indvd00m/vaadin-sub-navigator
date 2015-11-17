package com.indvd00m.vaadin.navigator;

import java.util.ArrayList;
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

	protected boolean showingView = false;

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

	protected void restate() {
		Navigator navigator = getNavigator();
		String state = navigator.getState();
		String path = getPath(this);
		Map<String, SubView> viewsByState = getViews();

		if (equalsPath(state, path)) {
			if (viewsByState.isEmpty()) {
				return;
			} else {
				String viewPath = (String) viewsByState.keySet().toArray()[0];
				navigator.navigateTo(viewPath);
				return;
			}
		}

		if (isSubPath(path, state)) {
			// goto next level
			if (viewsByState.isEmpty()) {
				navigator.navigateTo(path);
				return;
			} else {
				String viewPath = (String) viewsByState.keySet().toArray()[0];
				navigator.navigateTo(viewPath);
				return;
			}
		}

		if (isSubPath(state, path)) {
			// restate after build
			if (viewsByState.isEmpty()) {
				navigator.navigateTo(state);
				return;
			} else {
				for (String viewPath : viewsByState.keySet()) {
					if (isSubPath(state, viewPath)) {
						navigator.navigateTo(state);
						return;
					}
				}
			}
		}
		throw new IllegalArgumentException(
				"Trying to navigate to an unknown state '" + state + "' and an error view provider not present");
	}

	/**
	 * View selected not by navigator.
	 */
	public void selectedViewChangeDirected() {
		Navigator navigator = getNavigator();
		SubView selectedView = getSelectedView();
		String path = getPath(selectedView);
		String state = navigator.getState();
		if (showingView)
			return;
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
		boolean changedShowingView = false;
		if (!showingView) {
			showingView = true;
			changedShowingView = true;
		}

		if (view == this) {

		} else {
			Navigator navigator = getNavigator();
			String state = navigator.getState();
			Map<String, SubView> viewsByState = getViews();

			if (viewsByState.values().contains(view)) {
				SubView subView = (SubView) view;
				List<SubView> pathElements = subView.getPath();
				for (SubView pathElement : pathElements) {
					if (!pathElement.isSelected()) {
						SubContainer container = pathElement.getViewContainer();
						if (container != null) {
							container.deselect(container);
							container.setSelectedView(pathElement);
						}
					}
				}
				if (subView instanceof SubContainer) {
					SubContainer subContainer = (SubContainer) subView;
					subContainer.deselect(subContainer);
				}
			} else {
				String path = getFullPath();
				if (isSubPath(state, path)) {
					for (Entry<String, SubView> e : viewsByState.entrySet()) {
						SubView subView = e.getValue();
						String subPath = e.getKey();
						if (isSubPath(state, subPath)) {
							if (subView instanceof ViewDisplay) {
								((ViewDisplay) subView).showView(view);
							}
						}
					}
				}
			}
		}

		if (changedShowingView) {
			showingView = false;
			changedShowingView = false;
		}
	}

	protected void deselect(SubContainer container) {
		List<SubView> selectedPath = container.getSelectedPath();
		for (int i = selectedPath.size() - 1; i >= 0; i--) {
			SubView subView = selectedPath.get(i);
			if (subView instanceof SubContainer) {
				SubContainer subContainer = (SubContainer) subView;
				subContainer.deselect(subContainer);
			}
		}
	}

	protected List<SubView> getSelectedPath() {
		List<SubView> path = new ArrayList<SubView>();
		SubView selectedView = getSelectedView();
		while (selectedView != null) {
			path.add(selectedView);
			if (selectedView instanceof SubContainer) {
				SubContainer container = (SubContainer) selectedView;
				selectedView = container.getSelectedView();
			} else {
				break;
			}
		}
		return path;
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
	public View getView(String state) {
		for (Entry<String, SubView> e : getViews().entrySet()) {
			String path = e.getKey();
			SubView view = e.getValue();
			if (equalsPath(state, path)) {
				return view;
			}
			if (isSubPath(state, path)) {
				if (view instanceof SubContainer) {
					SubContainer container = (SubContainer) view;
					return container.getView(state);
				}
				return view;
			}
		}
		String path = getPath(this);
		if (isSubPath(state, path)) {
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
