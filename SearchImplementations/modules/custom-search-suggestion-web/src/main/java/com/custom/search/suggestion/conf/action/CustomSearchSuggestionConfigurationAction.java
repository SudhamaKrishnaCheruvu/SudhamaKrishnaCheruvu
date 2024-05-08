package com.custom.search.suggestion.conf.action;

import com.custom.search.suggestion.conf.CustomSearchSuggestionConfiguration;
import com.custom.search.suggestion.constants.CustomSearchSuggestionConstants;
import com.custom.search.suggestion.constants.CustomSearchSuggestionWebPortletKeys;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Nishant Tankariya
 *
 */
@Component(
	immediate = true,
	configurationPid = CustomSearchSuggestionConstants.CONFIGURATION_ID,
	property = "javax.portlet.name=" + CustomSearchSuggestionWebPortletKeys.CUSTOMSEARCHSUGGESTIONWEB,
	service = ConfigurationAction.class
)
public class CustomSearchSuggestionConfigurationAction extends DefaultConfigurationAction {

	@Override
	public void include(PortletConfig portletConfig, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
			throws Exception {
		httpServletRequest.setAttribute(CustomSearchSuggestionConfiguration.class.getName(), _customSearchSuggestionConfiguration);
		super.include(portletConfig, httpServletRequest, httpServletResponse);
	}

	@Override
	public void processAction(PortletConfig portletConfig, ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {
		String blueprintId = ParamUtil.getString(actionRequest, CustomSearchSuggestionConstants.PREFERENCES_BLUEPRINT_ID);

		if (!Validator.isBlank(blueprintId)) {
			setPreference(actionRequest, CustomSearchSuggestionConstants.PREFERENCES_BLUEPRINT_ID, blueprintId);
		}

		super.processAction(portletConfig, actionRequest, actionResponse);
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_customSearchSuggestionConfiguration = ConfigurableUtil.createConfigurable(CustomSearchSuggestionConfiguration.class, properties);
	}

	private volatile CustomSearchSuggestionConfiguration _customSearchSuggestionConfiguration;

}
