package com.liferay.custom.daterange.facet.display.context;

import com.liferay.custom.date.facet.portlet.CustomDateFacetConfiguration;

import java.io.Serializable;
import java.util.List;

/**
 * @author Lino Alves
 */
public class CustomDateFacetDisplayContext implements Serializable {

	public CustomDateFacetTermDisplayContext
		getCustomRangeModifiedFacetTermDisplayContext() {

		return _customDateFacetTermDisplayContext;
	}

	public CustomDateFacetTermDisplayContext
		getDefaultDateFacetTermDisplayContext() {

		return _defaultDateFacetTermDisplayContext;
	}

	public long getDisplayStyleGroupId() {
		return _displayStyleGroupId;
	}

	public CustomDateFacetCalendarDisplayContext
		getCustomDateFacetCalendarDisplayContext() {

		return _customDateFacetCalendarDisplayContext;
	}

	public CustomDateFacetConfiguration
		getCustomDateFacetConfiguration() {

		return _customDateFacetConfiguration;
	}

	public List<CustomDateFacetTermDisplayContext>
		getCustomDateFacetTermDisplayContexts() {

		return _customDateFacetTermDisplayContexts;
	}

	public String getPaginationStartParameterName() {
		return _paginationStartParameterName;
	}

	public boolean isNothingSelected() {
		return _nothingSelected;
	}

	public boolean isRenderNothing() {
		return _renderNothing;
	}

	public void setCalendarDisplayContext(
		CustomDateFacetCalendarDisplayContext
			customDateFacetCalendarDisplayContext) {

		_customDateFacetCalendarDisplayContext =
			customDateFacetCalendarDisplayContext;
	}

	public void setCustomDateFacetTermDisplayContext(
		CustomDateFacetTermDisplayContext customRangeTermDisplayContext) {

		_customDateFacetTermDisplayContext =
			customRangeTermDisplayContext;
	}

	public void setDefaultCustomDateFacetTermDisplayContext(
		CustomDateFacetTermDisplayContext defaultTermDisplayContext) {

		_defaultDateFacetTermDisplayContext = defaultTermDisplayContext;
	}

	public void setDisplayStyleGroupId(long displayStyleGroupId) {
		_displayStyleGroupId = displayStyleGroupId;
	}

	public void setCustomDateFacetConfiguration(
			CustomDateFacetConfiguration
			customDateFacetConfiguration) {

		_customDateFacetConfiguration =
				customDateFacetConfiguration;
	}

	public void setCustomDateFacetTermDisplayContexts(
		List<CustomDateFacetTermDisplayContext>
			customDateFacetTermDisplayContexts) {

		_customDateFacetTermDisplayContexts = customDateFacetTermDisplayContexts;
	}

	public void setNothingSelected(boolean nothingSelected) {
		_nothingSelected = nothingSelected;
	}

	public void setPaginationStartParameterName(
		String paginationStartParameterName) {

		_paginationStartParameterName = paginationStartParameterName;
	}

	public String getParameterName() {
		return _parameterName;
	}

	public void setParameterName(String parameterName) {
		this._parameterName = parameterName;
	}

	public void setRenderNothing(boolean renderNothing) {
		_renderNothing = renderNothing;
	}

	private CustomDateFacetTermDisplayContext
		_customDateFacetTermDisplayContext;
	private CustomDateFacetTermDisplayContext
		_defaultDateFacetTermDisplayContext;
	private long _displayStyleGroupId;
	private CustomDateFacetCalendarDisplayContext
		_customDateFacetCalendarDisplayContext;
	private CustomDateFacetConfiguration
	_customDateFacetConfiguration;
	private List<CustomDateFacetTermDisplayContext>
		_customDateFacetTermDisplayContexts;
	private boolean _nothingSelected;
	private String _paginationStartParameterName;
	private String _parameterName;
	private boolean _renderNothing;

}