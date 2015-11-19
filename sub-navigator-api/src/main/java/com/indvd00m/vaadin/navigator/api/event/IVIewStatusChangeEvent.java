package com.indvd00m.vaadin.navigator.api.event;

import java.util.Date;

import com.indvd00m.vaadin.navigator.api.ISubView;
import com.indvd00m.vaadin.navigator.api.ViewStatus;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Nov 19, 2015 2:31:10 PM
 *
 */
public interface IVIewStatusChangeEvent {

	ISubView getView();

	ViewStatus getPrevStatus();

	ViewStatus getCurrentStatus();

	Date getEventDate();

}