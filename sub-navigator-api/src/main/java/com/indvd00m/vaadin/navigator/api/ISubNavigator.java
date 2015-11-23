package com.indvd00m.vaadin.navigator.api;

import java.util.List;

import com.indvd00m.vaadin.navigator.api.event.IViewStatusChangeListener;
import com.vaadin.navigator.NavigationStateManager;
import com.vaadin.navigator.Navigator;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Nov 19, 2015 2:31:20 PM
 *
 */
public interface ISubNavigator {

	boolean addView(ISubContainer container, ISubView view);

	boolean contains(ISubView view);

	boolean removeView(ISubContainer container);

	boolean removeView(ISubView view);

	ViewStatus getViewStatus(ISubView view);

	void rebuild(ISubView view);

	/**
	 * View selected not by navigator.
	 */
	void notifySelectedChangeDirected(ISubContainer container);

	void navigateTo(String path);

	void navigateTo(ISubContainer container, String relativePath);

	List<ISubView> getSelectedPath(ISubContainer container);

	String getSelectedPath();

	ISubView getSelected();

	void setSelected(ISubView view);

	boolean isSelected(ISubView view);

	List<ISubView> getPathList(ISubView view);

	String getPath(ISubView view);

	String getURL(ISubView view);

	String getURL(String path);

	int getLevel(ISubView view);

	boolean isRoot(ISubView view);

	boolean isDebug();

	void setDebug(boolean debug);

	ISubContainer getRoot();

	void addViewStatusChangeListener(IViewStatusChangeListener listener);

	void removeViewStatusChangeListener(IViewStatusChangeListener listener);

	void addViewStatusChangeListener(ISubView view, IViewStatusChangeListener listener);

	void removeViewStatusChangeListener(ISubView view, IViewStatusChangeListener listener);

	Navigator getNavigator();

	NavigationStateManager getStateManager();

	List<ViewStatus> getViewStatusHistory(ISubView view);

	ISubContainer getContainer(ISubView view);

	List<ISubView> getSubViews(ISubContainer container);

	boolean isSubPath(String sourcePath, String testPath);

	boolean equalsPath(String path1, String path2);

	String trimDelimiter(String path);

	String trimDelimiterLeft(String path);

	String trimDelimiterRight(String path);

	HierarchyDirection getDirection(ISubView sourceView, ISubView targetView);

	HierarchyDirection getDirection(String sourcePath, String targetPath);

	ISubView getView(String path);

	List<String> getPathsBetween(String path1, String path2);

	String getDivergationPath(String path1, String path2);

	ISubView getDivergationNode(ISubView view1, ISubView view2);

	void setDelimiter(String delimiter);

	String getDelimiter();

	String getState();

}