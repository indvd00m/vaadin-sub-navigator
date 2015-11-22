package com.indvd00m.vaadin.navigator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.indvd00m.vaadin.navigator.api.HierarchyDirection;
import com.indvd00m.vaadin.navigator.api.ISubContainer;
import com.indvd00m.vaadin.navigator.api.ISubDynamicContainer;
import com.indvd00m.vaadin.navigator.api.ISubNavigator;
import com.indvd00m.vaadin.navigator.api.ISubView;
import com.indvd00m.vaadin.navigator.api.ViewStatus;
import com.indvd00m.vaadin.navigator.api.event.IViewStatusChangeListener;
import com.indvd00m.vaadin.navigator.holder.ContainerHolder;
import com.indvd00m.vaadin.navigator.holder.DynamicContainerHolder;
import com.indvd00m.vaadin.navigator.holder.ViewHolder;
import com.indvd00m.vaadin.navigator.status.ViewStatusDispatcher;
import com.indvd00m.vaadin.navigator.status.ViewStatusLogger;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.Navigator.UriFragmentManager;
import com.vaadin.navigator.View;
import com.vaadin.ui.UI;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Nov 17, 2015 5:40:41 PM
 *
 */
public class SubNavigator implements ISubNavigator {

	Navigator navigator;
	ISubContainer root;
	UriFragmentManager stateManager;
	String currentNavigationState;
	String delimiter = "/";
	Map<ISubView, ViewHolder> viewHolders = new HashMap<ISubView, ViewHolder>();
	boolean processing = false;
	boolean debug = false;
	ViewStatusLogger viewStatusLogger = new ViewStatusLogger(this);
	ViewStatusDispatcher viewStatusDispatcher = new ViewStatusDispatcher();
	SubViewDisplay viewDisplay = new SubViewDisplay(this);
	SubViewProvider viewProvider = new SubViewProvider(this);
	ISubView currentView = null;

	// TODO: hierarchical page title
	// TODO: deprecate double add of same view name

	public SubNavigator(UI ui, ISubContainer root) {
		this(ui, root, null);
	}

	public SubNavigator(UI ui, ISubContainer root, IViewStatusChangeListener listener) {
		this(ui, root, listener, false);
	}

	public SubNavigator(UI ui, ISubContainer root, IViewStatusChangeListener listener, boolean debug) {
		this.root = root;
		this.debug = debug;
		stateManager = new UriFragmentManager(ui.getPage());
		navigator = new Navigator(ui, stateManager, viewDisplay);
		navigator.addProvider(viewProvider);
		if (listener != null)
			addViewStatusChangeListener(listener);
		if (addView(root))
			setAddedStatus(root);
		currentNavigationState = stateManager.getState();
	}

	protected boolean addView(ISubView view) {
		if (contains(view))
			return false;
		checkRelativePath(view);
		ViewHolder holder = createHolder(view);
		viewHolders.put(view, holder);
		holder.addViewStatusChangeListener(viewStatusLogger);
		holder.addViewStatusChangeListener(viewStatusDispatcher);
		return true;
	}

	protected void setAddedStatus(ISubView view) {
		ViewHolder holder = getHolder(view);
		holder.setViewStatus(ViewStatus.Added);
	}

	@Override
	public void addView(ISubContainer container, ISubView view) {
		if (container instanceof ISubDynamicContainer) {
			ISubDynamicContainer dynamicContainer = (ISubDynamicContainer) container;
			DynamicContainerHolder dynamicContainerHolder = getHolder(dynamicContainer);
			if (dynamicContainerHolder != null) {
				for (ISubView subView : dynamicContainerHolder.getViews().values()) {
					ViewHolder holder = getHolder(subView);
					if (holder.isCreatedDynamically())
						throw new IllegalStateException(dynamicContainer.getClass().getSimpleName() + " can contain only one dynamically created element!");
				}
			}
		}
		if (contains(container) && contains(view))
			return;

		boolean containerContains = addView(container);
		boolean viewContains = addView(view);

		ContainerHolder containerHolder = getHolder(container);
		ViewHolder viewHolder = getHolder(view);

		viewHolder.setContainer(container);
		String viewPath = getPath(view);
		containerHolder.getViews().put(viewPath, view);

		if (containerContains)
			setAddedStatus(container);
		if (viewContains)
			setAddedStatus(view);
	}

