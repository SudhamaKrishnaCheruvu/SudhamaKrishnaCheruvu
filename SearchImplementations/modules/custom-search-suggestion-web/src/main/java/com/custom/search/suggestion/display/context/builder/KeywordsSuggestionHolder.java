package com.custom.search.suggestion.display.context.builder;

import com.liferay.portal.kernel.util.Validator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * The Class KeywordsSuggestionHolder.
 *
 * @author Dhivakar Sengottaiyan
 */
public class KeywordsSuggestionHolder {

	/**
	 * Instantiates a new keywords suggestion holder.
	 *
	 * @param suggestedKeywords the suggested keywords
	 * @param originalKeywords the original keywords
	 */
	public KeywordsSuggestionHolder(
		String suggestedKeywords, String originalKeywords) {

		this(suggestedKeywords, originalKeywords, _KEYWORDS_DELIMETER_REGEXP);
	}

	/**
	 * Instantiates a new keywords suggestion holder.
	 *
	 * @param suggestedKeywords the suggested keywords
	 * @param originalKeywords the original keywords
	 * @param keywordsDelimiterRegexp the keywords delimiter regexp
	 */
	public KeywordsSuggestionHolder(
		String suggestedKeywords, String originalKeywords,
		String keywordsDelimiterRegexp) {

		Pattern keywordsDelimiterRegexpPattern = Pattern.compile(
			keywordsDelimiterRegexp);

		if (Validator.isBlank(suggestedKeywords)) {
			_suggestedKeywords = Collections.emptyList();
		}
		else {
			_suggestedKeywords = Arrays.asList(
				keywordsDelimiterRegexpPattern.split(suggestedKeywords));
		}

		if (Validator.isNull(originalKeywords)) {
			_originalKeywords = Collections.emptyList();
		}
		else {
			_originalKeywords = Arrays.asList(
				keywordsDelimiterRegexpPattern.split(originalKeywords));
		}
	}

	/**
	 * Gets the suggested keywords.
	 *
	 * @return the suggested keywords
	 */
	public List<String> getSuggestedKeywords() {
		return _suggestedKeywords;
	}

	/**
	 * Checks for changed.
	 *
	 * @param suggestedKeyword the suggested keyword
	 * @return true, if successful
	 */
	public boolean hasChanged(String suggestedKeyword) {
		return !_originalKeywords.contains(suggestedKeyword);
	}

	private static final String _KEYWORDS_DELIMETER_REGEXP = "[ ]+";

	private final List<String> _originalKeywords;
	
	private final List<String> _suggestedKeywords;

}