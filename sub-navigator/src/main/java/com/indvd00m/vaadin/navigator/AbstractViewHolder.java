package com.indvd00m.vaadin.navigator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.indvd00m.vaadin.navigator.api.ISubContainer;
import com.indvd00m.vaadin.navigator.api.ISubView;
import com.indvd00m.vaadin.navigator.api.ViewStatus;
import com.indvd00m.vaadin.navigator.api.event.IVIewStatusChangeEvent;
import com.indvd00m.vaadin.navigator.api.event.IViewStatusChangeListener;
import com.indvd00m.vaadin.navigator.event.ViewStatusChangeEvent;
import com.vaadin.navigator.View;
import com.vaadin.server.ClientConnector.AttachEvent;
import com.vaadin.server.ClientConnector.AttachListener;
import com.vaadin.server.ClientConnector.DetachEvent;
import com.vaadin.server.ClientConnector.DetachListener;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Nov 17, 2015 7:04:15 PM
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractViewHolder<V extends ISubView> implements View, AttachListener, DetachListener {

	V view;
	ISubContainer container;
	ViewStatus viewStatus;
	List<ViewStatus> statusHistory = new ArrayList<ViewStatus>();
	List<IViewStatusChangeListener> statusListeners = new ArrayList<IViewStatusChangeListener>();
	boolean createdDynamically = false;

	public AbstractViewHolder(V view) {
		this.view = view;
		view.addAttachListener(this);
		view.addDetachListener(this);
	}

	public ViewStatus getViewStatus() {
		return viewStatus;
	}

	protected void setViewStatus(ViewStatus viewStatus) {
		ViewStatus prevStatus = this.viewStatus;
		this.viewStatus = viewStatus;
		statusHistory.add(viewStatus);
		IVIewStatusChangeEvent event = new ViewStatusChangeEvent(view, prevStatus, viewStatus);
		for (IViewStatusChangeListener listener : statusListeners) {
			listener.viewStatusChanged(event);
		}
	}

	public V getView() {
		return view;
	}

	@Override
	public void attach(AttachEvent event) {
		setViewStatus(ViewStatus.Attached);
	}

	@Override
	public void detach(DetachEvent event) {
		setViewStatus(ViewStatus.Detached);
	}

	public void addViewStatusChangeListener(IViewStatusChangeListener listener) {
		if (!statusListeners.contains(listener))
			statusListeners.add(listener);
	}

	public void removeViewStatusChangeListener(IViewStatusChangeListener listener) {
		statusListeners.remove(listener);
	}

	public ISubContainer getContainer() {
		return container;
	}

	public void setContainer(ISubContainer container) {
		this.container = container;
	}

	public List<ViewStatus> getStatusHistory() {
		return Collections.unmodifiableList(statusHistory);
	}

	public boolean isBuilt() {
		for (int i = statusHistory.size() - 1; i >= 0; i--) {
			ViewStatus status = statusHistory.get(i);
			if (status == ViewStatus.Built)
				return true;
			if (status == ViewStatus.Cleaned)
				return false;
		}
		return false;
	}

	public boolean isCleaned() {
		for (int i = statusHistory.size() - 1; i >= 0; i--) {
			ViewStatus status = statusHistory.get(i);
			if (status == ViewStatus.Cleaned)
				return true;
			if (status == ViewStatus.Built)
				return false;
		}
		return false;
	}

	public boolean isBuiltAtLeastOnce() {
		return statusHistory.contains(ViewStatus.Built);
	}

	public boolean isCreatedDynamically() {
		return createdDynamically;
	}

	public void setCreatedDynamically(boolean createdDynamically) {
		this.createdDynamically = createdDynamically;
	}

}
