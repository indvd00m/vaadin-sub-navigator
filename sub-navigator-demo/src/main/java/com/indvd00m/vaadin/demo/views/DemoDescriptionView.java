package com.indvd00m.vaadin.demo.views;

import java.util.List;

import com.indvd00m.vaadin.demo.SubNavigatorUI;
import com.indvd00m.vaadin.navigator.api.ISubNavigator;
import com.indvd00m.vaadin.navigator.api.view.ISubContainer;
import com.indvd00m.vaadin.navigator.api.view.ISubDynamicContainer;
import com.indvd00m.vaadin.navigator.api.view.ISubView;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Nov 23, 2015 5:33:44 PM
 *
 */
@SuppressWarnings("serial")
public class DemoDescriptionView extends VerticalLayout implements ISubView, ISubDynamicContainer, CloseListener {

	ISubNavigator subNavigator;
	ISubView selectedView = null;
	String redirectViewPath = "redirect-on-build";
	String redirectUrl = null;

	@Override
	public String getRelativePath() {
		return "description";
	}

	@Override
	public void clean() {
		removeAllComponents();
	}

	@Override
	public void build() {
		subNavigator = ((SubNavigatorUI) getUI()).getSubNavigator();

		setSpacing(true);
		setMargin(true);

		String info = "Try to navigate by mouse clicking, back/forward browser buttons, reloading (F5) or manual url editing.";
		Label label = new Label(info);
		addComponent(label);

		ISubContainer root = subNavigator.getRoot();
		List<ISubView> subViews = subNavigator.getSubViews(root);
		ISubView infoView = subViews.get(0);
		String url = subNavigator.getURL(infoView);

		Link link = new Link("Show info", new ExternalResource(url));
		addComponent(link);

		Link failLink = new Link("Link to not existed view", new ExternalResource(subNavigator.getURL("not/existed/view")));
		addComponent(failLink);

		redirectUrl = subNavigator.getPath(infoView);
		String urlToRedirectView = subNavigator.getURL(subNavigator.getPath(this) + subNavigator.getPathDelimiter() + redirectViewPath);
		Link redirectLink = new Link("Link to view with redirect", new ExternalResource(urlToRedirectView));
		addComponent(redirectLink);
	}

	@Override
	public ISubView getSelectedView() {
		return selectedView;
	}

	@Override
	public void setSelectedView(ISubView view) {
		selectedView = view;
		Window window = new Window();
		window.setModal(true);
		window.setWidth(300, Unit.PIXELS);
		window.setHeight(500, Unit.PIXELS);
		window.setContent(selectedView);
		window.setCaption("Window with view with redirect");
		window.addCloseListener(this);
		getUI().addWindow(window);
	}

	@Override
	public void deselectView(ISubView view) {
		Window window = (Window) selectedView.getParent();
		window.removeCloseListener(this);
		window.close();
		selectedView = null;
	}

	@Override
	public void windowClose(CloseEvent e) {
		selectedView = null;
		subNavigator.notifySelectedChangeDirected(this);
	}

	@Override
	public ISubView createView(String viewPathAndParameters) {
		if (!viewPathAndParameters.equals(redirectViewPath))
			return null;
		RedirectView view = new RedirectView(redirectUrl, redirectViewPath);
		return view;
	}

}
