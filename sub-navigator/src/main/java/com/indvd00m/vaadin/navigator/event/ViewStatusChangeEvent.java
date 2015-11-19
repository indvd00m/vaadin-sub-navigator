package com.indvd00m.vaadin.navigator.event;

import java.util.Date;

import com.indvd00m.vaadin.navigator.api.ISubView;
import com.indvd00m.vaadin.navigator.api.ViewStatus;
import com.indvd00m.vaadin.navigator.api.event.IVIewStatusChangeEvent;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Nov 17, 2015 12:59:27 AM
 *
 */
public class ViewStatusChangeEvent implements IVIewStatusChangeEvent {

	ISubView view;
	ViewStatus prevStatus;
	ViewStatus currentStatus;
	Date eventDate;

	public ViewStatusChangeEvent(ISubView view, ViewStatus prevStatus, ViewStatus currentStatus) {
		super();
		this.view = view;
		this.prevStatus = prevStatus;
		this.currentStatus = currentStatus;
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
	public Date getEventDate() {
		return eventDate;
	}

}