	protected ViewHolder createHolder(ISubView view) {
		if (view instanceof ISubDynamicContainer) {
			ISubDynamicContainer dynamicContainer = (ISubDynamicContainer) view;
			return new DynamicContainerHolder(dynamicContainer);
		} else if (view instanceof ISubContainer) {
			ISubContainer container = (ISubContainer) view;
			return new ContainerHolder(container);
		}
		return new ViewHolder(view);
	}

	@Override
	public void removeView(ISubView view) {
		if (!contains(view))
			return;

		ViewHolder holder = getHolder(view);
		ISubContainer container = holder.getContainer();
		if (container != null) {
			ContainerHolder containerHolder = getHolder(container);
			String viewPath = getPath(view);
			if (containerHolder != null)
				containerHolder.getViews().remove(viewPath);
			holder.setContainer(null);
			if (container instanceof ISubDynamicContainer) {
				ISubDynamicContainer dynamicContainer = (ISubDynamicContainer) container;
				ContainerHolder dynamicContainerHolder = containerHolder;
				if (dynamicContainerHolder.getSelectedView() == view)
					deselect(dynamicContainer);
			}
		}

		if (!holder.isCleaned())
			clean(view);
		holder.setViewStatus(ViewStatus.Removed);
		holder.removeAllViewStatusChangeListeners();
		viewHolders.remove(view);
	}

	@Override
	public void removeView(ISubContainer container) {
		if (!contains(container))
			return;

		ContainerHolder containerHolder = getHolder(container);
		while (!containerHolder.getViews().isEmpty()) {
			ISubView subView = null;
			for (ISubView nextSubView : containerHolder.getViews().values()) {
				subView = nextSubView;
				break;
			}
			if (subView instanceof ISubContainer) {
				ISubContainer subContainer = (ISubContainer) subView;
				removeView(subContainer);
			} else {
				removeView(subView);
			}
		}
		removeView((ISubView) container);
	}

	protected ViewHolder getHolder(ISubView view) {
		return (ViewHolder) viewHolders.get(view);
	}

	protected ContainerHolder getHolder(ISubContainer container) {
		return (ContainerHolder) viewHolders.get(container);
	}

	protected DynamicContainerHolder getHolder(ISubDynamicContainer dynamicContainer) {
		return (DynamicContainerHolder) viewHolders.get(dynamicContainer);
	}

	@Override
	public void setSelected(ISubView view) {
		checkContains(view);

		if (!processing) {
			navigator.navigateTo(getPath(view));
		} else {
			ViewHolder holder = getHolder(view);
			if (!isSelected(view)) {
				ISubContainer container = holder.getContainer();
				deselect(container);
				select(view);
			}
			ISubView selected = getSelected();
			List<ISubView> selectedPathList = getPathList(selected);
			if (selectedPathList.contains(view)) {
				rebuild(selected);
			} else {
				rebuild(view);
			}
		}
	}

	@Override
	public void rebuild(ISubView view) {
		checkContains(view);
		if (!processing) {
			rebuildSimple(view);
		} else {
			if (currentView == null || !contains(currentView)) {
				rebuildDifferentPath(root, view);
			} else {
				rebuildDifferentPath(currentView, view);
			}
		}
	}

	protected void rebuildDifferentPath(ISubView oldView, ISubView newView) {
		if (!contains(oldView))
			return;
		if (!contains(newView))
			return;

		if (oldView == newView) {
			ViewHolder holder = getHolder(newView);
			if (!holder.isBuilt()) {
				rebuildSimple(newView);
			}
			return;
		}

		ISubView divergationNode = getDivergationNode(oldView, newView);

		List<ISubView> newPath = getPathList(newView);
		List<ISubView> toRebuildPath = new ArrayList<ISubView>();
		boolean needRebuild = false;
		for (ISubView view : newPath) {
			if (needRebuild)
				toRebuildPath.add(view);
			needRebuild |= view == divergationNode;
		}

		List<ISubView> oldPath = getPathList(oldView);
		List<ISubView> toCleanPath = new ArrayList<ISubView>();
		boolean needClean = false;
		for (ISubView view : oldPath) {
			if (needClean)
				toCleanPath.add(view);
			needClean |= view == divergationNode;
		}
		Collections.reverse(toCleanPath);

		for (ISubView view : toCleanPath) {
			ViewHolder holder = getHolder(view);
			if (!holder.isCleaned()) {
				clean(view);
			}
		}

		for (ISubView view : toRebuildPath) {
			ViewHolder holder = getHolder(view);
			if (!holder.isBuilt()) {
				rebuildSimple(view);
			}
		}
	}

