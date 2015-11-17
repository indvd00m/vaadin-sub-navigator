package com.indvd00m.vaadin.navigator.event;

import java.util.Date;

import com.indvd00m.vaadin.navigator.LocalizableView;
import com.indvd00m.vaadin.navigator.ViewState;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Nov 17, 2015 12:59:27 AM
 *
 */
public class SubViewStateChangeEvent {

	LocalizableView subView;
	ViewState prevState;
	ViewState currentState;
	Date eventDate;

	public SubViewStateChangeEvent(LocalizableView subView, ViewState prevState, ViewState currentState) {
		super();
		this.subView = subView;
		this.prevState = prevState;
		this.currentState = currentState;
		eventDate = new Date();
	}

	public LocalizableView getSubView() {
		return subView;
	}

	public ViewState getPrevState() {
		return prevState;
	}

	public ViewState getCurrentState() {
		return currentState;
	}

	public Date getEventDate() {
		return eventDate;
	}

}
