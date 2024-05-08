package com.custom.search.suggestion.helper;

import com.custom.search.suggestion.util.SearchStringUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.Optional;

import javax.portlet.PortletPreferences;

/**
 * The Class PortletPreferencesHelper.
 *
 * @author Dhivakar Sengottaiyan
 */
public class PortletPreferencesHelper {

	/**
	 * Instantiates a new portlet preferences helper.
	 *
	 * @param portletPreferencesOptional the portlet preferences optional
	 */
	public PortletPreferencesHelper(
		Optional<PortletPreferences> portletPreferencesOptional) {

		_portletPreferencesOptional = portletPreferencesOptional;
	}

	/**
	 * Gets the boolean.
	 *
	 * @param key the key
	 * @return the boolean
	 */
	public Optional<Boolean> getBoolean(String key) {
		Optional<String> valueOptional = _getValue(key);

		return valueOptional.map(GetterUtil::getBoolean);
	}

	/**
	 * Gets the boolean.
	 *
	 * @param key the key
	 * @param defaultValue the default value
	 * @return the boolean
	 */
	public boolean getBoolean(String key, boolean defaultValue) {
		Optional<Boolean> valueOptional = getBoolean(key);

		return valueOptional.orElse(defaultValue);
	}

	/**
	 * Gets the integer.
	 *
	 * @param key the key
	 * @return the integer
	 */
	public Optional<Integer> getInteger(String key) {
		Optional<String> valueOptional = _getValue(key);

		return valueOptional.map(GetterUtil::getInteger);
	}

	/**
	 * Gets the integer.
	 *
	 * @param key the key
	 * @param defaultValue the default value
	 * @return the integer
	 */
	public int getInteger(String key, int defaultValue) {
		Optional<Integer> valueOptional = getInteger(key);

		return valueOptional.orElse(defaultValue);
	}

	/**
	 * Gets the string.
	 *
	 * @param key the key
	 * @return the string
	 */
	public Optional<String> getString(String key) {
		return _getValue(key);
	}

	/**
	 * Gets the string.
	 *
	 * @param key the key
	 * @param defaultValue the default value
	 * @return the string
	 */
	public String getString(String key, String defaultValue) {
		Optional<String> valueOptional = getString(key);

		return valueOptional.orElse(defaultValue);
	}

	/**
	 * Gets the value.
	 *
	 * @param key the key
	 * @return the optional
	 */
	private Optional<String> _getValue(String key) {
		return _portletPreferencesOptional.flatMap(
			portletPreferences -> SearchStringUtil.maybe(
				portletPreferences.getValue(key, StringPool.BLANK)));
	}

	private final Optional<PortletPreferences> _portletPreferencesOptional;

}