	@Override
	public ISubView getDivergationNode(ISubView view1, ISubView view2) {
		if (!contains(view1))
			return null;
		if (!contains(view2))
			return null;

		List<ISubView> pathList1 = getPathList(view1);
		List<ISubView> pathList2 = getPathList(view2);
		if (pathList1.isEmpty())
			return null;
		if (pathList2.isEmpty())
			return null;
		if (pathList1.get(0) != root && pathList2.get(0) != root)
			return null;
		ISubView lastDivergationNode = root;
		int minSize = Math.min(pathList1.size(), pathList2.size());
		for (int i = 0; i < minSize; i++) {
			ISubView node1 = pathList1.get(i);
			ISubView node2 = pathList2.get(i);
			if (node1 == node2)
				lastDivergationNode = node1;
			else
				break;
		}

		return lastDivergationNode;
	}

	protected String quote(String s) {
		return "\\Q" + s + "\\E";
	}

	@Override
	public String getDivergationPath(String path1, String path2) {
		path1 = trimDelimiter(path1);
		path2 = trimDelimiter(path2);
		String[] pathElements1 = path1.split(quote(delimiter));
		String[] pathElements2 = path2.split(quote(delimiter));
		StringBuilder divergationNode = new StringBuilder("");
		int minSize = Math.min(pathElements1.length, pathElements2.length);
		for (int i = 0; i < minSize; i++) {
			String node1 = pathElements1[i];
			String node2 = pathElements2[i];
			if (node1.equals(node2))
				divergationNode.append(node1).append(delimiter);
			else
				break;
		}

		return trimDelimiter(divergationNode.toString());
	}

	@Override
	public List<String> getPathsBetween(String path1, String path2) {
		List<String> nodes = new ArrayList<String>();
		if (!isSubPath(path2, path1))
			return nodes;
		path1 = trimDelimiter(path1.replaceAll(quote(delimiter) + "+", delimiter));
		path2 = trimDelimiter(path2.replaceAll(quote(delimiter) + "+", delimiter));
		String remain = trimDelimiter(path2.replaceFirst(quote(path1), ""));
		String lastNode = path1;
		for (String node : remain.split(quote(delimiter))) {
			String path = lastNode + delimiter + node;
			nodes.add(path);
			lastNode = path;
		}
		return nodes;
	}

	protected void rebuildSimple(ISubView view) {
		if (!isSelected(view))
			return;
		clean(view);
		build(view);
	}

	protected void clean(ISubView view) {
		ViewHolder holder = getHolder(view);
		if (holder.isBuiltAtLeastOnce()) {
			view.clean();
			holder.setViewStatus(ViewStatus.Cleaned);
		}
	}

	protected void build(ISubView view) {
		ViewHolder holder = getHolder(view);
		view.build();
		holder.setViewStatus(ViewStatus.Built);
	}

	@Override
	public boolean isSelected(ISubView view) {
		checkContains(view);
		ViewHolder holder = getHolder(view);
		ISubContainer container = holder.getContainer();
		if (container == null)
			return true;
		List<ISubView> pathElements = getPathList(view);
		for (int i = 0; i < pathElements.size(); i++) {
			ISubView pathElement = pathElements.get(i);
			ISubView nextElement = null;
			if (i + 1 < pathElements.size())
				nextElement = pathElements.get(i + 1);
			if (nextElement != null) {
				if (pathElement instanceof ISubContainer) {
					ISubContainer pathContainer = (ISubContainer) pathElement;
					ContainerHolder pathContainerHolder = getHolder(pathContainer);
					if (pathContainerHolder.getSelectedView() != nextElement) {
						return false;
					}
				}
			}
		}
		return true;
	}

