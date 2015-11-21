package com.indvd00m.vaadin.navigator.test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;

import com.indvd00m.vaadin.navigator.SubNavigator;
import com.indvd00m.vaadin.navigator.api.ISubContainer;
import com.indvd00m.vaadin.navigator.api.ISubNavigator;
import com.vaadin.server.Page;
import com.vaadin.ui.UI;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Nov 21, 2015 6:33:22 PM
 *
 */
public class TestDivergationNode {

	@Test
	public void test() {
		UI ui = mock(UI.class);
		Page page = mock(Page.class);
		when(ui.getPage()).thenReturn(page);
		ISubContainer rootContainer = mock(ISubContainer.class);
		ISubNavigator subNavigator = new SubNavigator(ui, rootContainer);

		assertTrue(subNavigator.equalsPath("test1/test2", subNavigator.getDivergationNode("test1/test2", "/test1/test2/")));
		assertTrue(subNavigator.equalsPath("test1/test2", subNavigator.getDivergationNode("test1/test2", "/test1/test2/test3")));
		assertFalse(subNavigator.equalsPath("test1", subNavigator.getDivergationNode("test1/test2", "/test1/test2/test3")));
		assertTrue(subNavigator.equalsPath("test1/test2", subNavigator.getDivergationNode("test1/test2", "/test1/test2/test3/test4")));
		assertTrue(subNavigator.equalsPath("test1/test2", subNavigator.getDivergationNode("/test1/test2/test3/test4", "test1/test2")));
		assertTrue(subNavigator.equalsPath("test1/test2", subNavigator.getDivergationNode("test1/test2/test3_1", "/test1/test2/test3/test4")));
		assertTrue(subNavigator.equalsPath("", subNavigator.getDivergationNode("test2/test3_1", "/test1/test2/test3/test4")));
		assertTrue(subNavigator.equalsPath("test1", subNavigator.getDivergationNode("test1/test2/test3_1", "/test1/test2_1/test3/test4")));

		assertArrayEquals(new String[] {}, subNavigator.getNodesBetween("test1/test2/test3_1", "/test1/test2/test3/test4").toArray());
		assertArrayEquals(new String[] {
				"test1/test2/test3/test4"
		}, subNavigator.getNodesBetween("test1/test2/test3", "/test1/test2/test3/test4").toArray());
		assertArrayEquals(new String[] {
				"test1/test2",
				"test1/test2/test3",
				"test1/test2/test3/test4"
		}, subNavigator.getNodesBetween("test1//", "/test1/test2/test3/test4").toArray());
	}

}
