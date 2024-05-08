package com.liferay.object.importer.portlet;

import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.master.configuration.MasterConfiguration;
import com.liferay.object.admin.rest.dto.v1_0.ObjectDefinition;
import com.liferay.object.admin.rest.resource.v1_0.ObjectDefinitionResource;
import com.liferay.object.healper.util.HealperUtil;
import com.liferay.object.importer.constants.ObjectImporterPortletKeys;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.FileItem;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.upload.UploadPortletRequestImpl;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author krishna
 */
@Component(configurationPid = "com.liferay.master.configuration.MasterConfiguration", immediate = true, property = {
		"com.liferay.portlet.display-category=category.sample", "com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.instanceable=false", "javax.portlet.display-name=EdelweissObjectImporter",
		"javax.portlet.init-param.template-path=/", "javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + ObjectImporterPortletKeys.EDELWEISSOBJECTIMPORTER,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=administrator" }, service = Portlet.class)
public class ObjectImporterPortlet extends MVCPortlet {

	@Override
	public void render(RenderRequest renderRequest, RenderResponse renderResponse)
			throws IOException, PortletException {
		ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY);
		renderRequest.setAttribute("showActions", configuration.getShowActions());

		try {
			Folder mainFolder = DLAppServiceUtil.getFolder(themeDisplay.getScopeGroupId(), 0, "Objects Backup");
			List<Folder> folders = DLAppServiceUtil
					.getFolders(mainFolder.getRepositoryId(), mainFolder.getFolderId(), -1, -1).stream()
					.sorted(Comparator.comparing(Folder::getCreateDate).reversed()).collect(Collectors.toList());
			long folderId = ParamUtil.getLong(renderRequest, "folderId");

			if (folderId == 0 && !folders.isEmpty()) {
				folderId = folders.get(0).getFolderId();
			}
			if (folderId > 0) {
				renderRequest.setAttribute("show", true);
				renderRequest.setAttribute("folderId", folderId);
			}

			renderRequest.setAttribute("folders", folders);
		} catch (PortalException e) {
			log.error(" Exception in the render method ..." + e.getMessage());
		}