	@Override
	public List<ISubView> getPathList(ISubView view) {
		checkContains(view);
		ViewHolder holder = getHolder(view);
		List<ISubView> path = new ArrayList<ISubView>();
		path.add(view);
		ISubContainer parent = holder.getContainer();
		while (parent != null) {
			path.add(parent);
			ContainerHolder parentHolder = getHolder(parent);
			parent = parentHolder.getContainer();
		}
		Collections.reverse(path);
		return Collections.unmodifiableList(path);
	}

	@Override
	public String getPath(ISubView view) {
		checkContains(view);
		StringBuilder sb = new StringBuilder();
		List<ISubView> pathList = getPathList(view);
		for (ISubView pathElement : pathList) {
			sb.append(getRelativePath(pathElement)).append(delimiter);
		}
		String path = trimDelimiter(sb.toString());
		return path;
	}

	@Override
	public int getLevel(ISubView view) {
		checkContains(view);
		return getPathList(view).size();
	}

	@Override
	public boolean isRoot(ISubView view) {
		checkContains(view);
		return view == root;
	}

	@Override
	public void notifySelectedChangeDirected(ISubContainer container) {
		checkContains(container);
		if (processing)
			return;
		ContainerHolder containerHolder = getHolder(container);
		// call getSelectedView() direct from container, because of containerHolder don't know about direct selected
		// element
		ISubView selectedView = container.getSelectedView();
		containerHolder.setSelectedView(selectedView);
		if (selectedView == null)
			selectedView = container;
		String path = getPath(selectedView);
		if (!equalsPath(currentNavigationState, path)) {
			if (isSelected(container))
				navigator.navigateTo(path);
		}
	}

	@Override
	public ISubView getView(String path) {
		for (ISubView view : viewHolders.keySet()) {
			String viewPath = getPath(view);
			if (equalsPath(path, viewPath))
				return view;
		}
		return null;
	}

	protected void closeDynamicallyCreatedViews(String state) {
		String prevState = currentNavigationState;
		HierarchyDirection direction = getDirection(prevState, state);
		if (direction == HierarchyDirection.Nearby || direction == HierarchyDirection.Up) {
			String divergationPath = getDivergationPath(prevState, state);
			List<String> pathsBetween = getPathsBetween(divergationPath, prevState);
			List<ISubView> oldBranch = new ArrayList<ISubView>();
			for (String path : pathsBetween) {
				ISubView view = getView(path);
				if (view != null) {
					oldBranch.add(view);
				}
			}
			for (ISubView view : oldBranch) {
				ViewHolder holder = getHolder(view);
				if (holder.isCreatedDynamically()) {
					if (view instanceof ISubContainer) {
						ISubContainer container = (ISubContainer) view;
						removeView(container);
					} else {
						removeView(view);
					}
					break;
				}
			}
		}
	}

	protected View getView(ContainerHolder containerHolder, String state) {

		ISubContainer container = containerHolder.getView();
		String path = getPath(container);

		// first select and build container
		setSelected(container);

		// check this container
		if (equalsPath(state, path)) {
			List<ISubView> subViews = getSubViews(container);
			if (subViews.isEmpty()) {
				// view found - this container
				return containerHolder;
			} else {
				// if container contain at least one view, return it
				ISubView view = subViews.get(0);
				ViewHolder holder = getHolder(view);
				if (view instanceof ISubContainer) {
					ContainerHolder subContainerHolder = (ContainerHolder) holder;
					String viewPath = getPath(view);
					// this is container, redirect
					return getView(subContainerHolder, viewPath);
				} else {
					// view found
					return holder;
				}
			}
		}

		// try to find already added view
		for (Entry<String, ISubView> e : containerHolder.getViews().entrySet()) {
			String viewPath = e.getKey();
			ISubView view = e.getValue();
			ViewHolder holder = getHolder(view);
			if (equalsPath(state, viewPath)) {
				if (view instanceof ISubContainer) {
					ContainerHolder subContainerHolder = (ContainerHolder) holder;
					// this is container, redirect
					return getView(subContainerHolder, viewPath);
				} else {
					// view found
					return holder;
				}
			}
			if (isSubPath(state, viewPath)) {
				if (!(view instanceof ISubContainer))
					return null;
				ISubContainer subContainer = (ISubContainer) view;
				ContainerHolder subContainerHolder = getHolder(subContainer);
				// search in next level
				return getView(subContainerHolder, state);
			}
		}

		// we are at the end of hierarchy now
		// after selecting and building all hierarchy view not found yet
		if (isSubPath(state, path)) {
			if (container instanceof ISubDynamicContainer) {
				// this container can dynamically create views, trying it
				ISubDynamicContainer dynamicContainer = (ISubDynamicContainer) container;
				DynamicContainerHolder dynamicContainerHolder = (DynamicContainerHolder) containerHolder;
				String subPath = trimDelimiter(state.replaceFirst("\\Q" + path + "\\E", ""));
				String viewPath = subPath.replaceAll(quote(delimiter) + "+.*", "");
				ISubView subView = dynamicContainerHolder.createView(viewPath);
				if (subView != null) {
					if (!viewPath.equals(getRelativePath(subView)))
						throw new IllegalStateException(String.format("Name of created view \"%s\" must be \"%s\"", getRelativePath(subView), viewPath));
					addView(dynamicContainer, subView);
					getHolder(subView).setCreatedDynamically(true);
					setSelected(subView);
					// search in next level
					return getView(containerHolder, state);
				}
			}
		}

		// view for this state not found
		return null;
	}

