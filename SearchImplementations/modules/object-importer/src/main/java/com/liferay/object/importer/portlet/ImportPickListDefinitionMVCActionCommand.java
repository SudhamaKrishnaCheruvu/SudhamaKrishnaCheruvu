package com.liferay.object.importer.portlet;

import com.liferay.headless.admin.list.type.resource.v1_0.ListTypeDefinitionResource;
import com.liferay.headless.admin.list.type.resource.v1_0.ListTypeEntryResource;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.service.ListTypeDefinitionLocalServiceUtil;
import com.liferay.object.importer.constants.ObjectImporterPortletKeys;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.FileItem;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.upload.UploadPortletRequestImpl;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(property = {
		"javax.portlet.name=" + ObjectImporterPortletKeys.EDELWEISSPICKLISTEXPORTIMPORTWEB,
		"mvc.command.name=/pick_list/export_import_pick_list" }, service = MVCActionCommand.class)
public class ImportPickListDefinitionMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {
		try {
			_importPickListDefinition(actionRequest);
		} catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
			_log.error(exception);

			SessionErrors.add(actionRequest, "error");
			actionResponse.setRenderParameter("mvcPath", "/import_pick_list.jsp");

		}

		hideDefaultErrorMessage(actionRequest);

	}

	private void _importPickListDefinition(ActionRequest actionRequest) throws Exception {

		ListTypeDefinitionResource.Builder builder = factory.create();

		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

		ListTypeDefinitionResource listTypeDefinitionResource = builder.user(themeDisplay.getUser()).build();

		try {
			UploadPortletRequest uploadPortletRequest = _getUploadPortletRequest(actionRequest);

			Map<String, FileItem[]> files = uploadPortletRequest.getMultipartParameterMap();

			for (Entry<String, FileItem[]> file2 : files.entrySet()) {

				FileItem item[] = file2.getValue();

				for (FileItem fileItem : item) {

					try {
						_log.info("Import operation is in processes for Picklist " + fileItem.getFileName());
						_importPickListEntry(actionRequest, listTypeDefinitionResource,
								FileUtil.createTempFile(fileItem.getInputStream()));

					} catch (Exception e) {
						_log.error(" Exception while importing List Type definition ....." + fileItem.getFileName());
						_log.error(e.getMessage());
					}
				}

			}
		} catch (Exception e) {
			_log.error(" Exception in the importObjectDefinition method ..." + e.getMessage());
		}

	}

	private void _importPickListEntry(ActionRequest actionRequest,
			ListTypeDefinitionResource listTypeDefinitionResource, File file) throws Exception {

		String pickListDefinitionJSON = FileUtil.read(file);

		JSONObject objectDefinitionJSONObject = _jsonFactory.createJSONObject(pickListDefinitionJSON);

		com.liferay.headless.admin.list.type.dto.v1_0.ListTypeDefinition listTypeDefinition = com.liferay.headless.admin.list.type.dto.v1_0.ListTypeDefinition
				.toDTO(objectDefinitionJSONObject.toString());

		ListTypeDefinition listTypeDefinition2 = ListTypeDefinitionLocalServiceUtil
				.fetchListTypeDefinitionByExternalReferenceCode(listTypeDefinition.getExternalReferenceCode(),
						PortalUtil.getCompanyId(actionRequest));

		if (Validator.isNotNull(listTypeDefinition2)) {
			_log.info(" The Pick List is already exist with external reference code "
					+ listTypeDefinition.getExternalReferenceCode());
			_log.info("This action will delete old one and import new one");
			ListTypeDefinitionLocalServiceUtil.deleteListTypeDefinition(listTypeDefinition2);
		}

		com.liferay.headless.admin.list.type.dto.v1_0.ListTypeDefinition putListTypeDefinition = listTypeDefinitionResource
				.postListTypeDefinition(listTypeDefinition);

		JSONArray listTypeEntryJSONArray = objectDefinitionJSONObject.getJSONArray("listTypeEntries");

		ListTypeEntryResource.Builder builder = entryFactory.create();

		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

		ListTypeEntryResource listTypeEntryResource = builder.user(themeDisplay.getUser()).build();

		for (int i = 0; i < listTypeEntryJSONArray.length(); i++) {

			com.liferay.headless.admin.list.type.dto.v1_0.ListTypeEntry listTypeEntry = com.liferay.headless.admin.list.type.dto.v1_0.ListTypeEntry
					.toDTO(listTypeEntryJSONArray.getString(i));

			listTypeEntryResource.postListTypeDefinitionListTypeEntry(putListTypeDefinition.getId(), listTypeEntry);

		}

		FileUtil.delete(file);

	}

	private UploadPortletRequest _getUploadPortletRequest(ActionRequest actionRequest) {

		LiferayPortletRequest liferayPortletRequest = _portal.getLiferayPortletRequest(actionRequest);

		return new UploadPortletRequestImpl(
				_portal.getUploadServletRequest(liferayPortletRequest.getHttpServletRequest()), liferayPortletRequest,
				_portal.getPortletNamespace(liferayPortletRequest.getPortletName()));
	}

	private static final Log _log = LogFactoryUtil.getLog(ImportPickListDefinitionMVCActionCommand.class);

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private ListTypeDefinitionResource.Factory factory;

	@Reference
	private ListTypeEntryResource.Factory entryFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;
}
