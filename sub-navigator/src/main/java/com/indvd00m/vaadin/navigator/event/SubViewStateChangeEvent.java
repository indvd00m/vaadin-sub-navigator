package com.indvd00m.vaadin.navigator.event;

import java.util.Date;

import com.indvd00m.vaadin.navigator.SubView;
import com.indvd00m.vaadin.navigator.ViewState;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Nov 17, 2015 12:59:27 AM
 *
 */
public class SubViewStateChangeEvent {

	SubView subView;
	ViewState prevState;
	ViewState currentState;
	Date eventDate;

	public SubViewStateChangeEvent(SubView subView, ViewState prevState, ViewState currentState) {
		super();
		this.subView = subView;
		this.prevState = prevState;
		this.currentState = currentState;
		eventDate = new Date();
	}

	public SubView getSubView() {
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