	protected void showView(ViewHolder viewHolder) {
		ISubView view = viewHolder.getView();

		// selecting and building all hierarchy of view, which not selected and built yet
		setSelected(view);
		String selectedPath = getSelectedPath();

		currentView = view;
		if (!equalsPath(selectedPath, stateManager.getState())) {
			// set state to actual value
			stateManager.setState(selectedPath);
		}
		currentNavigationState = selectedPath;
	}

	protected void select(ISubView view) {
		List<ISubView> pathList = getPathList(view);
		for (ISubView pathElement : pathList) {
			ViewHolder holder = getHolder(pathElement);
			ISubContainer container = holder.getContainer();
			if (container != null) {
				ContainerHolder containerHolder = getHolder(container);
				ISubView selectedView = containerHolder.getSelectedView();
				if (selectedView != pathElement) {
					containerHolder.setSelectedView(pathElement);
				}
			}
		}
	}

	protected void deselect(ISubContainer container) {
		ContainerHolder containerHolder = getHolder(container);
		ISubView selectedView = containerHolder.getSelectedView();

		List<ISubView> selectedPath = getSelectedPath(container);
		for (int i = selectedPath.size() - 1; i >= 0; i--) {
			ISubView subView = selectedPath.get(i);
			if (subView instanceof ISubContainer) {
				ISubContainer subContainer = (ISubContainer) subView;
				deselect(subContainer);
			}
		}

		if (selectedView != null) {
			if (containerHolder.getViews().isEmpty()) {
				if (connected(container)) {
					containerHolder.setSelectedView(null);
				}
			}
			if (contains(selectedView)) {
				ViewHolder selectedHolder = getHolder(selectedView);
				if (selectedHolder.isCreatedDynamically()) {
					if (selectedView instanceof ISubContainer) {
						ISubContainer subContainer = (ISubContainer) selectedView;
						removeView(subContainer);
					} else {
						removeView(selectedView);
					}
				}
			}
		}
	}

	protected boolean connected(ISubView view) {
		ViewHolder holder = getHolder(view);
		ISubContainer container = holder.getContainer();
		if (container == null)
			return false;
		if (container == root)
			return true;
		return connected(container);
	}

	@Override
	public List<ISubView> getSelectedPath(ISubContainer container) {
		checkContains(container);
		List<ISubView> path = new ArrayList<ISubView>();
		ContainerHolder containerHolder = getHolder(container);
		ISubView selectedView = containerHolder.getSelectedView();
		while (selectedView != null) {
			path.add(selectedView);
			if (selectedView instanceof ISubContainer) {
				ISubContainer subContainer = (ISubContainer) selectedView;
				ContainerHolder subContainerHolder = getHolder(subContainer);
				selectedView = subContainerHolder.getSelectedView();
			} else {
				break;
			}
		}
		return Collections.unmodifiableList(path);
	}

