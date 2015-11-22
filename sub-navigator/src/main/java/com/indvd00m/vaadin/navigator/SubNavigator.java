package com.indvd00m.vaadin.navigator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.Navigator.UriFragmentManager;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.UI;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Nov 17, 2015 5:40:41 PM
 *
 */
@SuppressWarnings("serial")
public class SubNavigator implements ISubNavigator {

	Navigator navigator;
	ISubContainer rootContainer;
	UriFragmentManager stateManager;
	String currentNavigationState;
	Map<ISubView, ViewHolder> viewHolders = new HashMap<ISubView, ViewHolder>();
	boolean processing = false;
	boolean debug = false;
	ViewStatusLogger viewStatusLogger = new ViewStatusLogger(this);
	ViewStatusDispatcher viewStatusDispatcher = new ViewStatusDispatcher();
	SubViewDisplay viewDisplay = new SubViewDisplay(this);
	SubViewProvider viewProvider = new SubViewProvider(this);
	ISubView currentView = null;

	// TODO: hierarchical page title
	// TODO: setDelimiter()
	// TODO: deprecate double register of same view name

	class ViewHolder extends AbstractViewHolder<ISubView> {

		public ViewHolder(ISubView view) {
			super(view);
		}

		@Override
		public void enter(ViewChangeEvent event) {

		}

	}

	class ContainerHolder extends ViewHolder {

		Map<String, ISubView> views = new LinkedHashMap<String, ISubView>();

		public ContainerHolder(ISubContainer container) {
			super(container);
		}

		@Override
		public ISubContainer getView() {
			return (ISubContainer) view;
		}

		public Map<String, ISubView> getViews() {
			return views;
		}

	}

	public SubNavigator(UI ui, ISubContainer rootContainer) {
		this(ui, rootContainer, null);
	}

	public SubNavigator(UI ui, ISubContainer rootContainer, IViewStatusChangeListener listener) {
		this(ui, rootContainer, listener, false);
	}

	public SubNavigator(UI ui, ISubContainer rootContainer, IViewStatusChangeListener listener, boolean debug) {
		this.rootContainer = rootContainer;
		this.debug = debug;
		stateManager = new UriFragmentManager(ui.getPage());
		navigator = new Navigator(ui, stateManager, viewDisplay);
		navigator.addProvider(viewProvider);
		if (listener != null)
			addViewStatusChangeListener(listener);
		if (register(rootContainer))
			setRegisteredStatus(rootContainer);
		currentNavigationState = stateManager.getState();
	}

	protected boolean register(ISubView view) {
		if (registered(view))
			return false;
		ViewHolder holder = createHolder(view);
		viewHolders.put(view, holder);
		holder.addViewStatusChangeListener(viewStatusLogger);
		holder.addViewStatusChangeListener(viewStatusDispatcher);
		return true;
	}

	protected void setRegisteredStatus(ISubView view) {
		ViewHolder holder = getHolder(view);
		holder.setViewStatus(ViewStatus.Registered);
	}

	@Override
	public void register(ISubContainer container, ISubView view) {
		if (container instanceof ISubDynamicContainer) {
			ISubDynamicContainer dynamicContainer = (ISubDynamicContainer) container;
			ContainerHolder containerHolder = getHolder(dynamicContainer);
			if (containerHolder != null) {
				for (ISubView subView : containerHolder.getViews().values()) {
					ViewHolder holder = getHolder(subView);
					if (holder.isCreatedDynamically())
						throw new IllegalStateException(dynamicContainer.getClass().getSimpleName() + " can contain only one dynamically created element!");
				}
			}
		}
		if (registered(container) && registered(view))
			return;

		boolean containerRegistered = register(container);
		boolean viewRegistered = register(view);

		ContainerHolder containerHolder = getHolder(container);
		ViewHolder viewHolder = getHolder(view);

		viewHolder.setContainer(container);
		String viewPath = getPath(view);
		containerHolder.getViews().put(viewPath, view);

		if (containerRegistered)
			setRegisteredStatus(container);
		if (viewRegistered)
			setRegisteredStatus(view);
	}

	protected ViewHolder createHolder(ISubView view) {
		if (view instanceof ISubContainer) {
			ISubContainer container = (ISubContainer) view;
			return new ContainerHolder(container);
		}
		return new ViewHolder(view);
	}

