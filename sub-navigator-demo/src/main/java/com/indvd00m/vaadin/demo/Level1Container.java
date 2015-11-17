package com.indvd00m.vaadin.demo;

import java.text.SimpleDateFormat;

import com.indvd00m.vaadin.demo.loggable.LTabSubContainer;
import com.indvd00m.vaadin.navigator.SubView;
import com.indvd00m.vaadin.navigator.event.SubViewStateChangeEvent;
import com.indvd00m.vaadin.navigator.event.SubViewStateChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontAwesome;
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

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Nov 16, 2015 2:12:11 PM
 *
 */
@SuppressWarnings("serial")
public class Level1Container extends LTabSubContainer implements SubViewStateChangeListener {

	StringBuffer log = new StringBuffer();
	Label logLabel;
	Panel logPanel;
	TabSheet ts;

	SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	int stateCount = 0;

	@Override
	public TabSheet getTabSheet() {
		return ts;
	}

	@Override
	protected void clean() {
		removeAllComponents();
	}

	@Override
	public String getViewName() {
		return "level1";
	}

	@Override
	protected void build() {
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
			}

		});
		hl.addComponent(clearLog);
		hl.setComponentAlignment(clearLog, Alignment.MIDDLE_CENTER);

		ts = new TabSheet();
		ts.setCaption("Level 1 container");
		ts.setImmediate(true);
		ts.setSizeFull();
		addComponent(ts);
		setExpandRatio(ts, 3f);

		logLabel = new Label(log.toString());
		logLabel.setContentMode(ContentMode.PREFORMATTED);
		logPanel = new Panel("SubView state change log");
		logPanel.setSizeFull();
		logPanel.setContent(logLabel);
		addComponent(logPanel);
		setExpandRatio(logPanel, 1f);
		logPanel.setScrollTop(Integer.MAX_VALUE);

		addView(new Level2Container1(), "Level 2 container 1", FontAwesome.LEVEL_UP);
		addView(new Level2DynamicContainer1(), "Level 2 dynamic container 1", FontAwesome.LEVEL_DOWN);
		addView(new Level2DynamicContainer2(), "Level 2 dynamic container 2", FontAwesome.ALIGN_LEFT);
	}

	@Override
	public void subViewStateChanged(SubViewStateChangeEvent event) {
		stateCount++;
		final String str = String.format("%04d. %s %s: %s\n", stateCount, sdf.format(event.getEventDate()),
				((SubView) event.getSubView()).getFullPath(), event.getCurrentState().name());
		log.append(str);
		if (logLabel != null && logPanel != null) {
			logLabel.setValue(log.toString());
			logPanel.setScrollTop(Integer.MAX_VALUE);
		}
	}

}
