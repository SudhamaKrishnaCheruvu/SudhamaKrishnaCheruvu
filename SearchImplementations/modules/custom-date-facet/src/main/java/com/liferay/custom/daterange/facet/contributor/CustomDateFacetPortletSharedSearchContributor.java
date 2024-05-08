package com.liferay.custom.daterange.facet.contributor;

import com.liferay.custom.date.facet.constants.CustomDateFacetPortletKeys;
import com.liferay.custom.daterange.facet.builder.CustomDateFacetBuilder;
import com.liferay.custom.daterange.facet.builder.CustomDateFacetFactory;
import com.liferay.custom.daterange.facet.builder.DateRangeFactory;
import com.liferay.custom.daterange.facet.display.context.SearchOptionalUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletPreferencesLocalServiceUtil;
import com.liferay.portal.kernel.util.CalendarFactory;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.DateFormatFactory;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchContributor;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchSettings;

import javax.portlet.PortletPreferences;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lino Alves
 * @author Adam Brandizzi
 * @author Andr� de Oliveira
 */
@Component(
	immediate = true,
	property = "javax.portlet.name=" + CustomDateFacetPortletKeys.CUSTOMDATEFACET,
	service = PortletSharedSearchContributor.class
)
public class CustomDateFacetPortletSharedSearchContributor
	implements PortletSharedSearchContributor {

	@Override
	public void contribute(
		PortletSharedSearchSettings portletSharedSearchSettings) {
		CustomDateFacetPortletPreferences customDateFacetPortletPreferences =
			new CustomDateFacetPortletPreferencesImpl(
				portletSharedSearchSettings.getPortletPreferencesOptional());
		PortletLocalServiceUtil.getPortletById(portletSharedSearchSettings.getPortletId());
		portletSharedSearchSettings.addFacet(
			buildFacet(
					customDateFacetPortletPreferences, portletSharedSearchSettings));
	}

	protected Facet buildFacet(
			CustomDateFacetPortletPreferences customDateFacetPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		CustomDateFacetBuilder customDateFacetBuilder = new CustomDateFacetBuilder(
				customCreateDateFacetFactory, getCalendarFactory(), getDateFormatFactory(),
			getJSONFactory());

		customDateFacetBuilder.setRangesJSONArray(
			replaceAliases(
					customDateFacetPortletPreferences.getRangesJSONArray()));
		
		SearchContext searchContext = portletSharedSearchSettings.getSearchContext();
	
		PortletPreferences portletPreferences = PortletPreferencesLocalServiceUtil.getPreferences(searchContext.getCompanyId(), searchContext.getLayout().getGroupId(),
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, searchContext.getLayout().getPlid(), portletSharedSearchSettings.getPortletId());
		String portletId=portletSharedSearchSettings.getPortletId();

		javax.portlet.PortletPreferences portletSetup =PortletPreferencesFactoryUtil.getLayoutPortletSetup(searchContext.getLayout(), portletId);
		
		String fieldName = portletSetup.getValue("customFieldName", "createDate");
		searchContext.setAttribute("fieldName", fieldName);
		customDateFacetBuilder.setSearchContext(searchContext);

		String parameterName =
				fieldName;

		customDateFacetBuilder.setSelectedRanges(
				portletSharedSearchSettings.getParameterValues(parameterName));

			SearchOptionalUtil.copy(
				() -> portletSharedSearchSettings.getParameterOptional(
					parameterName + "From"),
				customDateFacetBuilder::setCustomRangeFrom);

			SearchOptionalUtil.copy(
				() -> portletSharedSearchSettings.getParameterOptional(
					parameterName + "To"),
				customDateFacetBuilder::setCustomRangeTo);

		return customDateFacetBuilder.build();
	}
	
	protected CalendarFactory getCalendarFactory() {

		// See LPS-72507 and LPS-76500

		if (calendarFactory != null) {
			return calendarFactory;
		}

		return CalendarFactoryUtil.getCalendarFactory();
	}

	protected DateFormatFactory getDateFormatFactory() {

		// See LPS-72507 and LPS-76500

		if (dateFormatFactory != null) {
			return dateFormatFactory;
		}

		return DateFormatFactoryUtil.getDateFormatFactory();
	}

	protected DateRangeFactory getDateRangeFactory() {
		if (dateRangeFactory == null) {
			dateRangeFactory = new DateRangeFactory(getDateFormatFactory());
		}

		return dateRangeFactory;
	}

	protected JSONFactory getJSONFactory() {

		// See LPS-72507 and LPS-76500

		if (jsonFactory != null) {
			return jsonFactory;
		}

		return JSONFactoryUtil.getJSONFactory();
	}

	protected JSONArray replaceAliases(JSONArray rangesJSONArray) {
		DateRangeFactory dateRangeFactory = getDateRangeFactory();

		CalendarFactory calendarFactory = getCalendarFactory();

		return dateRangeFactory.replaceAliases(
			rangesJSONArray, calendarFactory.getCalendar(), getJSONFactory());
	}

	protected CalendarFactory calendarFactory;
	protected DateFormatFactory dateFormatFactory;
	protected DateRangeFactory dateRangeFactory;
	protected JSONFactory jsonFactory;

	@Reference
	protected CustomDateFacetFactory customCreateDateFacetFactory;

}