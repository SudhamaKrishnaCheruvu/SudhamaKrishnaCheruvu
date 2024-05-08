package BlueprintTest.portlet;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.search.experiences.model.SXPBlueprint;
import com.liferay.search.experiences.service.SXPBlueprintLocalServiceUtil;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;

import BlueprintTest.constants.BlueprintTestPortletKeys;

/**
 * @author SudhamaKrishnaC
 */
@Component(immediate = true, property = { "com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.header-portlet-css=/css/main.css", "com.liferay.portlet.instanceable=true",
		"javax.portlet.display-name=BlueprintTest", "javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + BlueprintTestPortletKeys.BLUEPRINTTEST,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user" }, service = Portlet.class)
public class BlueprintTestPortlet extends MVCPortlet {

	private static final Log log = LogFactoryUtil.getLog(BlueprintTestPortlet.class);

	@Override
	public void render(RenderRequest renderRequest, RenderResponse renderResponse)
			throws IOException, PortletException {
		{

			List<SXPBlueprint> sxpBlueprints = SXPBlueprintLocalServiceUtil.getSXPBlueprints(QueryUtil.ALL_POS,
					QueryUtil.ALL_POS);

			JSONObject enPR_advancedConfiguration = JSONFactoryUtil.createJSONObject();
			JSONObject enPR_generalConfiguration = JSONFactoryUtil.createJSONObject();

			for (SXPBlueprint sxpBlueprint : sxpBlueprints) {
				if ("Enforcement_PressRelease".equalsIgnoreCase(sxpBlueprint.getTitle(Locale.US))) {
					String configurationJSON = sxpBlueprint.getConfigurationJSON();
					try {
						JSONObject configurationJSONObject = JSONFactoryUtil.createJSONObject(configurationJSON);

						enPR_advancedConfiguration = (JSONObject) configurationJSONObject.get("advancedConfiguration");
						enPR_generalConfiguration = (JSONObject) configurationJSONObject.get("generalConfiguration");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;
				}
			}

			if (Validator.isNotNull(enPR_advancedConfiguration) && Validator.isNotNull(enPR_generalConfiguration)) {

				for (SXPBlueprint sxpBlueprint : sxpBlueprints) {
					int i = 1;

					if (sxpBlueprint.getTitle(Locale.US).toLowerCase().contains("press")
							&& !sxpBlueprint.getTitle(Locale.US).equalsIgnoreCase("Enforcement_PressRelease")) {

						String configurationJSON = sxpBlueprint.getConfigurationJSON();

						try {
							JSONObject configurationJSONObject = JSONFactoryUtil.createJSONObject(configurationJSON);

							if (Validator.isNotNull(configurationJSONObject)) {

								JSONObject advancedConfiguration = (JSONObject) configurationJSONObject
										.get("advancedConfiguration");
								JSONObject generalConfiguration = (JSONObject) configurationJSONObject
										.get("generalConfiguration");
								JSONObject sortConfiguration = (JSONObject) configurationJSONObject
										.get("sortConfiguration");

								advancedConfiguration = enPR_advancedConfiguration;

								JSONArray sorts = (JSONArray) sortConfiguration.getJSONArray("sorts");

								if (Validator.isNotNull(sorts)) {

									for (Object sortParameterObject : sorts) {

										JSONObject sortJsonObject = (JSONObject) sortParameterObject;
										System.out.println(sortJsonObject.keySet().size());

										if (sortJsonObject.keys().hasNext()) {

											String sortparameter = sortJsonObject.keys().next();

											if (!advancedConfiguration.toJSONString().toString()
													.contains(sortparameter)) {

												JSONObject sourceJsonObject = (JSONObject) advancedConfiguration
														.getJSONObject("source");

												JSONArray includesJSONArray = (JSONArray) sourceJsonObject
														.getJSONArray("includes");
												if (Validator.isNull(includesJSONArray)) {

													includesJSONArray = JSONFactoryUtil.createJSONArray();
												}

												includesJSONArray.put(sortparameter);
												sourceJsonObject.put("includes", includesJSONArray);

												JSONArray stored_fieldsJSONArray = (JSONArray) advancedConfiguration
														.getJSONArray("stored_fields");
												if (Validator.isNull(stored_fieldsJSONArray)) {

													stored_fieldsJSONArray = JSONFactoryUtil.createJSONArray();
												}

												stored_fieldsJSONArray.put(sortparameter);
												advancedConfiguration.put("stored_fields", stored_fieldsJSONArray);

												sourceJsonObject.put("fetchSource", false);

												advancedConfiguration.put("source", sourceJsonObject);
											}
										}
									}
								}

								configurationJSONObject.put("advancedConfiguration", advancedConfiguration);

								configurationJSONObject.put("generalConfiguration", enPR_generalConfiguration);

								System.out.println(sxpBlueprint.getTitle(Locale.US) + "::" + configurationJSON + "::"
										+ configurationJSONObject.toJSONString().toString());

								configurationJSON = configurationJSONObject.toJSONString().toString();

								sxpBlueprint.setConfigurationJSON(configurationJSON);

								SXPBlueprintLocalServiceUtil.updateSXPBlueprint(sxpBlueprint);

							}
						} catch (JSONException e) {
							e.printStackTrace();
							System.out.println("  " + e);
						}

					}
				}

			}
		}
		super.render(renderRequest, renderResponse);
	}
}