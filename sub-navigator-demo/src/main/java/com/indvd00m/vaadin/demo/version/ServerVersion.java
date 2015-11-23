package com.indvd00m.vaadin.demo.version;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @date Nov 23, 2015 7:11:36 PM
 *
 */
public class ServerVersion {
	private static final String BUNDLE_NAME = "version";

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private ServerVersion() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}

	public static String getVersion() {
		return getString("version");
	}
}
