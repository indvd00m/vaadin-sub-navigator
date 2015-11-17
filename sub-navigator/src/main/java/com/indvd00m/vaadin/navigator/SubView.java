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
public abstract class SubView extends LocalizableView implements View {

	SubContainer viewContainer;

	protected abstract void clean();

	public abstract String getViewName();

	public String getFullPath() {
		if (viewContainer == null)
			return getViewName();
		return viewContainer.getPath(this);
	}

	public int getLevel() {
		return getPath().size();
	}

	public List<SubView> getPath() {
		List<SubView> path = new ArrayList<SubView>();
		path.add(this);
		SubContainer parent = getViewContainer();
		while (parent != null) {
			path.add(parent);
			parent = parent.getViewContainer();
		}
		Collections.reverse(path);
		return path;
	}

	public boolean isRoot() {
		return getLevel() == 1;
	}

	public boolean isSelected() {
		if (viewContainer == null)
			return true;
		List<SubView> pathElements = getPath();
		for (int i = 0; i < pathElements.size(); i++) {
			SubView pathElement = pathElements.get(i);
			SubView nextElement = null;
			if (i + 1 < pathElements.size())
				nextElement = pathElements.get(i + 1);
			if (nextElement != null) {
				if (pathElement instanceof SubContainer) {
					SubContainer container = (SubContainer) pathElement;
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
		setViewState(ViewState.Cleaned);
		build();
		setViewState(ViewState.Builded);
		localize();
		setViewState(ViewState.Localized);
	}

	public SubContainer getViewContainer() {
		return viewContainer;
	}

	public void setViewContainer(SubContainer viewContainer) {
		this.viewContainer = viewContainer;
	}

	protected Navigator getNavigator() {
		SubView view = this;
		UI ui = getUI();
		while (ui == null && view != null) {
			view = view.getViewContainer();
			ui = view.getUI();
		}
		return ui.getNavigator();
	}

}