	@Override
	public void unregister(ISubView view) {
		if (!registered(view))
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
				if (dynamicContainer.getSelectedView() == view)
					deselect(dynamicContainer);
			}
		}

		if (!holder.isCleaned())
			clean(view);
		holder.setViewStatus(ViewStatus.Unregistered);
		holder.statusListeners.clear();
		viewHolders.remove(view);
	}

	@Override
	public void unregister(ISubContainer container) {
		if (!registered(container))
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
				unregister(subContainer);
			} else {
				unregister(subView);
			}
		}
		unregister((ISubView) container);
	}

	protected ViewHolder getHolder(ISubView view) {
		return (ViewHolder) viewHolders.get(view);
	}

	protected ContainerHolder getHolder(ISubContainer container) {
		return (ContainerHolder) viewHolders.get(container);
	}

	@Override
	public void setSelected(ISubView view) {
		checkRegistered(view);

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
		checkRegistered(view);
		if (!processing) {
			rebuildSimple(view);
		} else {
			if (currentView == null || !registered(currentView)) {
				rebuildDifferentPath(rootContainer, view);
			} else {
				rebuildDifferentPath(currentView, view);
			}
		}
	}

	protected void rebuildDifferentPath(ISubView oldView, ISubView newView) {
		if (!registered(oldView))
			return;
		if (!registered(newView))
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
		if (!registered(view1))
			return null;
		if (!registered(view2))
			return null;

		List<ISubView> pathList1 = getPathList(view1);
		List<ISubView> pathList2 = getPathList(view2);
		if (pathList1.isEmpty())
			return null;
		if (pathList2.isEmpty())
			return null;
		if (pathList1.get(0) != rootContainer && pathList2.get(0) != rootContainer)
			return null;
		ISubView lastDivergationNode = rootContainer;
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
	public String getDivergationNode(String path1, String path2) {
		String delimiter = "/";
		path1 = trimDivider(path1);
		path2 = trimDivider(path2);
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

		return trimDivider(divergationNode.toString());
	}

	@Override
	public List<String> getNodesBetween(String path1, String path2) {
		List<String> nodes = new ArrayList<String>();
		if (!isSubPath(path2, path1))
			return nodes;
		String delimiter = "/";
		path1 = trimDivider(path1.replaceAll(quote(delimiter) + "+", delimiter));
		path2 = trimDivider(path2.replaceAll(quote(delimiter) + "+", delimiter));
		String remain = trimDivider(path2.replaceFirst(quote(path1), ""));
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
		checkRegistered(view);
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
					if (pathContainer.getSelectedView() != nextElement) {
						return false;
					}
				}
			}
		}
		return true;
	}

	@Override
	public List<ISubView> getPathList(ISubView view) {
		checkRegistered(view);
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
		checkRegistered(view);
		StringBuilder sb = new StringBuilder();
		List<ISubView> pathList = getPathList(view);
		for (ISubView pathElement : pathList) {
			sb.append(pathElement.getViewName()).append("/");
		}
		String path = trimDivider(sb.toString());
		return path;
	}

	@Override
	public int getLevel(ISubView view) {
		checkRegistered(view);
		return getPathList(view).size();
	}

	@Override
	public boolean isRoot(ISubView view) {
		checkRegistered(view);
		return view == rootContainer;
	}

	@Override
	public void notifySelectedChangeDirected(ISubContainer container) {
		checkRegistered(container);
		if (processing)
			return;
		ISubView selectedView = container.getSelectedView();
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
			String divergationNode = getDivergationNode(prevState, state);
			List<String> nodesBetween = getNodesBetween(divergationNode, prevState);
			List<ISubView> oldBranch = new ArrayList<ISubView>();
			for (String path : nodesBetween) {
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
						unregister(container);
					} else {
						unregister(view);
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

		// try to find already registered view
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
				String subPath = trimDivider(state.replaceFirst("\\Q" + path + "\\E", ""));
				String viewName = subPath.replaceAll("/+.*", "");
				ISubView subView = dynamicContainer.createView(viewName);
				if (subView != null) {
					if (!viewName.equals(subView.getViewName()))
						throw new IllegalStateException(String.format("Name of created view \"%s\" must be \"%s\"", subView.getViewName(), viewName));
					register(dynamicContainer, subView);
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
		String path = getPath(view);

		// selecting and building all hierarchy of view, which not selected and built yet
		setSelected(view);

		currentView = view;
		HierarchyDirection direction = getDirection(currentNavigationState, path);
		if (direction == HierarchyDirection.Down) {
			// found subviews, set state to actual value
			stateManager.setState(path);
		}
		currentNavigationState = stateManager.getState();
	}

	protected void select(ISubView view) {
		List<ISubView> pathList = getPathList(view);
		for (ISubView pathElement : pathList) {
			ViewHolder holder = getHolder(pathElement);
			ISubContainer container = holder.getContainer();
			if (container != null) {
				ISubView selectedView = container.getSelectedView();
				if (selectedView != pathElement) {
					container.setSelectedView(pathElement);
				}
			}
		}
	}

	protected void deselect(ISubContainer container) {
		ISubView selectedView = container.getSelectedView();

		List<ISubView> selectedPath = getSelectedPath(container);
		for (int i = selectedPath.size() - 1; i >= 0; i--) {
			ISubView subView = selectedPath.get(i);
			if (subView instanceof ISubContainer) {
				ISubContainer subContainer = (ISubContainer) subView;
				deselect(subContainer);
			}
		}

		if (selectedView != null) {
			ContainerHolder containerHolder = getHolder(container);
			if (containerHolder.getViews().isEmpty()) {
				if (connected(container)) {
					container.setSelectedView(null);
				}
			}
			if (registered(selectedView)) {
				ViewHolder selectedHolder = getHolder(selectedView);
				if (selectedHolder.isCreatedDynamically()) {
					if (selectedView instanceof ISubContainer) {
						ISubContainer subContainer = (ISubContainer) selectedView;
						unregister(subContainer);
					} else {
						unregister(selectedView);
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
		if (container == rootContainer)
			return true;
		return connected(container);
	}

	@Override
	public List<ISubView> getSelectedPath(ISubContainer container) {
		checkRegistered(container);
		List<ISubView> path = new ArrayList<ISubView>();
		ISubView selectedView = container.getSelectedView();
		while (selectedView != null) {
			path.add(selectedView);
			if (selectedView instanceof ISubContainer) {
				ISubContainer subContainer = (ISubContainer) selectedView;
				selectedView = subContainer.getSelectedView();
			} else {
				break;
			}
		}
		return Collections.unmodifiableList(path);
	}

	@Override
	public ISubView getSelected() {
		List<ISubView> path = getSelectedPath(rootContainer);
		if (path.isEmpty())
			return rootContainer;
		return path.get(path.size() - 1);
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
	public String trimDividerRight(String path) {
		return path.replaceAll("/*$", "");
	}

	@Override
	public String trimDividerLeft(String path) {
		return path.replaceAll("^/*", "");
	}

	@Override
	public String trimDivider(String path) {
		return trimDividerLeft(trimDividerRight(path));
	}

	@Override
	public boolean equalsPath(String path1, String path2) {
		String trimmed1 = trimDivider(path1);
		String trimmed2 = trimDivider(path2);
		return trimmed1.equals(trimmed2);
	}

	@Override
	public boolean isSubPath(String sourcePath, String testPath) {
		if (equalsPath(sourcePath, testPath))
			return true;
		String trimmedSource = trimDivider(sourcePath);
		String trimmedTest = trimDivider(testPath);
		if (trimmedTest.isEmpty())
			return true;
		return trimmedSource.startsWith(trimmedTest + "/");
	}

	@Override
	public ISubContainer getRootContainer() {
		return rootContainer;
	}

	@Override
	public ViewStatus getViewStatus(ISubView view) {
		checkRegistered(view);
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
	public boolean registered(ISubView view) {
		return viewHolders.containsKey(view);
	}

	protected void checkRegistered(ISubView view) {
		if (!registered(view))
			throw new IllegalArgumentException("View is not registered: \"" + view.getViewName() + "\"");
	}

	@Override
	public List<ViewStatus> getViewStatusHistory(ISubView view) {
		checkRegistered(view);
		ViewHolder holder = getHolder(view);
		return Collections.unmodifiableList(holder.getStatusHistory());
	}

	@Override
	public ISubContainer getContainer(ISubView view) {
		checkRegistered(view);
		ViewHolder holder = getHolder(view);
		ISubContainer container = holder.getContainer();
		return container;
	}

	@Override
	public List<ISubView> getSubViews(ISubContainer container) {
		checkRegistered(container);
		ContainerHolder holder = getHolder(container);
		List<ISubView> subViews = new ArrayList<ISubView>(holder.getViews().values());
		return Collections.unmodifiableList(subViews);
	}

	@Override
	public HierarchyDirection getDirection(ISubView sourceView, ISubView targetView) {
		checkRegistered(sourceView);
		checkRegistered(targetView);
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

}
