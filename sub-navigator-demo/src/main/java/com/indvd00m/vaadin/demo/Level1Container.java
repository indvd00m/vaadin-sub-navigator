package com.indvd00m.vaadin.demo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.indvd00m.vaadin.navigator.api.ISubContainer;
import com.indvd00m.vaadin.navigator.api.ISubNavigator;
import com.indvd00m.vaadin.navigator.api.ISubView;
import com.indvd00m.vaadin.navigator.api.event.IVIewStatusChangeEvent;
import com.indvd00m.vaadin.navigator.api.event.IViewStatusChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.label.ContentMode;
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
public class Level1Container extends VerticalLayout implements ISubContainer, IViewStatusChangeListener, SelectedTabChangeListener {

	ISubNavigator subNavigator;

	List<IVIewStatusChangeEvent> eventsBeforeBuild = new ArrayList<IVIewStatusChangeEvent>();
	StringBuffer log = new StringBuffer();
	Label logLabel;
	Panel logPanel;
	TabSheet ts;

	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	int logCount = 0;

	@Override
	public void clean() {
		removeAllComponents();
	}

	@Override
	public String getViewName() {
		return "level1";
	}

	@Override
	public void build() {
		subNavigator = ((SubNavigatorUI) getUI()).getSubNavigator();

		setMargin(true);
		setSpacing(true);
		setSizeFull();

		Label info = new Label("Try to navigate by mouse clicking, back/forward browser buttons, reloading (F5) or manual url editing.");
		addComponent(info);

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
		ts.setCaption("Level 1 container");
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

		for (IVIewStatusChangeEvent event : eventsBeforeBuild) {
			viewStatusChanged(event);
		}
		eventsBeforeBuild.clear();

		addView(new Level2Container1(), "Level 2 container 1", FontAwesome.LEVEL_UP);
		addView(new Level2DynamicContainer1(), "Level 2 dynamic container 1", FontAwesome.LEVEL_DOWN);
		addView(new Level2DynamicContainer2(), "Level 2 dynamic container 2", FontAwesome.ALIGN_LEFT);
		addView(new Level2DynamicContainer3(), "Level 2 dynamic container 3", FontAwesome.BAN);

		ts.addSelectedTabChangeListener(this);
	}

	void addView(ISubView view, String caption, Resource icon) {
		subNavigator.register(this, view);
		ts.addTab(view, caption, icon);
	}

	@Override
	public void viewStatusChanged(IVIewStatusChangeEvent event) {
		if (logLabel != null && logPanel != null) {
			logCount++;
			String date = sdf.format(event.getEventDate());
			String path = subNavigator.getPath(event.getView());
			String status = event.getCurrentStatus().name();
			String text = String.format("%04d. %s %s: %s\n", logCount, date, path, status);
			log.append(text);
			logLabel.setValue(log.toString());
			logPanel.setScrollTop(Integer.MAX_VALUE);
		} else {
			eventsBeforeBuild.add(event);
		}
	}

	@Override
	public void detach() {
		subNavigator.removeViewStatusChangeListener(this);
	}

	@Override
	public ISubView getSelectedView() {
		if (ts == null || ts.getComponentCount() == 0)
			return null;
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

}
