package com.custom.search.suggestion.display.context.builder;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.util.Html;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The Class CustomSuggestionsPortletDisplayContextBuilder.
 *
 * @author Dhivakar Sengottaiyan
 */
public class CustomSuggestionsPortletDisplayContextBuilder {

	/**
	 * Instantiates a new custom suggestions portlet display context builder.
	 *
	 * @param html the html
	 */
	public CustomSuggestionsPortletDisplayContextBuilder(Html html) {
		_html = html;
	}

	/**
	 * Builds the.
	 *
	 * @return the custom suggestions portlet display context
	 */
	public CustomSuggestionsPortletDisplayContext build() {
		CustomSuggestionsPortletDisplayContext suggestionsPortletDisplayContext =
			new CustomSuggestionsPortletDisplayContext();

		buildRelatedQueriesSuggestions(suggestionsPortletDisplayContext);
		buildSpellCheckSuggestion(suggestionsPortletDisplayContext);

		return suggestionsPortletDisplayContext;
	}

	/**
	 * Sets the keywords.
	 *
	 * @param keywords the new keywords
	 */
	public void setKeywords(String keywords) {
		_keywords = keywords;
	}

	/**
	 * Sets the keywords parameter name.
	 *
	 * @param keywordsParameterName the new keywords parameter name
	 */
	public void setKeywordsParameterName(String keywordsParameterName) {
		_keywordsParameterName = keywordsParameterName;
	}

	/**
	 * Sets the related queries suggestions.
	 *
	 * @param relatedQueriesSuggestions the new related queries suggestions
	 */
	public void setRelatedQueriesSuggestions(
		List<String> relatedQueriesSuggestions) {

		_relatedQueriesSuggestions = relatedQueriesSuggestions;
	}

	/**
	 * Sets the related queries suggestions enabled.
	 *
	 * @param relatedQueriesSuggestionsEnabled the new related queries suggestions enabled
	 */
	public void setRelatedQueriesSuggestionsEnabled(
		boolean relatedQueriesSuggestionsEnabled) {

		_relatedQueriesSuggestionsEnabled = relatedQueriesSuggestionsEnabled;
	}

	/**
	 * Sets the search URL.
	 *
	 * @param searchURL the new search URL
	 */
	public void setSearchURL(String searchURL) {
		_searchURL = searchURL;
	}

	/**
	 * Sets the spell check suggestion.
	 *
	 * @param spellCheckSuggestion the new spell check suggestion
	 */
	public void setSpellCheckSuggestion(String spellCheckSuggestion) {
		_spellCheckSuggestion = spellCheckSuggestion;
	}

	/**
	 * Sets the spell check suggestion enabled.
	 *
	 * @param spellCheckSuggestionEnabled the new spell check suggestion enabled
	 */
	public void setSpellCheckSuggestionEnabled(
		boolean spellCheckSuggestionEnabled) {

		_spellCheckSuggestionEnabled = spellCheckSuggestionEnabled;
	}

	/**
	 * Builds the related queries suggestions.
	 *
	 * @param suggestionsPortletDisplayContext the suggestions portlet display context
	 */
	protected void buildRelatedQueriesSuggestions(
		CustomSuggestionsPortletDisplayContext suggestionsPortletDisplayContext) {

//		if (!_relatedQueriesSuggestionsEnabled) {
//			return;
//		}

		suggestionsPortletDisplayContext.setRelatedQueriesSuggestionsEnabled(
			true);

		List<CustomSuggestionDisplayContext> relatedQueriesSuggestions =
			_buildRelatedQueriesSuggestions();

		suggestionsPortletDisplayContext.setHasRelatedQueriesSuggestions(
			!relatedQueriesSuggestions.isEmpty());
		suggestionsPortletDisplayContext.setRelatedQueriesSuggestions(
			relatedQueriesSuggestions);
	}

