package com.indvd00m.vaadin.navigator.status;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.indvd00m.vaadin.navigator.api.ISubNavigator;
import com.indvd00m.vaadin.navigator.api.event.IVIewStatusChangeEvent;
import com.indvd00m.vaadin.navigator.api.event.IViewStatusChangeListener;
import com.indvd00m.vaadin.navigator.api.view.ISubView;
import com.indvd00m.vaadin.navigator.api.view.ViewStatus;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Nov 19, 2015 2:02:07 PM
 *
 */
public class ViewStatusLogger implements IViewStatusChangeListener {

	ISubNavigator subNavigator;
	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

	public ViewStatusLogger(ISubNavigator subNavigator) {
		this.subNavigator = subNavigator;
	}

	@Override
	public void viewStatusChanged(IVIewStatusChangeEvent event) {
		if (subNavigator.isDebug()) {
			ISubView view = event.getView();
			ViewStatus viewStatus = event.getCurrentStatus();
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

			String date = sdf.format(new Date());
			String path = subNavigator.getPath(view);
			String status = viewStatus.name();
			System.out.println(String.format("%s \"%s\": %s", date, path, status));
		}
	}

}
