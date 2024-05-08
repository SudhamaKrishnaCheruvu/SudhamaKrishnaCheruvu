package com.custom.search.suggestion.portlet;

import com.custom.search.suggestion.conf.CustomSearchSuggestionConfiguration;
import com.custom.search.suggestion.conf.RBIDidYouMeanConfiguration;
import com.custom.search.suggestion.constants.CustomSearchSuggestionConstants;
import com.custom.search.suggestion.constants.CustomSearchSuggestionWebPortletKeys;
import com.custom.search.suggestion.display.context.builder.CustomSuggestionsPortletDisplayContext;
import com.custom.search.suggestion.display.context.builder.CustomSuggestionsPortletDisplayContextBuilder;
import com.custom.search.suggestion.helper.PortletSharedRequestHelper;
import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Html;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchRequest;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchResponse;
import com.liferay.portal.search.web.search.request.SearchSettings;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * The Class CustomSearchSuggestionWebPortlet.
 *
 * @author Dhivakar Sengottaiyan
 */
@Component(immediate = true, configurationPid = CustomSearchSuggestionConstants.CONFIGURATION_ID, property = {
		"com.liferay.portlet.display-category=category.sample", "com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.instanceable=true", "javax.portlet.display-name=CustomSearchSuggestionWeb",
		"javax.portlet.init-param.template-path=/", "javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + CustomSearchSuggestionWebPortletKeys.CUSTOMSEARCHSUGGESTIONWEB,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user" }, service = Portlet.class)
public class CustomSearchSuggestionWebPortlet extends MVCPortlet {

	private static final Log log = LogFactoryUtil.getLog(CustomSearchSuggestionWebPortlet.class);

	/*
	 * This method is used to prepare render attributes and setting values to the
	 * request
	 *
	 * @param renderRequest
	 * 
	 * @param renderResponse
	 * 
	 * @throws IOException
	 * 
	 * @throws PortletException
	 */
	@Override
	public void render(RenderRequest renderRequest, RenderResponse renderResponse)
			throws IOException, PortletException {
		ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY);

		RBIDidYouMeanConfiguration rbiDidYouMeanConfiguration = null;
		String suggestionReponse = StringPool.BLANK;
		try {
			rbiDidYouMeanConfiguration = ConfigurationProviderUtil
					.getSystemConfiguration(RBIDidYouMeanConfiguration.class);
		} catch (ConfigurationException e) {
			log.error(e);
		}
		String keyword = CustomSearchSuggestionUtil.getQueryParams(themeDisplay.getURLCurrent(), "q");
		if (Validator.isNotNull(rbiDidYouMeanConfiguration)) {
			suggestionReponse = CustomSearchSuggestionUtil.getNearestSuggestionResponse(keyword,
					rbiDidYouMeanConfiguration, renderRequest);
		}

		String suggestedKeyword = StringPool.BLANK;
		try {
			if (!suggestionReponse.isEmpty()) {
				JSONObject responseJsonObject = JSONFactoryUtil.createJSONObject(suggestionReponse);
				JSONObject suggestResponseObj = (JSONObject) responseJsonObject.get("suggest");
				suggestResponseObj = (JSONObject) ((JSONArray) suggestResponseObj.get("suggest")).get(0);
				JSONArray optionsArray = (JSONArray) suggestResponseObj.get("options");

				if (optionsArray.length() != 0) {
					suggestedKeyword = ((JSONObject) optionsArray.get(0)).getString("text");

					String redirectURL = themeDisplay.getPortalURL() + themeDisplay.getPathFriendlyURLPublic()
							+ themeDisplay.getScopeGroup().getFriendlyURL() + themeDisplay.getLayout().getFriendlyURL()
							+ StringPool.QUESTION + "q" + StringPool.EQUAL + suggestedKeyword
							+ "&type=com.liferay.journal.model.JournalArticle&type=com.liferay.portal.kernel.model.Layout&orderBy=newest";

					List<Document> hitDocuments = getSearchResultsDocuments(suggestedKeyword,
							themeDisplay.getScopeGroupId(), themeDisplay.getCompanyId(),
							renderRequest.getPreferences());
					renderRequest.setAttribute("hitDocuments", hitDocuments);
					renderRequest.setAttribute("redirectUrl", redirectURL);
					renderRequest.setAttribute("suggestedKeyword", suggestedKeyword);
				}
			}
		} catch (Exception e) {
			log.error("Error while fetching and reading suggestion");
		}

