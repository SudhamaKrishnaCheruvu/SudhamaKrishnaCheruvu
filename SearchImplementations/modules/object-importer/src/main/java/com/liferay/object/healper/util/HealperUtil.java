package com.liferay.object.healper.util;

import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.service.ListTypeDefinitionLocalServiceUtil;
import com.liferay.object.admin.rest.dto.v1_0.ObjectAction;
import com.liferay.object.admin.rest.resource.v1_0.ObjectDefinitionResource;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HealperUtil {

	public static void exportObject(ServiceContext serviceContext, User user,
			ObjectDefinitionResource.Factory _objectDefinitionResourceFactory) throws Exception {

		ObjectDefinitionResource.Builder builder = _objectDefinitionResourceFactory.create();

		ObjectDefinitionResource objectDefinitionResource = builder.user(user).build();

		List<ObjectDefinition> objectDefinitions = ObjectDefinitionLocalServiceUtil.getObjectDefinitions(-1, -1);

		Date date = Calendar.getInstance().getTime();
		DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy hh:mm:ss");
		String strDate = dateFormat.format(date).replace(":", "");

		Folder mainFolder = createFolder(serviceContext, "Objects Backup", 0);
		Folder subFolder = createFolder(serviceContext, strDate, mainFolder.getFolderId());

		for (ObjectDefinition objectDefinition1 : objectDefinitions) {
			com.liferay.object.admin.rest.dto.v1_0.ObjectDefinition objectDefinition = objectDefinitionResource
					.getObjectDefinition(objectDefinition1.getObjectDefinitionId());
			if (!objectDefinition1.isSystem()) {
				uploadObjectFile(objectDefinition1, objectDefinition, serviceContext, subFolder.getFolderId());
			}

		}

	}

	public static void uploadObjectFile(ObjectDefinition objectDefinition1,
			com.liferay.object.admin.rest.dto.v1_0.ObjectDefinition objectDefinition, ServiceContext serviceContext,
			long folderId) {
		try {

			for (ObjectAction objectAction : objectDefinition.getObjectActions()) {
				Map<String, Object> parameters = (Map<String, Object>) objectAction.getParameters();

				Object object = parameters.get("predefinedValues");

				if (object == null) {
					continue;
				}

				parameters.put("predefinedValues",
						ListUtil.toList((ArrayList<LinkedHashMap>) object, JSONFactoryUtil::createJSONObject));
			}

			objectDefinition.setObjectFields(ArrayUtil.filter(objectDefinition.getObjectFields(),
					objectField -> Validator.isNull(objectField.getRelationshipType())
							&& !Objects.equals(objectField.getBusinessTypeAsString(),
									ObjectFieldConstants.BUSINESS_TYPE_AGGREGATION)));

			JSONObject objectDefinitionJSONObject = JSONFactoryUtil.createJSONObject(objectDefinition.toString());

			if (!GetterUtil.getBoolean(PropsUtil.get("feature.flag.LPS-135430"))) {
				objectDefinitionJSONObject.remove("storageType");
			}

			_sanitizeJSON(objectDefinitionJSONObject,
					new String[] { "dateCreated", "dateModified", "id", "listTypeDefinitionId", "objectDefinitionId",
							"objectDefinitionId1", "objectDefinitionId2", "objectFieldId", "objectRelationshipId",
							"titleObjectFieldId" });

			String objectDefinitionJSON = objectDefinitionJSONObject.toString();

			String fileName = StringBundler.concat("Object_", objectDefinition.getName(), StringPool.UNDERLINE,
					String.valueOf(objectDefinition1.getObjectDefinitionId()), StringPool.UNDERLINE,
					Time.getTimestamp(), ".json");

			String contentType = MimeTypesUtil.getContentType(fileName);

			DLAppLocalServiceUtil.addFileEntry(null, serviceContext.getUserId(), serviceContext.getScopeGroupId(),
					folderId, fileName, contentType, objectDefinitionJSON.getBytes(), null, null, serviceContext);

		} catch (Exception e) {
			log.error(" Fail to create a back for object definition id  ....."
					+ objectDefinition1.getObjectDefinitionId());
			log.error(" Error while creating Object backup .....", e);
		}

	}

	public static void exportPickList(ServiceContext serviceContext, String portalURL, User user, String base64) {

		log.debug(" portalURL......." + portalURL);
		log.debug(" base64........" + base64);
		List<ListTypeDefinition> listTypeDefinitions = ListTypeDefinitionLocalServiceUtil.getListTypeDefinitions(-1,
				-1);

		Date date = Calendar.getInstance().getTime();
		DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy hh:mm:ss");
		String strDate = dateFormat.format(date).replace(":", "");

		Folder mainFolder = createFolder(serviceContext, "PickList Backup", 0);
		Folder subFolder = createFolder(serviceContext, strDate, mainFolder.getFolderId());
		

		for (ListTypeDefinition definition : listTypeDefinitions) {

			uploadPickList(definition.getListTypeDefinitionId(), user, serviceContext, subFolder.getFolderId(),
					portalURL, base64);
		}

	}

	public static void uploadPickList(long id, User user, ServiceContext serviceContext, long folderId, String baseURL,
			String base64) {

		JSONObject entry = null;

		try {

			StringBuilder targetURL = new StringBuilder();
			targetURL.append(baseURL);
			targetURL.append("/o/headless-admin-list-type/v1.0/list-type-definitions/");
			targetURL.append(id);
			targetURL.append("?pageSize=-1");

			log.debug(" URL " + targetURL.toString());
			URL url = new URL(targetURL.toString());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept", "application/json");

			connection.setRequestProperty("Authorization", "Basic " + base64);

			int responseCode = connection.getResponseCode();

			log.debug(" entry response code" + responseCode);
			log.debug(" entry response mesage" + connection.getResponseMessage());

			if (responseCode == HttpURLConnection.HTTP_OK) {
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String inputLine;
				StringBuilder response = new StringBuilder();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				entry = JSONFactoryUtil.createJSONObject(response.toString());

				log.debug("entry..... " + entry);

				_sanitizeJSON(entry,
						new String[] { "actions", "dateCreated", "dateModified", "id", "listTypeDefinitionId" });

				String entryJSON = entry.toString();

				String fileName = StringBundler.concat("PickList_", entry.getString("nameCurrentValue"),
						StringPool.UNDERLINE, entry.getString("listTypeDefinitionId"), StringPool.UNDERLINE,
						Time.getTimestamp(), ".json");

				String contentType = MimeTypesUtil.getContentType(fileName);

				DLAppLocalServiceUtil.addFileEntry(null, serviceContext.getUserId(), serviceContext.getScopeGroupId(),
						folderId, fileName, contentType, entryJSON.getBytes(), null, null, serviceContext);
			}

		} catch (Exception e) {

			log.error(" Error while creating Pick List backup .....", e);
		}

	}

	public static Folder createFolder(ServiceContext serviceContext, String folderName, long parentFolderId) {

		Folder folder = isFolderExist(serviceContext, 0, folderName);

		if (Validator.isNull(folder)) {

			long repositoryId = serviceContext.getScopeGroupId();

			try {
//				folder = DLAppLocalServiceUtil.addFolder(serviceContext.getUserId(), repositoryId, parentFolderId,
//						folderName, folderName, serviceContext);

			} catch (Exception e) {
				log.error("Ignore this error .........." + e.getMessage());
			}
		}

		return folder;
	}

	public static Folder isFolderExist(ServiceContext serviceContext, long parentFolderId, String folderName) {
		Folder folder = null;
		try {
			folder = DLAppLocalServiceUtil.getFolder(serviceContext.getScopeGroupId(), parentFolderId, folderName);
		} catch (Exception e) {
			log.error("Ignore this error .........." + e.getMessage());

		}
		return folder;
	}

	public static void _sanitizeJSON(Object object, String[] keys) {
		if (object instanceof JSONArray) {
			JSONArray jsonArray = (JSONArray) object;

			for (int i = 0; i < jsonArray.length(); ++i) {
				_sanitizeJSON(jsonArray.get(i), keys);
			}
		} else if (object instanceof JSONObject) {
			JSONObject jsonObject = (JSONObject) object;

			if (jsonObject.length() == 0) {
				return;
			}

			JSONArray jsonArray = jsonObject.names();

			for (int i = 0; i < jsonArray.length(); ++i) {
				String key = jsonArray.getString(i);

				if (ArrayUtil.contains(keys, key)) {
					jsonObject.remove(key);
				} else {
					_sanitizeJSON(jsonObject.get(key), keys);
				}
			}
		}
	}

	private static Log log = LogFactoryUtil.getLog(HealperUtil.class);
}