	/**
	 * Builds the spell check suggestion.
	 *
	 * @param suggestionsPortletDisplayContext the suggestions portlet display context
	 */
	protected void buildSpellCheckSuggestion(
		CustomSuggestionsPortletDisplayContext suggestionsPortletDisplayContext) {

//		if (!_spellCheckSuggestionEnabled) {
//			return;
//		}

		suggestionsPortletDisplayContext.setSpellCheckSuggestionEnabled(true);

		CustomSuggestionDisplayContext suggestionDisplayContext =
			_buildSuggestionDisplayContext(_spellCheckSuggestion);

		if (suggestionDisplayContext == null) {
			return;
		}

		suggestionsPortletDisplayContext.setHasSpellCheckSuggestion(true);
		suggestionsPortletDisplayContext.setSpellCheckSuggestion(
			suggestionDisplayContext);
	}

	/**
	 * Builds the formatted keywords.
	 *
	 * @param keywordsSuggestionHolder the keywords suggestion holder
	 * @return the string
	 */
	private String _buildFormattedKeywords(
		KeywordsSuggestionHolder keywordsSuggestionHolder) {

		List<String> suggestedKeywords =
			keywordsSuggestionHolder.getSuggestedKeywords();

		Stream<String> stream = suggestedKeywords.stream();

		return stream.map(
			keyword -> _formatSuggestedKeyword(
				keyword, keywordsSuggestionHolder.hasChanged(keyword))
		).collect(
			Collectors.joining(StringPool.SPACE)
		);
	}

	/**
	 * Builds the related queries suggestions.
	 *
	 * @return the list
	 */
	private List<CustomSuggestionDisplayContext> _buildRelatedQueriesSuggestions() {
		Stream<String> stream = _relatedQueriesSuggestions.stream();

		return stream.map(
			this::_buildSuggestionDisplayContext
		).filter(
			Objects::nonNull
		).collect(
			Collectors.toList()
		);
	}

	/**
	 * Builds the search URL.
	 *
	 * @param keywordsSuggestionHolder the keywords suggestion holder
	 * @return the string
	 */
	private String _buildSearchURL(
		KeywordsSuggestionHolder keywordsSuggestionHolder) {

		String parameterValue = StringUtil.merge(
			keywordsSuggestionHolder.getSuggestedKeywords(), StringPool.SPACE);

		return HttpComponentsUtil.setParameter(
			_searchURL, _keywordsParameterName, parameterValue);
	}

	/**
	 * Builds the suggestion display context.
	 *
	 * @param suggestion the suggestion
	 * @return the custom suggestion display context
	 */
	private CustomSuggestionDisplayContext _buildSuggestionDisplayContext(
		String suggestion) {

		if (!_isValidSuggestion(suggestion)) {
			return null;
		}

		CustomSuggestionDisplayContext suggestionDisplayContext =
			new CustomSuggestionDisplayContext();

		KeywordsSuggestionHolder keywordsSuggestionHolder =
			new KeywordsSuggestionHolder(suggestion, _keywords);

		suggestionDisplayContext.setSuggestedKeywordsFormatted(
			_buildFormattedKeywords(keywordsSuggestionHolder));

		suggestionDisplayContext.setURL(
			_buildSearchURL(keywordsSuggestionHolder));

		return suggestionDisplayContext;
	}

	/**
	 * Format suggested keyword.
	 *
	 * @param keyword the keyword
	 * @param changed the changed
	 * @return the string
	 */
	private String _formatSuggestedKeyword(String keyword, boolean changed) {
		StringBundler sb = new StringBundler(5);

		sb.append("<span class=\"");

		String keywordCssClass = "unchanged-keyword";

		if (changed) {
			keywordCssClass = "changed-keyword";
		}

		sb.append(keywordCssClass);

		sb.append("\">");
		sb.append(_html.escape(keyword));
		sb.append("</span>");

		return sb.toString();
	}

	/**
	 * Checks if is valid suggestion.
	 *
	 * @param suggestion the suggestion
	 * @return true, if successful
	 */
	private boolean _isValidSuggestion(String suggestion) {
		if (Objects.equals(_keywords, suggestion) ||
			Validator.isNull(suggestion)) {

			return false;
		}

		return true;
	}

	private final Html _html;
	
	private String _keywords;
	
	private String _keywordsParameterName;
	
	private List<String> _relatedQueriesSuggestions;
	
	private boolean _relatedQueriesSuggestionsEnabled;
	
	private String _searchURL;
	
	private String _spellCheckSuggestion;
	
	private boolean _spellCheckSuggestionEnabled;

}