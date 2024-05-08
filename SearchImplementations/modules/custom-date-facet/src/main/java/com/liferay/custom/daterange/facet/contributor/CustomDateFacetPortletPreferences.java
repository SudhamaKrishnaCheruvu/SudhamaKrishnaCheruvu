package com.liferay.custom.daterange.facet.contributor;

import com.liferay.portal.kernel.json.JSONArray;

/**
 * @author Lino Alves
 */
public interface CustomDateFacetPortletPreferences {

	public static final String PREFERENCE_KEY_FREQUENCIES_VISIBLE =
		"frequenciesVisible";

	public static final String PREFERENCE_KEY_PARAMETER_NAME = "publishDate";
	
	public static final String PREFERENCE_KEY_RANGES = "ranges";

	public JSONArray getRangesJSONArray();

	public String getRangesString();

	String getParameterName();

}