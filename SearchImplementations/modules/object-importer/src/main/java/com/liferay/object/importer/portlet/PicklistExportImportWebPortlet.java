package com.liferay.object.importer.portlet;

import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.service.ListTypeDefinitionLocalServiceUtil;
import com.liferay.master.configuration.MasterConfiguration;
import com.liferay.object.healper.util.HealperUtil;
import com.liferay.object.importer.constants.ObjectImporterPortletKeys;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.IOException;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author krishna
 */
@Component(immediate = true, property = { "com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.header-portlet-css=/css/main.css", "com.liferay.portlet.instanceable=false",
		"javax.portlet.display-name=EdelweissPicklistExportImportWeb", "javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/picklist/view.jsp",
		"javax.portlet.name=" + ObjectImporterPortletKeys.EDELWEISSPICKLISTEXPORTIMPORTWEB,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user" }, service = Portlet.class)
public class PicklistExportImportWebPortlet extends MVCPortlet {


	public void deletePickList(ActionRequest actionRequest, ActionResponse actionResponse) throws IOException {
		log.info(" Delete PickList Started.......");

		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
		try {

			for (ListTypeDefinition listTypeDefinition : ListTypeDefinitionLocalServiceUtil.getListTypeDefinitions(-1,
					-1)) {
				ListTypeDefinitionLocalServiceUtil.deleteListTypeDefinition(listTypeDefinition);
			}
		} catch (Exception e) {
			log.error("Exception while Deleting Picklist ......." + e);

		}

		if (themeDisplay.getLayout().isPrivateLayout()) {
			actionResponse.sendRedirect("/group/guest" + themeDisplay.getLayoutFriendlyURL(themeDisplay.getLayout()));
		} else {
			actionResponse.sendRedirect(themeDisplay.getLayoutFriendlyURL(themeDisplay.getLayout()));

		}
	}

	public void exportPickList(ActionRequest actionRequest, ActionResponse actionResponse) throws IOException {
		log.info(" PickList Backup Started.......");
		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
		try {

			MasterConfiguration configuration = ConfigurationProviderUtil
					.getSystemConfiguration(MasterConfiguration.class);
			ServiceContext serviceContext = ServiceContextFactory.getInstance(actionRequest);

			HealperUtil.exportPickList(serviceContext, serviceContext.getPortalURL(), PortalUtil.getUser(actionRequest),
					configuration.getBase64AuthCode());

			log.info(" PickList Backup Ended.......");
		} catch (Exception e) {
			log.error("Exception while exporting Picklist .......");
			e.getStackTrace();

		}
		if (themeDisplay.getLayout().isPrivateLayout()) {
			actionResponse.sendRedirect("/group/guest" + themeDisplay.getLayoutFriendlyURL(themeDisplay.getLayout()));
		} else {
			actionResponse.sendRedirect(themeDisplay.getLayoutFriendlyURL(themeDisplay.getLayout()));

		}

	}

	private static final Log log = LogFactoryUtil.getLog(PicklistExportImportWebPortlet.class);
}