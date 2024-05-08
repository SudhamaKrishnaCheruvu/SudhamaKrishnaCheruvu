package com.liferay.object.importer.portlet;

import com.liferay.object.importer.constants.ObjectImporterPortletKeys;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cookies.CookiesManagerUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.security.auth.AuthTokenUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(property = { "javax.portlet.name=" + ObjectImporterPortletKeys.EDELWEISSPICKLISTEXPORTIMPORTWEB,
		"mvc.command.name=/pick_list/export_import_pick_list" }, service = MVCResourceCommand.class)
public class ExportListDefinitionMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay) resourceRequest.getAttribute(WebKeys.THEME_DISPLAY);
		JSONObject entry = null;

		long id = ParamUtil.getLong(resourceRequest, "id");

		_log.info("id...." + id);
		try {

			StringBuilder targetURL = new StringBuilder();
			targetURL.append(themeDisplay.getPortalURL());
			targetURL.append("/o/headless-admin-list-type/v1.0/list-type-definitions/");
			targetURL.append(id);

			_log.info(" URL " + targetURL.toString());
			URL url = new URL(targetURL.toString());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestProperty("x-csrf-token",
					AuthTokenUtil.getToken(PortalUtil.getHttpServletRequest(resourceRequest)));

			connection.setRequestProperty("Cookie", "JSESSIONID=" + CookiesManagerUtil.getCookieValue("JSESSIONID",
					PortalUtil.getHttpServletRequest(resourceRequest)));

			int responseCode = connection.getResponseCode();

			_log.info(" entry response code" + responseCode);

			if (responseCode == HttpURLConnection.HTTP_OK) {
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String inputLine;
				StringBuilder response = new StringBuilder();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				entry = JSONFactoryUtil.createJSONObject(response.toString());

				_log.info("entry..... " + entry);

				if (Validator.isNotNull(entry)) {

					_sanitizeJSON(entry,
							new String[] { "actions", "dateCreated", "dateModified", "id", "listTypeDefinitionId" });

					String entryJSON = entry.toString();

					_log.info("entry..... " + entry);

					PortletResponseUtil.sendFile(resourceRequest, resourceResponse,
							StringBundler.concat("PickList_", entry.getString("name"), StringPool.UNDERLINE,
									String.valueOf(id), StringPool.UNDERLINE, Time.getTimestamp(), ".json"),
							entryJSON.getBytes(), ContentTypes.APPLICATION_JSON);

				}
			}
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	private void _sanitizeJSON(Object object, String[] keys) {
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

	@Reference
	private JSONFactory _jsonFactory;

	private static final Log _log = LogFactoryUtil.getLog(ExportListDefinitionMVCResourceCommand.class);

}