		super.render(renderRequest, renderResponse);
	}

	private UploadPortletRequest _getUploadPortletRequest(ActionRequest actionRequest) {

		LiferayPortletRequest liferayPortletRequest = _portal.getLiferayPortletRequest(actionRequest);

		return new UploadPortletRequestImpl(
				_portal.getUploadServletRequest(liferayPortletRequest.getHttpServletRequest()), liferayPortletRequest,
				_portal.getPortletNamespace(liferayPortletRequest.getPortletName()));
	}

	public void exportPublishObject(ActionRequest actionRequest, ActionResponse actionResponse) throws IOException {

		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
		try {
			HealperUtil.exportObject(ServiceContextFactory.getInstance(actionRequest),
					PortalUtil.getUser(actionRequest), _objectDefinitionResourceFactory);
		} catch (Exception e) {
			log.error("Exception while exporting Objects .......");
			e.getStackTrace();

		}
		if (themeDisplay.getLayout().isPrivateLayout()) {
			actionResponse.sendRedirect("/group/guest" + themeDisplay.getLayoutFriendlyURL(themeDisplay.getLayout()));
		} else {
			actionResponse.sendRedirect(themeDisplay.getLayoutFriendlyURL(themeDisplay.getLayout()));

		}

	}

	public void importObjectDefinition(ActionRequest actionRequest, ActionResponse actionResponse) throws IOException {
		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
		try {
			UploadPortletRequest uploadPortletRequest = _getUploadPortletRequest(actionRequest);

			Map<String, FileItem[]> files = uploadPortletRequest.getMultipartParameterMap();

			for (Entry<String, FileItem[]> file2 : files.entrySet()) {

				FileItem item[] = file2.getValue();

				for (FileItem fileItem : item) {
					log.info("Import operation is in processes for Object Definition " + fileItem.getFileName());
					try {
						_importObjectDefinition(actionRequest, FileUtil.createTempFile(fileItem.getInputStream()));

					} catch (Exception e) {
						log.error(" Exception while importing object definition ....." + fileItem.getFileName());
						log.error(e.getMessage());
					}
				}

			}
		} catch (Exception e) {
			log.error(" Exception in the importObjectDefinition method ..." + e.getMessage());
		}

		if (themeDisplay.getLayout().isPrivateLayout()) {
			actionResponse.sendRedirect("/group/guest" + themeDisplay.getLayoutFriendlyURL(themeDisplay.getLayout()));
		} else {
			actionResponse.sendRedirect(themeDisplay.getLayoutFriendlyURL(themeDisplay.getLayout()));

		}

	}

	public void deleteObject(ActionRequest actionRequest, ActionResponse actionResponse) throws IOException {
		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
		List<com.liferay.object.model.ObjectDefinition> objectDefinitions = ObjectDefinitionLocalServiceUtil
				.getObjectDefinitions(-1, -1);

		for (com.liferay.object.model.ObjectDefinition objectDefinition : objectDefinitions) {
			try {
				if (!objectDefinition.isSystem()) {
					ObjectDefinitionLocalServiceUtil.deleteObjectDefinition(objectDefinition.getObjectDefinitionId());
				}
			} catch (Exception e) {
				log.error(" Unable to delete object " + e.getMessage());
			}

		}
		if (themeDisplay.getLayout().isPrivateLayout()) {
			actionResponse.sendRedirect("/group/guest" + themeDisplay.getLayoutFriendlyURL(themeDisplay.getLayout()));
		} else {
			actionResponse.sendRedirect(themeDisplay.getLayoutFriendlyURL(themeDisplay.getLayout()));

		}
	}

	public void publishObject(ActionRequest actionRequest, ActionResponse actionResponse) throws IOException {

		log.info("publishObject........");
		List<com.liferay.object.model.ObjectDefinition> objectDefinitions = ObjectDefinitionLocalServiceUtil
				.getObjectDefinitions(-1, -1);

		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

		for (com.liferay.object.model.ObjectDefinition objectDefinition : objectDefinitions) {
			try {
				if (!objectDefinition.isSystem() && !objectDefinition.isApproved()) {
					ObjectDefinitionLocalServiceUtil.publishCustomObjectDefinition(themeDisplay.getUserId(),
							objectDefinition.getObjectDefinitionId());
				}
			} catch (PortalException e) {
				log.error(" Unable to publish object " + e.getMessage());
			}

		}
		if (themeDisplay.getLayout().isPrivateLayout()) {
			actionResponse.sendRedirect("/group/guest" + themeDisplay.getLayoutFriendlyURL(themeDisplay.getLayout()));
		} else {
			actionResponse.sendRedirect(themeDisplay.getLayoutFriendlyURL(themeDisplay.getLayout()));

		}

	}

	public void activePublishObject(ActionRequest actionRequest, ActionResponse actionResponse) throws IOException {

		log.info("publishObject........");
		List<com.liferay.object.model.ObjectDefinition> objectDefinitions = ObjectDefinitionLocalServiceUtil
				.getObjectDefinitions(-1, -1);

		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

		for (com.liferay.object.model.ObjectDefinition objectDefinition : objectDefinitions) {
			try {
				if (!objectDefinition.isActive()) {
					objectDefinition.setActive(true);
					ObjectDefinitionLocalServiceUtil.updateCustomObjectDefinition(
							objectDefinition.getExternalReferenceCode(), objectDefinition.getObjectDefinitionId(),
							objectDefinition.getAccountEntryRestrictedObjectFieldId(),
							objectDefinition.getDescriptionObjectFieldId(), objectDefinition.getObjectFolderId(),
							objectDefinition.getTitleObjectFieldId(), objectDefinition.isAccountEntryRestricted(), true,
							objectDefinition.isEnableCategorization(), objectDefinition.isEnableComments(),
							objectDefinition.isEnableLocalization(), objectDefinition.isEnableObjectEntryHistory(),
							objectDefinition.getLabelMap(), objectDefinition.getName(),
							objectDefinition.getPanelAppOrder(), objectDefinition.getPanelCategoryKey(),
							objectDefinition.getPortlet(), objectDefinition.getPluralLabelMap(),
							objectDefinition.getScope());

				}
			} catch (Exception e) {
				log.error(" Unable to active object " + e.getMessage());
			}

		}
		if (themeDisplay.getLayout().isPrivateLayout()) {
			actionResponse.sendRedirect("/group/guest" + themeDisplay.getLayoutFriendlyURL(themeDisplay.getLayout()));
		} else {
			actionResponse.sendRedirect(themeDisplay.getLayoutFriendlyURL(themeDisplay.getLayout()));

		}

	}

	public void importObject(ActionRequest actionRequest, ActionResponse actionResponse) throws IOException {
		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

		long[] rowIds = ParamUtil.getLongValues(actionRequest, "rowIds");

		try {

			for (long fileEntryId : rowIds) {
				FileEntry fileEntry = DLAppServiceUtil.getFileEntry(fileEntryId);
				log.info("Import operation is in processes for Object Definition " + fileEntry.getTitle());
				try {
					_importObjectDefinition(actionRequest, FileUtil.createTempFile(fileEntry.getContentStream()));
				} catch (Exception e) {
					log.error(" Exception while importing object definition ....." + fileEntry.getTitle());
					log.error(e.getMessage());
				}
			}

			if (themeDisplay.getLayout().isPrivateLayout()) {
				actionResponse
						.sendRedirect("/group/guest" + themeDisplay.getLayoutFriendlyURL(themeDisplay.getLayout()));
			} else {
				actionResponse.sendRedirect(themeDisplay.getLayoutFriendlyURL(themeDisplay.getLayout()));

			}
		} catch (Exception e) {
			e.printStackTrace();
			SessionErrors.add(actionRequest, e.getClass());
		}

	}

	private void _importObjectDefinition(ActionRequest actionRequest, File file) throws Exception {

		ObjectDefinitionResource.Builder builder = _objectDefinitionResourceFactory.create();

		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

		ObjectDefinitionResource objectDefinitionResource = builder.user(themeDisplay.getUser()).build();

		String objectDefinitionJSON = FileUtil.read(file);

		JSONObject objectDefinitionJSONObject = JSONFactoryUtil.createJSONObject(objectDefinitionJSON);

		ObjectDefinition objectDefinition = ObjectDefinition.toDTO(objectDefinitionJSONObject.toString());

		objectDefinition.setActive(false);
		objectDefinition.setName(objectDefinitionJSONObject.getString("name"));

		ObjectDefinition putObjectDefinition = objectDefinitionResource.putObjectDefinitionByExternalReferenceCode(
				objectDefinition.getExternalReferenceCode(), objectDefinition);

		putObjectDefinition.setPortlet(objectDefinition.getPortlet());

		if (!GetterUtil.getBoolean(PropsUtil.get("feature.flag.LPS-135430"))) {
			putObjectDefinition.setStorageType(StringPool.BLANK);
		}

		objectDefinitionResource.putObjectDefinition(putObjectDefinition.getId(), putObjectDefinition);

		FileUtil.delete(file);

	}

	@Activate
	protected void activate(Map<String, Object> properties) {

		log.debug(" activate ....");

		configuration = ConfigurableUtil.createConfigurable(MasterConfiguration.class, properties);

	}

	private volatile MasterConfiguration configuration;

	@Reference
	private ObjectDefinitionResource.Factory _objectDefinitionResourceFactory;

	@Reference
	private Portal _portal;

	private static Log log = LogFactoryUtil.getLog(ObjectImporterPortlet.class);
}