		renderRequest.setAttribute(CustomSearchSuggestionConfiguration.class.getName(),
				_customSearchSuggestionConfiguration);

		super.render(renderRequest, renderResponse);
	}

	@Reference
	protected Html html;

	@Reference
	protected Portal portal;

	@Reference
	protected PortletSharedRequestHelper portletSharedRequestHelper;

	@Reference
	protected PortletSharedSearchRequest portletSharedSearchRequest;

	/**
	 * Builds the display context.
	 *
	 * @param suggestionsPortletPreferences the suggestions portlet preferences
	 * @param portletSharedSearchResponse   the portlet shared search response
	 * @param renderRequest                 the render request
	 * @return the custom suggestions portlet display context
	 */
	private CustomSuggestionsPortletDisplayContext _buildDisplayContext(
			SuggestionsPortletPreferences suggestionsPortletPreferences,
			PortletSharedSearchResponse portletSharedSearchResponse, RenderRequest renderRequest) {

		CustomSuggestionsPortletDisplayContextBuilder suggestionsPortletDisplayContextBuilder = new CustomSuggestionsPortletDisplayContextBuilder(
				html);

		_copy(portletSharedSearchResponse::getKeywordsOptional, suggestionsPortletDisplayContextBuilder::setKeywords);

		SearchSettings searchSettings = portletSharedSearchResponse.getSearchSettings();

		_copy(searchSettings::getKeywordsParameterName,
				suggestionsPortletDisplayContextBuilder::setKeywordsParameterName);

		suggestionsPortletDisplayContextBuilder
				.setRelatedQueriesSuggestions(portletSharedSearchResponse.getRelatedQueriesSuggestions());

		suggestionsPortletDisplayContextBuilder.setRelatedQueriesSuggestionsEnabled(true);
		suggestionsPortletDisplayContextBuilder.setSearchURL(portletSharedRequestHelper.getCompleteURL(renderRequest));

		_copy(portletSharedSearchResponse::getSpellCheckSuggestionOptional,
				suggestionsPortletDisplayContextBuilder::setSpellCheckSuggestion);

		suggestionsPortletDisplayContextBuilder
				.setSpellCheckSuggestionEnabled(suggestionsPortletPreferences.isSpellCheckSuggestionEnabled());

		return suggestionsPortletDisplayContextBuilder.build();
	}

	/**
	 * Copy.
	 *
	 * @param <T>  the generic type
	 * @param from the from
	 * @param to   the to
	 */
	private <T> void _copy(Supplier<Optional<T>> from, Consumer<T> to) {
		Optional<T> optional = from.get();

		optional.ifPresent(to);
	}

	private List<Document> getSearchResultsDocuments(String keywords, long groupId, long companyId,
			PortletPreferences portletPreferences) {
		long[] groupIds = { groupId };
		List<Document> results = null;
		String blueprintId = portletPreferences.getValue(CustomSearchSuggestionConstants.PREFERENCES_BLUEPRINT_ID,
				_customSearchSuggestionConfiguration.blueprintId());
		try {
			SearchRequestBuilder searchRequestBuilder = searchRequestBuilderFactory.builder();
			searchRequestBuilder.modelIndexerClassNames(JournalArticle.class.getName(), Layout.class.getName());
			searchRequestBuilder.withSearchContext(searchContext -> {
				searchContext.setCompanyId(companyId);
				searchContext.setGroupIds(groupIds);
				searchContext.setKeywords(keywords);
				searchContext.setAttribute("search.experiences.blueprint.id", blueprintId);
				searchContext.setAttribute("custom.orderBy", "newest");
			});

			searchRequestBuilder.from(0);
			searchRequestBuilder.size(10);
			searchRequestBuilder.highlightEnabled(true);

			com.liferay.portal.search.query.BooleanQuery booleanQuery = queries.booleanQuery();
			SearchRequest searchRequest = searchRequestBuilder.query(booleanQuery).build();
			SearchResponse searchResponse = searcher.search(searchRequest);
			results = searchResponse.getDocuments71();

		} catch (Exception exception) {
			log.error("Error while fetching records for the suggested word");
		}

		return results;
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_customSearchSuggestionConfiguration = ConfigurableUtil
				.createConfigurable(CustomSearchSuggestionConfiguration.class, properties);
	}

	private volatile CustomSearchSuggestionConfiguration _customSearchSuggestionConfiguration;

	@Reference
	protected Searcher searcher;

	@Reference
	protected Queries queries;

	@Reference
	protected SearchRequestBuilderFactory searchRequestBuilderFactory;

}