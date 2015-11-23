package com.indvd00m.vaadin.navigator.event;

import java.util.Date;
import java.util.List;

import com.indvd00m.vaadin.navigator.api.event.IVIewStatusChangeEvent;
import com.indvd00m.vaadin.navigator.api.view.ISubView;
import com.indvd00m.vaadin.navigator.api.view.ViewStatus;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Nov 17, 2015 12:59:27 AM
 *
 */
public class ViewStatusChangeEvent implements IVIewStatusChangeEvent {

	ISubView view;
	ViewStatus prevStatus;
	ViewStatus currentStatus;
	List<ViewStatus> statusHistory;
	Date eventDate;

	public ViewStatusChangeEvent(ISubView view, ViewStatus prevStatus, ViewStatus currentStatus, List<ViewStatus> statusHistory) {
		super();
		this.view = view;
		this.prevStatus = prevStatus;
		this.currentStatus = currentStatus;
		this.statusHistory = statusHistory;
		eventDate = new Date();
	}

	@Override
	public ISubView getView() {
		return view;
	}

	@Override
	public ViewStatus getPrevStatus() {
		return prevStatus;
	}

	@Override
	public ViewStatus getCurrentStatus() {
		return currentStatus;
	}

	@Override
	public List<ViewStatus> getStatusHistory() {
		return statusHistory;
	}

	@Override
	public Date getEventDate() {
		return eventDate;
	}

}
