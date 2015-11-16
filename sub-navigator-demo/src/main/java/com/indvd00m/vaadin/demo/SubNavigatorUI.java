package com.indvd00m.vaadin.demo;

import java.util.Arrays;
import java.util.Locale;

import javax.servlet.annotation.WebServlet;

import com.github.peholmst.i18n4vaadin.I18N;
import com.github.peholmst.i18n4vaadin.LocaleChangedEvent;
import com.github.peholmst.i18n4vaadin.LocaleChangedListener;
import com.github.peholmst.i18n4vaadin.simple.I18NProvidingUIStrategy;
import com.github.peholmst.i18n4vaadin.simple.SimpleI18N;
import com.github.peholmst.i18n4vaadin.util.I18NHolder;
import com.github.peholmst.i18n4vaadin.util.I18NProvider;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;

@Theme("valo")
@Title("SubNavigator Add-on Demo")
@SuppressWarnings("serial")
public class SubNavigatorUI extends UI implements LocaleChangedListener, I18NProvider {

	private I18N i18n = new SimpleI18N(Arrays.asList(new Locale("en"), new Locale("ru")));

	static {
		I18NHolder.setStrategy(new I18NProvidingUIStrategy());
	}

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = SubNavigatorUI.class)
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {
		Level1Container root = new Level1Container();
		Navigator navigator = new Navigator(this, (ViewDisplay) root);
		setNavigator(navigator);
		setContent(root);
		navigator.addProvider((ViewProvider) root);
		navigator.addView("", root);
		navigator.addView(root.getViewName(), root);

	}

	@Override
	public void attach() {
		super.attach();
		i18n.addLocaleChangedListener(this);
	}

	@Override
	public void detach() {
		i18n.removeLocaleChangedListener(this);
		super.detach();
	}

	@Override
	public I18N getI18N() {
		return i18n;
	}

	@Override
	public void localeChanged(LocaleChangedEvent event) {
		// TODO Auto-generated method stub

	}

}