	@Override
	public ISubView getSelected() {
		List<ISubView> path = getSelectedPath(root);
		if (path.isEmpty())
			return root;
		return path.get(path.size() - 1);
	}

	@Override
	public String getSelectedPath() {
		ISubView selected = getSelected();
		String path = getPath(selected);
		return path;
	}

	@Override
	public boolean isDebug() {
		return debug;
	}

	@Override
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	@Override
	public String trimDelimiterRight(String path) {
		return path.replaceAll(quote(delimiter) + "*$", "");
	}

	@Override
	public String trimDelimiterLeft(String path) {
		return path.replaceAll("^" + quote(delimiter) + "*", "");
	}

	@Override
	public String trimDelimiter(String path) {
		return trimDelimiterLeft(trimDelimiterRight(path));
	}

	@Override
	public boolean equalsPath(String path1, String path2) {
		String trimmed1 = trimDelimiter(path1);
		String trimmed2 = trimDelimiter(path2);
		return trimmed1.equals(trimmed2);
	}

	@Override
	public boolean isSubPath(String sourcePath, String testPath) {
		if (equalsPath(sourcePath, testPath))
			return true;
		String trimmedSource = trimDelimiter(sourcePath);
		String trimmedTest = trimDelimiter(testPath);
		if (trimmedTest.isEmpty())
			return true;
		return trimmedSource.startsWith(trimmedTest + delimiter);
	}

	@Override
	public ISubContainer getRoot() {
		return root;
	}

	@Override
	public ViewStatus getViewStatus(ISubView view) {
		checkContains(view);
		ViewHolder holder = getHolder(view);
		return holder.getViewStatus();
	}

	@Override
	public void addViewStatusChangeListener(IViewStatusChangeListener listener) {
		viewStatusDispatcher.addViewStatusChangeListener(listener);
	}

	@Override
	public void removeViewStatusChangeListener(IViewStatusChangeListener listener) {
		viewStatusDispatcher.removeViewStatusChangeListener(listener);
	}

	@Override
	public Navigator getNavigator() {
		return navigator;
	}

	@Override
	public boolean contains(ISubView view) {
		return viewHolders.containsKey(view);
	}

	protected void checkContains(ISubView view) {
		if (!contains(view))
			throw new IllegalArgumentException("View is not added: \"" + getRelativePath(view) + "\"");
	}

	protected String getRelativePath(ISubView view) {
		String path = view.getRelativePath();
		checkRelativePath(path);
		return path;
	}

	protected void checkRelativePath(ISubView view) {
		checkRelativePath(view.getRelativePath());
	}

	protected void checkRelativePath(String path) {
		if (path.contains(delimiter))
			throw new IllegalArgumentException(String.format("Relative path of view \"%s\" can't contain delimiter \"%s\"", path, delimiter));
	}

	@Override
	public List<ViewStatus> getViewStatusHistory(ISubView view) {
		checkContains(view);
		ViewHolder holder = getHolder(view);
		return Collections.unmodifiableList(holder.getStatusHistory());
	}

	@Override
	public ISubContainer getContainer(ISubView view) {
		checkContains(view);
		ViewHolder holder = getHolder(view);
		ISubContainer container = holder.getContainer();
		return container;
	}

	@Override
	public List<ISubView> getSubViews(ISubContainer container) {
		checkContains(container);
		ContainerHolder holder = getHolder(container);
		List<ISubView> subViews = new ArrayList<ISubView>(holder.getViews().values());
		return Collections.unmodifiableList(subViews);
	}

	@Override
	public HierarchyDirection getDirection(ISubView sourceView, ISubView targetView) {
		checkContains(sourceView);
		checkContains(targetView);
		String sourcePath = getPath(sourceView);
		String targetPath = getPath(targetView);
		return getDirection(sourcePath, targetPath);
	}

	@Override
	public HierarchyDirection getDirection(String sourcePath, String targetPath) {
		if (equalsPath(sourcePath, targetPath))
			return HierarchyDirection.None;
		if (isSubPath(sourcePath, targetPath))
			return HierarchyDirection.Up;
		if (isSubPath(targetPath, sourcePath))
			return HierarchyDirection.Down;
		return HierarchyDirection.Nearby;
	}

	@Override
	public String getDelimiter() {
		return delimiter;
	}

	@Override
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

}
