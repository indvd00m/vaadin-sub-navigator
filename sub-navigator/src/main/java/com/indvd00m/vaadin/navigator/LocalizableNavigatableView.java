package com.indvd00m.vaadin.navigator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.UI;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Nov 5, 2015 4:13:46 PM
 *
 */
@SuppressWarnings("serial")
public abstract class LocalizableNavigatableView extends LocalizableVL implements View {

	LocalizableNavigatableContainer viewContainer;

	protected abstract void clean();

	public abstract String getViewName();

	public String getFullPath() {
		if (viewContainer == null)
			return getViewName();
		return viewContainer.getPath(this);
	}

	public List<LocalizableNavigatableView> getFullPathElements() {
		List<LocalizableNavigatableView> path = new ArrayList<LocalizableNavigatableView>();
		path.add(this);
		LocalizableNavigatableContainer parent = getViewContainer();
		while (parent != null) {
			path.add(parent);
			parent = parent.getViewContainer();
		}
		Collections.reverse(path);
		return path;
	}

	public boolean isSelected() {
		if (viewContainer == null)
			return true;
		List<LocalizableNavigatableView> pathElements = getFullPathElements();
		for (int i = 0; i < pathElements.size(); i++) {
			LocalizableNavigatableView pathElement = pathElements.get(i);
			LocalizableNavigatableView nextElement = null;
			if (i + 1 < pathElements.size())
				nextElement = pathElements.get(i + 1);
			if (nextElement != null) {
				if (pathElement instanceof LocalizableNavigatableContainer) {
					LocalizableNavigatableContainer container = (LocalizableNavigatableContainer) pathElement;
					if (container.getSelectedView() != nextElement) {
						return false;
					}
				}
			}
		}
		return true;
	}

	@Override
	protected void onAttach() {
		// nothing to do
	}

	@Override
	public void enter(ViewChangeEvent event) {
		rebuild();
	}

	public void rebuild() {
		if (!isSelected())
			return;
		clean();
		viewState = ViewState.Cleaned;
		build();
		viewState = ViewState.Builded;
		localize();
		viewState = ViewState.Localized;
	}

	public LocalizableNavigatableContainer getViewContainer() {
		return viewContainer;
	}

	public void setViewContainer(LocalizableNavigatableContainer viewContainer) {
		this.viewContainer = viewContainer;
	}

	protected Navigator getNavigator() {
		LocalizableNavigatableView view = this;
		UI ui = getUI();
		while (ui == null && view != null) {
			view = view.getViewContainer();
			ui = view.getUI();
		}
		return ui.getNavigator();
	}

}