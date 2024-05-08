package com.liferay.custom.daterange.facet.display.context;

import com.liferay.custom.date.facet.portlet.CustomDateFacetConfiguration;
import com.liferay.custom.daterange.facet.builder.DateRangeFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.search.facet.collector.TermCollector;
import com.liferay.portal.kernel.search.facet.config.FacetConfiguration;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.CalendarFactory;
import com.liferay.portal.kernel.util.DateFormatFactory;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.stream.Stream;

import javax.portlet.RenderRequest;

/**
 * @author Lino Alves
 * @author Adam Brandizzi
 */
public class CustomDateFacetDisplayContextBuilder implements Serializable {

	public CustomDateFacetDisplayContextBuilder(
			CalendarFactory calendarFactory,
			DateFormatFactory dateFormatFactory, RenderRequest renderRequest, String fieldName)
		throws ConfigurationException {

		_calendarFactory = calendarFactory;
		_dateFormatFactory = dateFormatFactory;
		_fieldName = fieldName;
		_dateRangeFactory = new DateRangeFactory(dateFormatFactory);

		_themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

		_customDateFacetConfiguration =
			portletDisplay.getPortletInstanceConfiguration(
				CustomDateFacetConfiguration.class);
	}

	public CustomDateFacetDisplayContext build() {
		CustomDateFacetDisplayContext customDateFacetDisplayContext =
			new CustomDateFacetDisplayContext();

		if (_calendarFactory != null) {
			customDateFacetDisplayContext.setCalendarDisplayContext(
				_buildCalendarDisplayContext());
		}

		if ((_dateFormatFactory != null) && (_dateRangeFactory != null)) {
			customDateFacetDisplayContext.
				setCustomDateFacetTermDisplayContext(
					_buildCustomDateTermDisplayContext());
		}

		customDateFacetDisplayContext.setDefaultCustomDateFacetTermDisplayContext(
			_buildDefaultDateFacetTermDisplayContext());
		customDateFacetDisplayContext.setDisplayStyleGroupId(
			getDisplayStyleGroupId());
		customDateFacetDisplayContext.
			setCustomDateFacetConfiguration(
				_customDateFacetConfiguration);
		customDateFacetDisplayContext.setCustomDateFacetTermDisplayContexts(
			_buildTermDisplayContexts());
		customDateFacetDisplayContext.setNothingSelected(isNothingSelected());
		customDateFacetDisplayContext.setPaginationStartParameterName(
			_paginationStartParameterName);
		customDateFacetDisplayContext.setParameterName(_parameterName);
		customDateFacetDisplayContext.setRenderNothing(isRenderNothing());

		return customDateFacetDisplayContext;
	}

	public void setCurrentURL(String currentURL) {
		_currentURL = currentURL;
	}

	public void setFacet(Facet facet) {
		_facet = facet;
	}

	public void setFromParameterValue(String from) {
		_from = from;
	}

	public void setLocale(Locale locale) {
		_locale = locale;
	}

	public void setPaginationStartParameterName(
		String paginationStartParameterName) {

		_paginationStartParameterName = paginationStartParameterName;
	}

	public void setParameterName(String parameterName) {
		_parameterName = parameterName;
	}

	public void setParameterValues(String... parameterValues) {
		_selectedRanges = Arrays.asList(
			Objects.requireNonNull(parameterValues));
	}

	public void setTimeZone(TimeZone timeZone) {
		_timeZone = timeZone;
	}

	public void setToParameterValue(String to) {
		_to = to;
	}

	public void setTotalHits(int totalHits) {
		_totalHits = totalHits;
	}

	protected long getDisplayStyleGroupId() {
		long displayStyleGroupId = 0l;
		//	_customDateFacetPortletInstanceConfiguration.displayStyleGroupId();

		if (displayStyleGroupId <= 0) {
			displayStyleGroupId = _themeDisplay.getScopeGroupId();
		}

		return displayStyleGroupId;
	}

	protected int getFrequency(TermCollector termCollector) {
		if (termCollector != null) {
			return termCollector.getFrequency();
		}

		return 0;
	}

	protected TermCollector getTermCollector(String range) {
		if (_facet == null) {
			return null;
		}

		FacetCollector facetCollector = _facet.getFacetCollector();

		if (facetCollector == null) {
			return null;
		}

		return facetCollector.getTermCollector(range);
	}

	protected boolean isNothingSelected() {
		if (!_selectedRanges.isEmpty() ||
			(!Validator.isBlank(_from) && !Validator.isBlank(_to))) {

			return false;
		}

		return true;
	}

	protected boolean isRenderNothing() {
		if (_totalHits > 0) {
			return false;
		}

		return isNothingSelected();
	}

	private CustomDateFacetCalendarDisplayContext _buildCalendarDisplayContext() {
		CustomDateFacetCalendarDisplayContextBuilder
			customDateFacetCalendarDisplayContextBuilder =
				new CustomDateFacetCalendarDisplayContextBuilder(
					_calendarFactory);

		Stream<String> selectedRangesStream = _selectedRanges.stream();

		selectedRangesStream.filter(
			s -> s.startsWith(StringPool.OPEN_CURLY_BRACE)
		).findAny(
		).ifPresent(
				customDateFacetCalendarDisplayContextBuilder::setRangeString
		);

		customDateFacetCalendarDisplayContextBuilder.setFrom(_from);
		customDateFacetCalendarDisplayContextBuilder.setLocale(_locale);
		customDateFacetCalendarDisplayContextBuilder.setTimeZone(_timeZone);
		customDateFacetCalendarDisplayContextBuilder.setTo(_to);

		return customDateFacetCalendarDisplayContextBuilder.build();
	}

