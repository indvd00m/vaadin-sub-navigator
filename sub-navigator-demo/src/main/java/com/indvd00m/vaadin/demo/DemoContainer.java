package com.indvd00m.vaadin.demo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.indvd00m.vaadin.demo.views.DemoDescriptionView;
import com.indvd00m.vaadin.demo.views.DynamicContainer1;
import com.indvd00m.vaadin.demo.views.DynamicContainer2;
import com.indvd00m.vaadin.demo.views.DynamicContainer3;
import com.indvd00m.vaadin.demo.views.TabContainer1;
import com.indvd00m.vaadin.navigator.api.ISubNavigator;
import com.indvd00m.vaadin.navigator.api.event.IViewStatusChangeEvent;
import com.indvd00m.vaadin.navigator.api.event.IViewStatusChangeListener;
import com.indvd00m.vaadin.navigator.api.view.ISubContainer;
import com.indvd00m.vaadin.navigator.api.view.ISubTitled;
import com.indvd00m.vaadin.navigator.api.view.ISubView;
import com.indvd00m.vaadin.navigator.api.view.ViewStatus;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.VerticalLayout;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Nov 16, 2015 2:12:11 PM
 *
 */
@SuppressWarnings("serial")
public class DemoContainer extends VerticalLayout implements ISubContainer, ISubTitled, IViewStatusChangeListener, SelectedTabChangeListener {

	ISubNavigator subNavigator;

	List<IViewStatusChangeEvent> eventsBeforeBuild = new ArrayList<IViewStatusChangeEvent>();
	StringBuffer log = new StringBuffer();
	Label logLabel;
	Panel logPanel;
	TabSheet ts;

	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	int logCount = 0;

	@Override
	public void clean() {
		removeAllComponents();
		logLabel = null;
		logPanel = null;
	}

	@Override
	public String getRelativePath() {
		return "demo";
	}

	@Override
	public void build() {
		subNavigator = ((SubNavigatorUI) getUI()).getSubNavigator();

		setMargin(true);
		setSpacing(true);
		setSizeFull();

		HorizontalLayout hl = new HorizontalLayout();
		hl.setSpacing(true);
		addComponent(hl);

		Link home = new Link("Home", new ExternalResource("./"));
		hl.addComponent(home);
		hl.setComponentAlignment(home, Alignment.MIDDLE_CENTER);

		Button clearLog = new Button("Clear log");
		clearLog.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				log.setLength(0);
				logLabel.setValue(log.toString());
				logCount = 0;
			}

		});
		hl.addComponent(clearLog);
		hl.setComponentAlignment(clearLog, Alignment.MIDDLE_CENTER);

		ts = new TabSheet();
		ts.setCaption("Tab container");
		ts.setImmediate(true);
		ts.setSizeFull();
		addComponent(ts);
		setExpandRatio(ts, 2f);

		logLabel = new Label();
		logLabel.setContentMode(ContentMode.PREFORMATTED);
		logPanel = new Panel("SubView status change log");
		logPanel.setSizeFull();
		logPanel.setContent(logLabel);
		addComponent(logPanel);
		setExpandRatio(logPanel, 1f);
		logPanel.setScrollTop(Integer.MAX_VALUE);

		for (IViewStatusChangeEvent event : eventsBeforeBuild) {
			viewStatusChanged(event);
		}
		synchronized (this) {
			eventsBeforeBuild.clear();
		}

		addView(new DemoDescriptionView(), "Description", FontAwesome.INFO);
		addView(new TabContainer1(), "Container 1", FontAwesome.LEVEL_UP);
		addView(new DynamicContainer1(), "Dynamic container 1", FontAwesome.LEVEL_DOWN);
		addView(new DynamicContainer2(), "Dynamic container 2", FontAwesome.ALIGN_LEFT);
		addView(new DynamicContainer3(), "Dynamic container 3", FontAwesome.BAN);

		ts.addSelectedTabChangeListener(this);
	}

	void addView(ISubView view, String caption, Resource icon) {
		subNavigator.addView(this, view);
		ts.addTab(view, caption, icon);
	}

	@Override
	public void viewStatusChanged(IViewStatusChangeEvent event) {
		if (logLabel != null && logPanel != null) {
			logCount++;
			ISubView view = event.getView();
			String date = sdf.format(event.getEventDate());
			String viewPath = view.getRelativePath();
			if (subNavigator.contains(view))
				viewPath = subNavigator.getPath(view);
			String status = event.getCurrentStatus().name();
			String text = String.format("%04d. %s \"%s\": %s\n", logCount, date, viewPath, status);
			log.append(text);
			logLabel.setValue(log.toString());
			logPanel.setScrollTop(Integer.MAX_VALUE);

			if (view instanceof AbstractComponent) {
				AbstractComponent component = (AbstractComponent) view;
				List<ViewStatus> statusHistory = event.getStatusHistory();
				String history = String.format("Status history for view at \"%s\": %s", viewPath, statusHistory.toString());
				component.setDescription(history);
			}
		} else {
			synchronized (this) {
				eventsBeforeBuild.add(event);
			}
		}
	}

	@Override
	public ISubView getSelectedView() {
		return (ISubView) ts.getSelectedTab();
	}

	@Override
	public void setSelectedView(ISubView view) {
		ts.setSelectedTab(view);
	}

	@Override
	public void selectedTabChange(SelectedTabChangeEvent event) {
		subNavigator.notifySelectedChangeDirected(this);
	}

	@Override
	public String getRelativeTitle() {
		return "Demo";
	}

}
