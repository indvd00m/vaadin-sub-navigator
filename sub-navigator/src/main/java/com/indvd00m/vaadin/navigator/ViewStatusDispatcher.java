package com.indvd00m.vaadin.navigator;

import java.util.ArrayList;
import java.util.List;

import com.indvd00m.vaadin.navigator.api.event.IVIewStatusChangeEvent;
import com.indvd00m.vaadin.navigator.api.event.IViewStatusChangeListener;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Nov 19, 2015 2:02:07 PM
 *
 */
public class ViewStatusDispatcher implements IViewStatusChangeListener {

	List<IViewStatusChangeListener> listeners = new ArrayList<IViewStatusChangeListener>();

	@Override
	public void viewStatusChanged(IVIewStatusChangeEvent event) {
		for (IViewStatusChangeListener listener : listeners)
			listener.viewStatusChanged(event);
	}

	public void addViewStatusChangeListener(IViewStatusChangeListener listener) {
		if (!listeners.contains(listener))
			listeners.add(listener);
	}

	public void removeViewStatusChangeListener(IViewStatusChangeListener listener) {
		listeners.remove(listener);
	}

}