	private CustomDateFacetTermDisplayContext
		_buildCustomDateTermDisplayContext() {

		boolean selected = _isCustomRangeSelected();

		CustomDateFacetTermDisplayContext customDateFacetTermDisplayContext =
			new CustomDateFacetTermDisplayContext();

		customDateFacetTermDisplayContext.setFrequency(
			getFrequency(_getCustomRangeTermCollector(selected)));
		customDateFacetTermDisplayContext.setLabel("custom-range");
		customDateFacetTermDisplayContext.setRange("custom-range");
		customDateFacetTermDisplayContext.setRangeURL(_getCustomRangeURL());
		customDateFacetTermDisplayContext.setSelected(selected);

		return customDateFacetTermDisplayContext;
	}

	private CustomDateFacetTermDisplayContext
		_buildDefaultDateFacetTermDisplayContext() {

		if (_facet == null) {
			return null;
		}

		FacetConfiguration facetConfiguration = _facet.getFacetConfiguration();

		String label = facetConfiguration.getLabel();

		CustomDateFacetTermDisplayContext customDateFacetTermDisplayContext =
			new CustomDateFacetTermDisplayContext();

		customDateFacetTermDisplayContext.setLabel(label);
		customDateFacetTermDisplayContext.setRange(label);
		customDateFacetTermDisplayContext.setSelected(true);

		return customDateFacetTermDisplayContext;
	}

	private CustomDateFacetTermDisplayContext _buildTermDisplayContext(
		String label, String range) {

		CustomDateFacetTermDisplayContext customDateFacetTermDisplayContext =
			new CustomDateFacetTermDisplayContext();

		customDateFacetTermDisplayContext.setFrequency(
			getFrequency(getTermCollector(range)));
		customDateFacetTermDisplayContext.setLabel(label);
		customDateFacetTermDisplayContext.setRange(range);
		customDateFacetTermDisplayContext.setRangeURL(_getLabeledRangeURL(label));
		customDateFacetTermDisplayContext.setSelected(
			_selectedRanges.contains(label));

		return customDateFacetTermDisplayContext;
	}

	private List<CustomDateFacetTermDisplayContext> _buildTermDisplayContexts() {
		JSONArray rangesJSONArray = _getRangesJSONArray();

		if (rangesJSONArray == null) {
			return null;
		}

		List<CustomDateFacetTermDisplayContext> customDateFacetTermDisplayContexts =
			new ArrayList<>();

		for (int i = 0; i < rangesJSONArray.length(); i++) {
			JSONObject jsonObject = rangesJSONArray.getJSONObject(i);

			customDateFacetTermDisplayContexts.add(
				_buildTermDisplayContext(
					jsonObject.getString("label"),
					jsonObject.getString("range")));
		}

		return customDateFacetTermDisplayContexts;
	}

	private TermCollector _getCustomRangeTermCollector(boolean selected) {
		if (!selected) {
			return null;
		}

		FacetCollector facetCollector = _facet.getFacetCollector();

		return facetCollector.getTermCollector(
			_dateRangeFactory.getRangeString(_from, _to));
	}

	private String _getCustomRangeURL() {
		DateFormat format = _dateFormatFactory.getSimpleDateFormat(
			"yyyy-MM-dd");

		Calendar calendar = _calendarFactory.getCalendar(_timeZone);

		String to = format.format(calendar.getTime());

		calendar.add(Calendar.DATE, -1);

		String from = format.format(calendar.getTime());

		String rangeURL = HttpComponentsUtil.removeParameter(
			_currentURL, _fieldName);

		rangeURL = HttpComponentsUtil.removeParameter(
			rangeURL, _paginationStartParameterName);

		rangeURL = HttpComponentsUtil.setParameter(
			rangeURL, _fieldName+"From", from);

		return HttpComponentsUtil.setParameter(rangeURL, _fieldName+"To", to);
	}

	private String _getLabeledRangeURL(String label) {
		String rangeURL = HttpComponentsUtil.removeParameter(
			_currentURL, _fieldName+"From");

		rangeURL = HttpComponentsUtil.removeParameter(rangeURL, _fieldName+"To");

		rangeURL = HttpComponentsUtil.removeParameter(
			rangeURL, _paginationStartParameterName);

		return HttpComponentsUtil.setParameter(rangeURL, _fieldName, label);
	}

	private JSONArray _getRangesJSONArray() {
		if (_facet == null) {
			return null;
		}

		FacetConfiguration facetConfiguration = _facet.getFacetConfiguration();

		JSONObject dataJSONObject = facetConfiguration.getData();

		return dataJSONObject.getJSONArray("ranges");
	}

	private boolean _isCustomRangeSelected() {
		if (Validator.isBlank(_from) && Validator.isBlank(_to)) {
			return false;
		}

		return true;
	}

	private final CalendarFactory _calendarFactory;
	private final String _fieldName;
	private String _currentURL;
	private final DateFormatFactory _dateFormatFactory;
	private final DateRangeFactory _dateRangeFactory;
	private Facet _facet;
	private String _from;
	private Locale _locale;
	private final CustomDateFacetConfiguration
		_customDateFacetConfiguration;
	private String _paginationStartParameterName;
	private String _parameterName;
	private List<String> _selectedRanges = Collections.emptyList();
	private final ThemeDisplay _themeDisplay;
	private TimeZone _timeZone;
	private String _to;
	private int _totalHits;

}