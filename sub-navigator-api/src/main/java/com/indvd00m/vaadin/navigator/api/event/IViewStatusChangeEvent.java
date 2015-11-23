package com.indvd00m.vaadin.navigator.api.event;

import java.util.Date;
import java.util.List;

import com.indvd00m.vaadin.navigator.api.view.ISubView;
import com.indvd00m.vaadin.navigator.api.view.ViewStatus;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Nov 19, 2015 2:31:10 PM
 *
 */
public interface IViewStatusChangeEvent {

	ISubView getView();

	ViewStatus getPrevStatus();

	ViewStatus getCurrentStatus();

	List<ViewStatus> getStatusHistory();

	Date getEventDate();

}