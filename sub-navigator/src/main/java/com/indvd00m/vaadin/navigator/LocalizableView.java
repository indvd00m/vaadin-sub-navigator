package com.indvd00m.vaadin.navigator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.github.peholmst.i18n4vaadin.LocaleChangedEvent;
import com.github.peholmst.i18n4vaadin.LocaleChangedListener;
import com.github.peholmst.i18n4vaadin.util.I18NHolder;
import com.indvd00m.vaadin.navigator.event.SubViewStateChangeEvent;
import com.indvd00m.vaadin.navigator.event.SubViewStateChangeListener;
import com.vaadin.ui.VerticalLayout;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Oct 27, 2015 7:57:52 PM
 *
 */
@SuppressWarnings("serial")
public abstract class LocalizableView extends VerticalLayout implements LocaleChangedListener {

	protected ViewState viewState = ViewState.Created;

	protected List<SubViewStateChangeListener> stateListeners = new ArrayList<SubViewStateChangeListener>();

	abstract protected void build();

	abstract protected void localize();

	@Override
	public void localeChanged(LocaleChangedEvent event) {
		if (viewState == ViewState.Builded || viewState == ViewState.Localized) {
			localize();
			setViewState(ViewState.Localized);
		}
	}

	@Override
	public void attach() {
		super.attach();
		onAttach();
		I18NHolder.get().addLocaleChangedListener(this);
		setViewState(ViewState.Attached);
	}

	protected void onAttach() {
		build();
		setViewState(ViewState.Builded);
		localize();
		setViewState(ViewState.Localized);
	}

	@Override
	public void detach() {
		I18NHolder.get().removeLocaleChangedListener(this);
		super.detach();
		setViewState(ViewState.Detached);
	}

	public ViewState getViewState() {
		return viewState;
	}

	@SuppressWarnings("unused")
	protected void setViewState(ViewState viewState) {
		ViewState prevState = this.viewState;
		this.viewState = viewState;
		if (false) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			System.out.println(String.format("%s %s: %s", sdf.format(new Date()), ((SubView) this).getFullPath(),
					viewState.name()));
		}
		SubViewStateChangeEvent event = new SubViewStateChangeEvent(this, prevState, viewState);
		for (SubViewStateChangeListener listener : stateListeners) {
			listener.subViewStateChanged(event);
		}
	}

	public void addSubViewStateChangeListener(SubViewStateChangeListener listener) {
		if (!stateListeners.contains(listener))
			stateListeners.add(listener);
	}

	public void removeSubViewStateChangeListener(SubViewStateChangeListener listener) {
		stateListeners.remove(listener);
	}

}
