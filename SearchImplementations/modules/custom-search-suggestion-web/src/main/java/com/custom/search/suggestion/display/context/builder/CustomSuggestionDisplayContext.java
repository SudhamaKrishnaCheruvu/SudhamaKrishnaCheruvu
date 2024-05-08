package com.custom.search.suggestion.display.context.builder;

/**
 * The Class CustomSuggestionDisplayContext.
 *
 * @author Dhivakar Sengottaiyan
 */
public class CustomSuggestionDisplayContext {

	/**
	 * Gets the suggested keywords formatted.
	 *
	 * @return the suggested keywords formatted
	 */
	public String getSuggestedKeywordsFormatted() {
		return _suggestedKeywordsFormatted;
	}

	/**
	 * Gets the url.
	 *
	 * @return the url
	 */
	public String getURL() {
		return _url;
	}

	/**
	 * Sets the suggested keywords formatted.
	 *
	 * @param suggestedKeywordsFormatted the new suggested keywords formatted
	 */
	public void setSuggestedKeywordsFormatted(
		String suggestedKeywordsFormatted) {

		_suggestedKeywordsFormatted = suggestedKeywordsFormatted;
	}

	/**
	 * Sets the url.
	 *
	 * @param url the new url
	 */
	public void setURL(String url) {
		_url = url;
	}

	private String _suggestedKeywordsFormatted;
	
	private String _url;

}