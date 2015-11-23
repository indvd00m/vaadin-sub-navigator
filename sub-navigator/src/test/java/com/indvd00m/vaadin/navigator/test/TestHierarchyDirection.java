package com.indvd00m.vaadin.navigator.test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;

import com.indvd00m.vaadin.navigator.SubNavigator;
import com.indvd00m.vaadin.navigator.api.HierarchyDirection;
import com.indvd00m.vaadin.navigator.api.ISubNavigator;
import com.indvd00m.vaadin.navigator.api.view.ISubContainer;
import com.vaadin.server.Page;
import com.vaadin.ui.UI;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Nov 21, 2015 6:33:22 PM
 *
 */
public class TestHierarchyDirection {

	@Test
	public void test() {
		UI ui = mock(UI.class);
		Page page = mock(Page.class);
		when(ui.getPage()).thenReturn(page);
		ISubContainer root = mock(ISubContainer.class);
		when(root.getRelativePath()).thenReturn("");
		ISubNavigator subNavigator = new SubNavigator(ui, root);

		assertEquals(HierarchyDirection.None, subNavigator.getDirection("/test1/test2/", "test1/test2"));
		assertEquals(HierarchyDirection.Up, subNavigator.getDirection("/test1/test2/test3/", "test1/test2"));
		assertEquals(HierarchyDirection.Down, subNavigator.getDirection("/test1/test2/test3/", "test1/test2/test3/test4"));
		assertEquals(HierarchyDirection.Nearby, subNavigator.getDirection("/test1/test2/test3/", "test1/test2/test3_1"));
		assertEquals(HierarchyDirection.Nearby, subNavigator.getDirection("/test1/test2/test3/", "test1/test2_1/test3"));
	}

}
