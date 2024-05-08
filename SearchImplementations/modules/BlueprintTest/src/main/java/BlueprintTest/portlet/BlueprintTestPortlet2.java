package BlueprintTest.portlet;

import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalServiceUtil;
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
public class BlueprintTestPortlet2 extends MVCPortlet {

	private static final Log log = LogFactoryUtil.getLog(BlueprintTestPortlet2.class);

	@Override
	public void render(RenderRequest renderRequest, RenderResponse renderResponse)
			throws IOException, PortletException {
		{

			List<SXPBlueprint> sxpBlueprints = SXPBlueprintLocalServiceUtil.getSXPBlueprints(QueryUtil.ALL_POS,
					QueryUtil.ALL_POS);

			JSONObject enPR_advancedConfiguration = JSONFactoryUtil.createJSONObject();
			JSONObject enPR_generalConfiguration = JSONFactoryUtil.createJSONObject();

			for (SXPBlueprint sxpBlueprint : sxpBlueprints) {
				// if
				// ("Enforcement_PressRelease".equalsIgnoreCase(sxpBlueprint.getTitle(Locale.US)))
				// {
				if ("Limited Content Results".equalsIgnoreCase(sxpBlueprint.getTitle(Locale.US))) {
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
					log.info(i++);
					/*
					 * if (sxpBlueprint.getTitle(Locale.US).toLowerCase().contains("press") &&
					 * !sxpBlueprint.getTitle(Locale.US).equalsIgnoreCase("Enforcement_PressRelease"
					 * )) {
					 */
					if (!sxpBlueprint.getTitle(Locale.US).equalsIgnoreCase("Limited Content Results")) {

						log.info(i++);

						String configurationJSON = sxpBlueprint.getConfigurationJSON();

						try {
							JSONObject configurationJSONObject = JSONFactoryUtil.createJSONObject(configurationJSON);
							log.info(i++);

							if (Validator.isNotNull(configurationJSONObject)) {
								log.info(i++);

								JSONObject advancedConfiguration = (JSONObject) configurationJSONObject
										.get("advancedConfiguration");
								JSONObject generalConfiguration = (JSONObject) configurationJSONObject
										.get("generalConfiguration");
								JSONObject sortConfiguration = (JSONObject) configurationJSONObject
										.get("sortConfiguration");

								advancedConfiguration = enPR_advancedConfiguration;
								log.info(i++);

								JSONArray sorts = (JSONArray) sortConfiguration.getJSONArray("sorts");
								log.info(i++);

								if (Validator.isNotNull(sorts)) {
									log.info(i++);

									for (Object sortParameterObject : sorts) {
										log.info(i++);

										JSONObject sortJsonObject = (JSONObject) sortParameterObject;
										log.error(sortJsonObject.keySet().size());
										log.info(i++);

										if (sortJsonObject.keys().hasNext()) {
											log.info(i++);
											String sortparameter = sortJsonObject.keys().next();
											log.info(i++);
											if (!advancedConfiguration.toJSONString().toString()
													.contains(sortparameter)) {
												log.info(i++);
												JSONObject sourceJsonObject = (JSONObject) advancedConfiguration
														.getJSONObject("source");

												log.info(i++);
												JSONArray includesJSONArray = (JSONArray) sourceJsonObject
														.getJSONArray("includes");
												if (Validator.isNull(includesJSONArray)) {
													log.info(i++);
													includesJSONArray = JSONFactoryUtil.createJSONArray();
												}
												log.info(i++);
												includesJSONArray.put(sortparameter);
												sourceJsonObject.put("includes", includesJSONArray);

												log.info(i++);
												JSONArray stored_fieldsJSONArray = (JSONArray) advancedConfiguration
														.getJSONArray("stored_fields");
												if (Validator.isNull(stored_fieldsJSONArray)) {
													log.info(i++);
													stored_fieldsJSONArray = JSONFactoryUtil.createJSONArray();
												}
												log.info(i++);
												stored_fieldsJSONArray.put(sortparameter);
												advancedConfiguration.put("stored_fields", stored_fieldsJSONArray);

												sourceJsonObject.put("fetchSource", false);

												advancedConfiguration.put("source", sourceJsonObject);
											}
										}
									}
								}

								log.info(i++);
								configurationJSONObject.put("advancedConfiguration", advancedConfiguration);
								log.info(i++);
								configurationJSONObject.put("generalConfiguration", enPR_generalConfiguration);
								log.info(i++);

								log.error(sxpBlueprint.getTitle(Locale.US) + "::" + configurationJSON + "::"
										+ configurationJSONObject.toJSONString().toString());

								log.info(i++);
								configurationJSON = configurationJSONObject.toJSONString().toString();

								log.info(i++);
								sxpBlueprint.setConfigurationJSON(configurationJSON);

								log.info(i++);
								SXPBlueprintLocalServiceUtil.updateSXPBlueprint(sxpBlueprint);

							}
						} catch (JSONException e) {
							e.printStackTrace();
							log.error("  " + e);
						}

					}
				}

			}
		}

		{
			List<DDMTemplate> ddmTemplates = DDMTemplateLocalServiceUtil.getDDMTemplates(QueryUtil.ALL_POS,
					QueryUtil.ALL_POS);

			for (DDMTemplate ddmTemplate : ddmTemplates) {
				if (ddmTemplate.getScript().contains("upload")) {
					System.out.println(ddmTemplate.getName(Locale.US) + " : " + ddmTemplate.getType());
				}
			}

		}
		super.render(renderRequest, renderResponse);
	}
}