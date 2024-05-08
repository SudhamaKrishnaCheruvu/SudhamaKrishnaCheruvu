package com.custom.search.suggestion.conf;

import com.custom.search.suggestion.constants.CustomSearchSuggestionConstants;

import aQute.bnd.annotation.metatype.Meta;

/**
 * @author Nishant Tankariya
 *
 */
@Meta.OCD(id = CustomSearchSuggestionConstants.CONFIGURATION_ID)
public interface CustomSearchSuggestionConfiguration {

	@Meta.AD(name = "blueprint-id", required = false, deflt = "50161710")
	String blueprintId();

}
