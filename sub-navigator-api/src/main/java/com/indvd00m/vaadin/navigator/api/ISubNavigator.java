package com.indvd00m.vaadin.navigator.api;

import java.util.List;

import com.indvd00m.vaadin.navigator.api.event.IViewStatusChangeListener;
import com.vaadin.navigator.Navigator;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Nov 19, 2015 2:31:20 PM
 *
 */
public interface ISubNavigator {

	void register(ISubContainer container, ISubView view);

	void unregister(ISubContainer container, ISubView view);

	boolean registered(ISubView view);

	ViewStatus getViewStatus(ISubView view);

	void rebuild(ISubView view);

	boolean isSelected(ISubView view);

	List<ISubView> getPathList(ISubView view);

	String getPath(ISubView view);

	int getLevel(ISubView view);

	boolean isRoot(ISubView view);

	/**
	 * View selected not by navigator.
	 */
	void selectedViewChangeDirected(ISubContainer container);

	List<ISubView> getSelectedPath(ISubContainer container);

	ISubView getSelected();

	boolean isDebug();

	void setDebug(boolean debug);

	ISubContainer getRootContainer();

	void addViewStatusChangeListener(IViewStatusChangeListener listener);

	void removeViewStatusChangeListener(IViewStatusChangeListener listener);

	Navigator getNavigator();

	List<ViewStatus> getViewStatusHistory(ISubView view);

}