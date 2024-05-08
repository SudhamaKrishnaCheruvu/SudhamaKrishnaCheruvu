/**
 * Copyright 2000-present Liferay, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liferay.custom.date.facet.portlet;

import com.liferay.custom.date.facet.constants.CustomDateFacetPortletKeys;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;
import com.liferay.portal.kernel.util.ParamUtil;

import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Liferay
 */
@Component(
	configurationPid = "com.liferay.custom.date.facet.portlet.CustomDateFacetConfiguration",
	configurationPolicy = ConfigurationPolicy.OPTIONAL, immediate = true,
	property = "javax.portlet.name=" + CustomDateFacetPortletKeys.CUSTOMDATEFACET,
	service = ConfigurationAction.class
)
public class CustomDateFacetConfigurationAction
	extends DefaultConfigurationAction {

	@Override
	public void include(
			PortletConfig portletConfig, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		if (_log.isInfoEnabled()) {
			_log.info("Blade Message Portlet configuration include");
		}

		httpServletRequest.setAttribute(
			CustomDateFacetConfiguration.class.getName(),
			_messageDisplayConfiguration);

		super.include(portletConfig, httpServletRequest, httpServletResponse);
	}

	@Override
	public void processAction(
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws Exception {

		String customFieldName = ParamUtil.getString(actionRequest, "customFieldName");
		
		if (_log.isInfoEnabled()) {
			_log.info("Portlet configuration action: field name : "+customFieldName);
		}


		setPreference(actionRequest, "customFieldName", customFieldName);

		super.processAction(portletConfig, actionRequest, actionResponse);
	}

	@Activate
	@Modified
	protected void activate(Map<Object, Object> properties) {
		_messageDisplayConfiguration = ConfigurableUtil.createConfigurable(
			CustomDateFacetConfiguration.class, properties);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CustomDateFacetConfigurationAction.class);

	private volatile CustomDateFacetConfiguration _messageDisplayConfiguration;

}