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

	boolean registered(ISubView view);

	void unregister(ISubContainer container);

	void unregister(ISubView view);

	ViewStatus getViewStatus(ISubView view);

	void rebuild(ISubView view);

	/**
	 * View selected not by navigator.
	 */
	void notifySelectedChangeDirected(ISubContainer container);

	List<ISubView> getSelectedPath(ISubContainer container);

	ISubView getSelected();

	void setSelected(ISubView view);

	boolean isSelected(ISubView view);

	List<ISubView> getPathList(ISubView view);

	String getPath(ISubView view);

	int getLevel(ISubView view);

	boolean isRoot(ISubView view);

	boolean isDebug();

	void setDebug(boolean debug);

	ISubContainer getRootContainer();

	void addViewStatusChangeListener(IViewStatusChangeListener listener);

	void removeViewStatusChangeListener(IViewStatusChangeListener listener);

	Navigator getNavigator();

	List<ViewStatus> getViewStatusHistory(ISubView view);

	ISubContainer getContainer(ISubView view);

	List<ISubView> getSubViews(ISubContainer container);

	boolean isSubPath(String sourcePath, String testPath);

	boolean equalsPath(String path1, String path2);

	String trimDivider(String path);

	String trimDividerLeft(String path);

	String trimDividerRight(String path);

	HierarchyDirection getDirection(ISubView sourceView, ISubView targetView);

	HierarchyDirection getDirection(String sourcePath, String targetPath);

	ISubView getView(String path);

	List<String> getNodesBetween(String path1, String path2);

	String getDivergationNode(String path1, String path2);

	ISubView getDivergationNode(ISubView view1, ISubView view2);

}