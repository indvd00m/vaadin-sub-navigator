package com.indvd00m.vaadin.navigator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.indvd00m.vaadin.navigator.api.ISubContainer;
import com.indvd00m.vaadin.navigator.api.ISubDynamicContainer;
import com.indvd00m.vaadin.navigator.api.ISubNavigator;
import com.indvd00m.vaadin.navigator.api.ISubView;
import com.indvd00m.vaadin.navigator.api.ViewStatus;
import com.indvd00m.vaadin.navigator.api.event.IViewStatusChangeListener;
import com.vaadin.navigator.Navigator;
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
	Map<ISubView, ViewHolder> viewHolders = new HashMap<ISubView, ViewHolder>();
	boolean showingView = false;
	boolean debug = false;
	ViewStatusLogger viewStatusLogger = new ViewStatusLogger(this);
	ViewStatusDispatcher viewStatusDispatcher = new ViewStatusDispatcher();
	SubViewDisplay viewDisplay = new SubViewDisplay(this);
	SubViewProvider viewProvider = new SubViewProvider(this);

	// TODO: separate api
	// TODO: check situation when views registered before build
	// TODO: do not call clean before first build
	// TODO: hierarchical page title
	// TODO: setDelimiter()

	class ViewHolder extends AbstractViewHolder<ISubView> {

		public ViewHolder(ISubView view) {
			super(view);
		}

		@Override
		public void enter(ViewChangeEvent event) {
			rebuild(getView());
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

		@Override
		public void enter(ViewChangeEvent event) {
			rebuild(getView());
			restate(getView());
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
		navigator = new Navigator(ui, viewDisplay);
		navigator.addProvider(viewProvider);
		if (listener != null)
			addViewStatusChangeListener(listener);
		if (register(rootContainer))
			setRegisteredStatus(rootContainer);
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
			if (containerHolder != null && !containerHolder.views.isEmpty())
				throw new IllegalStateException(
						dynamicContainer.getClass().getSimpleName() + " can contain only one element!");
		}
		if (registered(container) && registered(view))
			return;

		boolean containerRegistered = register(container);
		boolean viewRegistered = register(view);

		ContainerHolder containerHolder = getHolder(container);
		ViewHolder viewHolder = getHolder(view);

		viewHolder.setContainer(container);
		String viewPath = getPath(view);
		containerHolder.views.put(viewPath, view);

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

	protected void unregister(ISubView view) {
		if (!registered(view))
			return;
		ViewHolder holder = getHolder(view);
		holder.setViewStatus(ViewStatus.Unregistered);
		holder.statusListeners.clear();
		viewHolders.remove(view);
	}

	@Override
	public void unregister(ISubContainer container, ISubView view) {
		if (view instanceof ISubContainer) {
			ISubContainer subContainer = (ISubContainer) view;
			ContainerHolder subContainerHolder = getHolder(subContainer);
			if (subContainerHolder != null) {
				for (ISubView subView : subContainerHolder.views.values()) {
					unregister(subContainer, subView);
				}
			}
		}

		if (!registered(container) && !registered(view))
			return;

		ContainerHolder containerHolder = getHolder(container);
		ViewHolder viewHolder = getHolder(view);

		if (viewHolder != null) {
			String viewPath = getPath(view);
			if (containerHolder != null)
				containerHolder.views.remove(viewPath);
			viewHolder.setContainer(null);
		}

		if (registered(view))
			unregister(view);

		if (container instanceof ISubDynamicContainer) {
			ISubDynamicContainer dynamicContainer = (ISubDynamicContainer) container;
			dynamicContainer.setSelectedView(null);
		}
		// TODO: restate for ISubContainer?
	}

	protected ViewHolder getHolder(ISubView view) {
		return (ViewHolder) viewHolders.get(view);
	}

	protected ContainerHolder getHolder(ISubContainer container) {
		return (ContainerHolder) viewHolders.get(container);
	}

	@Override
	public void rebuild(ISubView view) {
		checkRegistered(view);
		if (!isSelected(view))
			return;
		ViewHolder holder = getHolder(view);
		if (holder.isBuiltAtLeastOnce()) {
			view.clean();
			holder.setViewStatus(ViewStatus.Cleaned);
		}
		view.build();
		holder.setViewStatus(ViewStatus.Builded);
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
		return path;
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

	protected void restate(ISubContainer container) {
		ContainerHolder holder = getHolder(container);
		String state = navigator.getState();
		String path = getPath(container);
		Map<String, ISubView> viewsByState = holder.views;

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
		throw new IllegalArgumentException("Trying to navigate to an unknown state '" + state + "'");
	}

	@Override
	public void selectedViewChangeDirected(ISubContainer container) {
		checkRegistered(container);
		if (showingView)
			return;
		ISubView selectedView = container.getSelectedView();
		if (selectedView == null)
			selectedView = container;
		String path = getPath(selectedView);
		String state = navigator.getState();
		if (!equalsPath(state, path)) {
			if (isSelected(container))
				navigator.navigateTo(path);
		}
	}

	protected View getView(ContainerHolder containerHolder, String state) {
		for (Entry<String, ISubView> e : containerHolder.views.entrySet()) {
			String path = e.getKey();
			ISubView view = e.getValue();
			ViewHolder holder = getHolder(view);
			if (equalsPath(state, path)) {
				return holder;
			}
			if (isSubPath(state, path)) {
				if (view instanceof ISubContainer) {
					ISubContainer container = (ISubContainer) view;
					ContainerHolder subContainerHolder = getHolder(container);
					return getView(subContainerHolder, state);
				}
				return holder;
			}
		}
		ISubContainer container = containerHolder.getView();
		String path = getPath(container);
		if (isSubPath(state, path)) {
			if (container instanceof ISubDynamicContainer) {
				ISubDynamicContainer dynamicContainer = (ISubDynamicContainer) container;
				if (!isSelected(dynamicContainer))
					return containerHolder;
				if (equalsPath(state, path))
					return containerHolder;
				String subPath = trimDivider(state.replaceFirst("\\Q" + path + "\\E", ""));
				String viewName = subPath.replaceAll("/+.*", "");
				ISubView subView = dynamicContainer.createView(viewName);
				if (subView == null)
					return null;
				register(dynamicContainer, subView);
				String fullSubPath = getPath(subView);
				if (!isSubPath(state, fullSubPath)) {
					unregister(dynamicContainer, subView);
					return null;
				}
			}
			return containerHolder;
		}
		return null;
	}

	protected void showView(ContainerHolder containerHolder, ViewHolder viewHolder) {
		if (containerHolder != viewHolder) {
			ISubContainer container = containerHolder.getView();
			ISubView view = viewHolder.getView();
			String state = navigator.getState();
			Map<String, ISubView> viewsByState = containerHolder.views;

			if (viewsByState.values().contains(view)) {
				List<ISubView> pathElements = getPathList(view);
				for (ISubView pathElement : pathElements) {
					if (!isSelected(pathElement)) {
						ViewHolder pathElementHolder = getHolder(pathElement);
						ISubContainer pathContainer = pathElementHolder.getContainer();
						if (pathContainer != null) {
							deselect(pathContainer);
							pathContainer.setSelectedView(pathElement);
						}
					}
				}
				if (view instanceof ISubContainer) {
					ISubContainer subContainer = (ISubContainer) view;
					deselect(subContainer);
				}
			} else {
				String containerPath = getPath(container);
				if (isSubPath(state, containerPath)) {
					for (Entry<String, ISubView> e : viewsByState.entrySet()) {
						String subPath = e.getKey();
						ISubView subView = e.getValue();
						if (isSubPath(state, subPath)) {
							if (subView instanceof ISubContainer) {
								ISubContainer subContainer = (ISubContainer) subView;
								ContainerHolder subHolder = getHolder(subContainer);
								showView(subHolder, viewHolder);
							}
						}
					}
				}
			}
			if (container instanceof ISubDynamicContainer) {
				String path = getPath(view);
				if (equalsPath(state, path)) {
					List<ISubView> pathList = getPathList(view);
					for (int i = pathList.size() - 1; i >= 0; i--) {
						ISubView pathElement = pathList.get(i);
						if (pathElement instanceof ISubDynamicContainer) {
							ISubDynamicContainer subDynamicContainer = (ISubDynamicContainer) pathElement;
							ContainerHolder subDynamicContainerHolder = getHolder(subDynamicContainer);
							subDynamicContainerHolder.views.clear();
						}
					}
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

		if (container instanceof ISubDynamicContainer) {
			ISubDynamicContainer dynamicContainer = (ISubDynamicContainer) container;
			if (selectedView != null) {
				unregister(dynamicContainer, selectedView);
			}
		}
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
		return path;
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
		return holder.getStatusHistory();
	}

}
