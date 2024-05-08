package com.custom.search.suggestion.portlet;

import com.custom.search.suggestion.helper.PortletPreferencesHelper;

import java.util.Optional;

import javax.portlet.PortletPreferences;

/**
 * The Class SuggestionsPortletPreferencesImpl.
 *
 * @author Dhivakar Sengottaiyan
 */
public class SuggestionsPortletPreferencesImpl
	implements SuggestionsPortletPreferences {

	/**
	 * Instantiates a new suggestions portlet preferences impl.
	 *
	 * @param portletPreferencesOptional the portlet preferences optional
	 */
	public SuggestionsPortletPreferencesImpl(
		Optional<PortletPreferences> portletPreferencesOptional) {

		_portletPreferencesHelper = new PortletPreferencesHelper(
			portletPreferencesOptional);
	}

	@Override
	public int getQueryIndexingThreshold() {
		return _portletPreferencesHelper.getInteger(
			PREFERENCE_KEY_QUERY_INDEXING_THRESHOLD, 50);
	}

	@Override
	public int getRelatedQueriesSuggestionsDisplayThreshold() {
		return _portletPreferencesHelper.getInteger(
			PREFERENCE_KEY_RELATED_QUERIES_SUGGESTIONS_DISPLAY_THRESHOLD, 50);
	}

	@Override
	public int getRelatedQueriesSuggestionsMax() {
		return _portletPreferencesHelper.getInteger(
			PREFERENCE_KEY_RELATED_QUERIES_SUGGESTIONS_MAX, 10);
	}

	@Override
	public int getSpellCheckSuggestionDisplayThreshold() {
		return _portletPreferencesHelper.getInteger(
			PREFERENCE_KEY_SPELL_CHECK_SUGGESTION_DISPLAY_THRESHOLD, 50);
	}

	@Override
	public boolean isQueryIndexingEnabled() {
		return _portletPreferencesHelper.getBoolean(
			PREFERENCE_KEY_QUERY_INDEXING_ENABLED, false);
	}

	@Override
	public boolean isRelatedQueriesSuggestionsEnabled() {
		return _portletPreferencesHelper.getBoolean(
			PREFERENCE_KEY_RELATED_QUERIES_SUGGESTIONS_ENABLED, false);
	}

	@Override
	public boolean isSpellCheckSuggestionEnabled() {
		return _portletPreferencesHelper.getBoolean(
			PREFERENCE_KEY_SPELL_CHECK_SUGGESTION_ENABLED, false);
	}

	private final PortletPreferencesHelper _portletPreferencesHelper;

}