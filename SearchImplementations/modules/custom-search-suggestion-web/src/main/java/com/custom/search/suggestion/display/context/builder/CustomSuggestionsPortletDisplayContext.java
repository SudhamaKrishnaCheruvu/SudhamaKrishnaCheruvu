package com.custom.search.suggestion.display.context.builder;

import java.util.Collections;
import java.util.List;

/**
 * The Class CustomSuggestionsPortletDisplayContext.
 *
 * @author Dhivakar Sengottaiyan
 */
public class CustomSuggestionsPortletDisplayContext {

	/**
	 * Gets the related queries suggestions.
	 *
	 * @return the related queries suggestions
	 */
	public List<CustomSuggestionDisplayContext> getRelatedQueriesSuggestions() {
		return _relatedQueriesSuggestions;
	}

	/**
	 * Gets the spell check suggestion.
	 *
	 * @return the spell check suggestion
	 */
	public CustomSuggestionDisplayContext getSpellCheckSuggestion() {
		return _spellCheckSuggestion;
	}

	/**
	 * Checks for related queries suggestions.
	 *
	 * @return true, if successful
	 */
	public boolean hasRelatedQueriesSuggestions() {
		return _hasRelatedQueriesSuggestions;
	}

	/**
	 * Checks for spell check suggestion.
	 *
	 * @return true, if successful
	 */
	public boolean hasSpellCheckSuggestion() {
		return _hasSpellCheckSuggestion;
	}

	/**
	 * Checks if is related queries suggestions enabled.
	 *
	 * @return true, if is related queries suggestions enabled
	 */
	public boolean isRelatedQueriesSuggestionsEnabled() {
		return _relatedQueriesSuggestionsEnabled;
	}

	/**
	 * Checks if is spell check suggestion enabled.
	 *
	 * @return true, if is spell check suggestion enabled
	 */
	public boolean isSpellCheckSuggestionEnabled() {
		return _spellCheckSuggestionEnabled;
	}

	/**
	 * Sets the checks for related queries suggestions.
	 *
	 * @param hasRelatedQueriesSuggestions the new checks for related queries suggestions
	 */
	public void setHasRelatedQueriesSuggestions(
		boolean hasRelatedQueriesSuggestions) {

		_hasRelatedQueriesSuggestions = hasRelatedQueriesSuggestions;
	}

	/**
	 * Sets the checks for spell check suggestion.
	 *
	 * @param hasSpellCheckSuggestion the new checks for spell check suggestion
	 */
	public void setHasSpellCheckSuggestion(boolean hasSpellCheckSuggestion) {
		_hasSpellCheckSuggestion = hasSpellCheckSuggestion;
	}

	/**
	 * Sets the related queries suggestions.
	 *
	 * @param relatedQueriesSuggestions the new related queries suggestions
	 */
	public void setRelatedQueriesSuggestions(
		List<CustomSuggestionDisplayContext> relatedQueriesSuggestions) {

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
	 * Sets the spell check suggestion.
	 *
	 * @param spellCheckSuggesion the new spell check suggestion
	 */
	public void setSpellCheckSuggestion(
		CustomSuggestionDisplayContext spellCheckSuggesion) {

		_spellCheckSuggestion = spellCheckSuggesion;
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

	private boolean _hasRelatedQueriesSuggestions;
	
	private boolean _hasSpellCheckSuggestion;
	
	private List<CustomSuggestionDisplayContext> _relatedQueriesSuggestions =
		Collections.emptyList();
	
	private boolean _relatedQueriesSuggestionsEnabled;
	
	private CustomSuggestionDisplayContext _spellCheckSuggestion;
	
	private boolean _spellCheckSuggestionEnabled;

}