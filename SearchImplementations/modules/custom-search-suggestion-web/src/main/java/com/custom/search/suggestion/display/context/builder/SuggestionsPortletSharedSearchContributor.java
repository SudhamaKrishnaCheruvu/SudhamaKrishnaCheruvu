package com.custom.search.suggestion.display.context.builder;

import com.custom.search.suggestion.constants.CustomSearchSuggestionWebPortletKeys;
import com.custom.search.suggestion.portlet.SuggestionsPortletPreferences;
import com.custom.search.suggestion.portlet.SuggestionsPortletPreferencesImpl;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchContributor;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchSettings;

import org.osgi.service.component.annotations.Component;

/**
 * @author Dhivakar Sengottaiyan
 */
@Component(
	immediate = true,
	property = "javax.portlet.name=" + CustomSearchSuggestionWebPortletKeys.CUSTOMSEARCHSUGGESTIONWEB,
	service = PortletSharedSearchContributor.class
)
public class SuggestionsPortletSharedSearchContributor
	implements PortletSharedSearchContributor {

	@Override
	public void contribute(
		PortletSharedSearchSettings portletSharedSearchSettings) {
		SuggestionsPortletPreferences suggestionsPortletPreferences =
			new SuggestionsPortletPreferencesImpl(
				portletSharedSearchSettings.getPortletPreferencesOptional());
		_setUpQueryIndexing(
			suggestionsPortletPreferences, portletSharedSearchSettings);
		_setUpRelatedSuggestions(
			suggestionsPortletPreferences, portletSharedSearchSettings);
		_setUpSpellCheckSuggestion(
			suggestionsPortletPreferences, portletSharedSearchSettings);
		
	}

	private void _setUpQueryIndexing(
		SuggestionsPortletPreferences suggestionsPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		QueryConfig queryConfig = portletSharedSearchSettings.getQueryConfig();
		
		queryConfig.setQueryIndexingEnabled(true);
		queryConfig.setQueryIndexingThreshold(1);
	}

	private void _setUpRelatedSuggestions(
		SuggestionsPortletPreferences suggestionsPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		QueryConfig queryConfig = portletSharedSearchSettings.getQueryConfig();

		queryConfig.setQuerySuggestionEnabled(false);
		queryConfig.setQuerySuggestionScoresThreshold(
			50);
		queryConfig.setQuerySuggestionMax(
			100);
	}

	private void _setUpSpellCheckSuggestion(
		SuggestionsPortletPreferences suggestionsPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		QueryConfig queryConfig = portletSharedSearchSettings.getQueryConfig();

		queryConfig.setCollatedSpellCheckResultEnabled(
			true);

		queryConfig.setCollatedSpellCheckResultScoresThreshold(
			1);
	}

}