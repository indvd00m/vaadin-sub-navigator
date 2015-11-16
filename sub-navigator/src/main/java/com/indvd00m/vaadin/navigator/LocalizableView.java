package com.indvd00m.vaadin.navigator;

import com.github.peholmst.i18n4vaadin.LocaleChangedEvent;
import com.github.peholmst.i18n4vaadin.LocaleChangedListener;
import com.github.peholmst.i18n4vaadin.util.I18NHolder;
import com.vaadin.ui.VerticalLayout;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Oct 27, 2015 7:57:52 PM
 *
 */
@SuppressWarnings("serial")
public abstract class LocalizableView extends VerticalLayout implements LocaleChangedListener {

	protected ViewState viewState = ViewState.Created;

	abstract protected void build();

	abstract protected void localize();

	@Override
	public void localeChanged(LocaleChangedEvent event) {
		if (viewState == ViewState.Builded || viewState == ViewState.Localized) {
			localize();
			viewState = ViewState.Localized;
		}
	}

	@Override
	public void attach() {
		super.attach();
		onAttach();
		I18NHolder.get().addLocaleChangedListener(this);
		viewState = ViewState.Attached;
	}

	protected void onAttach() {
		build();
		viewState = ViewState.Builded;
		localize();
		viewState = ViewState.Localized;
	}

	@Override
	public void detach() {
		I18NHolder.get().removeLocaleChangedListener(this);
		super.detach();
		viewState = ViewState.Detached;
	}

}
