package com.liferay.custom.date.facet.portlet;

import com.liferay.custom.date.facet.constants.CustomDateFacetPortletKeys;
import com.liferay.custom.daterange.facet.builder.CustomDateFacetFactory;
import com.liferay.custom.daterange.facet.contributor.CustomDateFacetPortletPreferences;
import com.liferay.custom.daterange.facet.contributor.CustomDateFacetPortletPreferencesImpl;
import com.liferay.custom.daterange.facet.display.context.CustomDateFacetDisplayContext;
import com.liferay.custom.daterange.facet.display.context.CustomDateFacetDisplayContextBuilder;
import com.liferay.custom.daterange.facet.display.context.SearchOptionalUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.CalendarFactory;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.DateFormatFactory;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.facet.Facet;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchRequest;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchResponse;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Liferay
 */
@Component(
	configurationPid = "com.liferay.custom.date.facet.portlet.CustomDateFacetConfiguration",
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=category.search",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.instanceable=true",
		"javax.portlet.display-name=CustomDateFacet",
		"javax.portlet.init-param.config-template=/configuration.jsp",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + CustomDateFacetPortletKeys.CUSTOMDATEFACET,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = Portlet.class
)
public class CustomDateFacetPortlet extends MVCPortlet {
	
	@Override
	public void doView(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		if (_log.isInfoEnabled()) {
			_log.info("Blade Message Portlet render");
		}

		renderRequest.setAttribute(
			CustomDateFacetConfiguration.class.getName(),
			_messageDisplayConfiguration);

		super.doView(renderRequest, renderResponse);
	}
	
	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		PortletSharedSearchResponse portletSharedSearchResponse =
			portletSharedSearchRequest.search(renderRequest);

		CustomDateFacetDisplayContext modifiedFacetDisplayContext =
			buildDisplayContext(portletSharedSearchResponse, renderRequest);

		if (modifiedFacetDisplayContext.isRenderNothing()) {
			renderRequest.setAttribute(
				WebKeys.PORTLET_CONFIGURATOR_VISIBILITY, Boolean.TRUE);
		}
		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT, modifiedFacetDisplayContext);
		
		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(WebKeys.THEME_DISPLAY);
		String portletId=themeDisplay.getPortletDisplay().getId();

		javax.portlet.PortletPreferences portletSetup =PortletPreferencesFactoryUtil.getLayoutPortletSetup(themeDisplay.getLayout(), portletId);
		
		renderRequest.setAttribute(
				"customFieldName", portletSetup.getValue("customFieldName", StringPool.BLANK));

		super.render(renderRequest, renderResponse);
	}

	protected CustomDateFacetDisplayContext buildDisplayContext(
		PortletSharedSearchResponse portletSharedSearchResponse,
		RenderRequest renderRequest) {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(WebKeys.THEME_DISPLAY);
		String portletId=themeDisplay.getPortletDisplay().getId();

		javax.portlet.PortletPreferences portletSetup =PortletPreferencesFactoryUtil.getLayoutPortletSetup(themeDisplay.getLayout(), portletId);
		CustomDateFacetPortletPreferences modifiedFacetPortletPreferences =
			new CustomDateFacetPortletPreferencesImpl(
				portletSharedSearchResponse.getPortletPreferences(
					renderRequest));

		CustomDateFacetDisplayContextBuilder modifiedFacetDisplayBuilder =
			createCustomDateFacetDisplayBuilder(
				getCalendarFactory(), getDateFormatFactory(),
				renderRequest, portletSetup.getValue("customFieldName", "createDate"));

		modifiedFacetDisplayBuilder.setCurrentURL(
			portal.getCurrentURL(renderRequest));
		
		modifiedFacetDisplayBuilder.setFacet(
			portletSharedSearchResponse.getFacet(portletSetup.getValue("customFieldName", "createDate")));


		modifiedFacetDisplayBuilder.setLocale(themeDisplay.getLocale());

		modifiedFacetDisplayBuilder.setPaginationStartParameterName(
			getPaginationStartParameterName(portletSharedSearchResponse));

		String parameterName =
				portletSetup.getValue("customFieldName", "createDate");

			modifiedFacetDisplayBuilder.setParameterName(parameterName);

			SearchOptionalUtil.copy(
				() -> portletSharedSearchResponse.getParameterValues(
					parameterName, renderRequest),
				modifiedFacetDisplayBuilder::setParameterValues);

			SearchOptionalUtil.copy(
				() -> portletSharedSearchResponse.getParameter(
					parameterName + "From", renderRequest),
				modifiedFacetDisplayBuilder::setFromParameterValue);

			SearchOptionalUtil.copy(
				() -> portletSharedSearchResponse.getParameter(
					parameterName + "To", renderRequest),
				modifiedFacetDisplayBuilder::setToParameterValue);

		SearchResponse searchResponse =
			portletSharedSearchResponse.getSearchResponse();

		modifiedFacetDisplayBuilder.setTimeZone(themeDisplay.getTimeZone());
		modifiedFacetDisplayBuilder.setTotalHits(searchResponse.getTotalHits());

		return modifiedFacetDisplayBuilder.build();
	}

	protected CustomDateFacetDisplayContextBuilder createCustomDateFacetDisplayBuilder(
		CalendarFactory calendarFactory, DateFormatFactory dateFormatFactory,
		RenderRequest renderRequest, String fieldName) {

		try {
			return new CustomDateFacetDisplayContextBuilder(
				calendarFactory, dateFormatFactory, renderRequest, fieldName);
		}
		catch (ConfigurationException configurationException) {
			throw new RuntimeException(configurationException);
		}
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

	protected String getFieldName() {
		Facet facet = customCreateDateFacetFactory.newInstance(null);
		return facet.getFieldName();
	}

	protected String getPaginationStartParameterName(
		PortletSharedSearchResponse portletSharedSearchResponse) {

		SearchResponse searchResponse =
			portletSharedSearchResponse.getSearchResponse();

		SearchRequest searchRequest = searchResponse.getRequest();

		return searchRequest.getPaginationStartParameterName();
	}

	protected CustomDateFacetPortletPreferencesImpl getPortletPreferences(
		RenderRequest renderRequest) {

		return new CustomDateFacetPortletPreferencesImpl(
			Optional.ofNullable(renderRequest.getPreferences()));
	}

	protected ThemeDisplay getThemeDisplay(RenderRequest renderRequest) {
		return (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY);
	}
	

	protected CalendarFactory calendarFactory;
	protected DateFormatFactory dateFormatFactory;
	
	private String customFieldName;

	@Reference
	protected Http http;

	@Reference
	protected CustomDateFacetFactory customCreateDateFacetFactory ;

	@Reference
	protected Portal portal;

	@Reference
	protected PortletSharedSearchRequest portletSharedSearchRequest;

	@Activate
	@Modified
	protected void activate(Map<Object, Object> properties) {
		_messageDisplayConfiguration = ConfigurableUtil.createConfigurable(
			CustomDateFacetConfiguration.class, properties);
	}

	private static final Log _log = LogFactoryUtil.getLog(
			CustomDateFacetPortlet.class);

	private volatile CustomDateFacetConfiguration _messageDisplayConfiguration;
}