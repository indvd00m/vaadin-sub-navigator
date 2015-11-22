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
public class TestSubPath {

	@Test
	public void test() {
		UI ui = mock(UI.class);
		Page page = mock(Page.class);
		when(ui.getPage()).thenReturn(page);
		ISubContainer root = mock(ISubContainer.class);
		when(root.getRelativePath()).thenReturn("");
		ISubNavigator subNavigator = new SubNavigator(ui, root);

		assertEquals("test1/test2//", subNavigator.trimDelimiterLeft("test1/test2//"));
		assertEquals("test1/test2//", subNavigator.trimDelimiterLeft("/test1/test2//"));
		assertEquals("test1/test2//", subNavigator.trimDelimiterLeft("//test1/test2//"));

		assertEquals("//test1/test2", subNavigator.trimDelimiterRight("//test1/test2"));
		assertEquals("//test1/test2", subNavigator.trimDelimiterRight("//test1/test2/"));
		assertEquals("//test1/test2", subNavigator.trimDelimiterRight("//test1/test2//"));

		assertEquals("test1/test2", subNavigator.trimDelimiter("//test1/test2"));
		assertEquals("test1/test2", subNavigator.trimDelimiter("/test1/test2/"));
		assertEquals("test1/test2", subNavigator.trimDelimiter("/test1/test2//"));

		assertTrue(subNavigator.equalsPath("/test/test2//", "test/test2"));
		assertFalse(subNavigator.equalsPath("/test1/test2//", "test/test2"));
		assertFalse(subNavigator.equalsPath("/test/test2d//", "test/test2"));
		assertFalse(subNavigator.equalsPath("/test/test2/test3", "test/test2"));

		assertTrue(subNavigator.isSubPath("test1/test2", "/test1/test2/"));
		assertTrue(subNavigator.isSubPath("test1/test2/test3", "test1/test2"));
		assertFalse(subNavigator.isSubPath("test1/test2", "test1/test2/test3"));
		assertFalse(subNavigator.isSubPath("test0/test1/test2", "test1/test2/"));
	}

}
