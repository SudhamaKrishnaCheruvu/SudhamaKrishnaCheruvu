package at.gv.magwien.apps.search.criteria.configuration;

import at.gv.magwien.apps.search.criteria.contributor.web.constants.SearchCriteriaContributorWebPortletKeys;

import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletPreferences;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * Component Class SearchCriteriaConfigAction.
 * @author bas9004
 */
@Component(configurationPid = "searcgCriteriaConfiguration", 
    configurationPolicy = ConfigurationPolicy.OPTIONAL, immediate = true, property = {
        "javax.portlet.name="
                + SearchCriteriaContributorWebPortletKeys.SEARCH_CRITERIA_CONTRIBUTORWEB }, 
    service = ConfigurationAction.class)
public class SearchCriteriaConfigAction extends DefaultConfigurationAction {

    private static final String GROUP_ID = "groupId";
    private static final String INLCUDE_CATEGORY_SELECTOR_TEXT = "inlcudeCategorySelectorText";
    private static final String INLCUDE_TAG_SELECTOR_TEXT = "inlcudeTagSelectorText";
    private static final String EXLCUDE_TAG_SELECTOR_TEXT = "exlcudeTagSelectorText";
    private static final String EXLCUDE_CATEGORY_SELECTOR_TEXT = "excludeCategorySelectorText";
    private static final String SEARCH_CRITERIA_SELECTED_STRUCTURES_TEXT = "searchCriteriaSelectedStructuresText";
    private static final String SEARCH_CRITERIA_SELECTED_ASSET_TYPES_TEXT = "searchCriteriaSelectedAssetTypesText";
    private static final String SEARCH_CRITERIA_SELECTED_DOCUMENT_FOLDER_TEXT = 
            "searchCriteriaSelectedDocumentFolderText";
    private static final String SEARCH_CRITERIA_SELECTED_JOURNAL_FOLDER_TEXT = 
            "searchCriteriaSelectedJournalFolderText";

    @Override
    public void processAction(PortletConfig portletConfig, ActionRequest actionRequest, ActionResponse actionResponse)
            throws Exception {
        String journalTypeInclude = ParamUtil.getString(actionRequest,
                SearchCriteriaContributorWebPortletKeys.JOURNAL_TYPE_INCLUDE);
        PortletPreferences prefs = actionRequest.getPreferences();

        prefs.setValue(SearchCriteriaContributorWebPortletKeys.JOURNAL_TYPE_INCLUDE, journalTypeInclude);

        ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
        prefs.setValue(GROUP_ID, String.valueOf(themeDisplay.getScopeGroupId()));

        String[] searchCriteriaSelectedJournalFolder = ParamUtil.getParameterValues(actionRequest,
                SEARCH_CRITERIA_SELECTED_JOURNAL_FOLDER_TEXT);
        prefs.setValues(SearchCriteriaContributorWebPortletKeys.SEARCH_CRITERIA_SELECTED_JOURNALFOLDER,
                searchCriteriaSelectedJournalFolder);
        String documentTypeInclude = ParamUtil.getString(actionRequest,
                SearchCriteriaContributorWebPortletKeys.DOCUMENT_TYPE_INCLUDE);
        prefs.setValue(SearchCriteriaContributorWebPortletKeys.DOCUMENT_TYPE_INCLUDE, documentTypeInclude);

        String[] searchCriteriaSelectedDocumentFolder = ParamUtil.getParameterValues(actionRequest,
                SEARCH_CRITERIA_SELECTED_DOCUMENT_FOLDER_TEXT);
        prefs.setValues(SearchCriteriaContributorWebPortletKeys.SEARCH_CRITERIA_SELECTED_DOCUMENTFOLDER,
                searchCriteriaSelectedDocumentFolder);
        String assetTypeInclude = ParamUtil.getString(actionRequest,
                SearchCriteriaContributorWebPortletKeys.ASSET_TYPE_INCLUDE);
        prefs.setValue(SearchCriteriaContributorWebPortletKeys.ASSET_TYPE_INCLUDE, assetTypeInclude);

        String[] searchCriteriaSelectedAssetTypes = ParamUtil.getParameterValues(actionRequest,
                SEARCH_CRITERIA_SELECTED_ASSET_TYPES_TEXT);
        prefs.setValues(SearchCriteriaContributorWebPortletKeys.SEARCH_CRITERIA_SELECTED_ASSETTYPES,
                searchCriteriaSelectedAssetTypes);
        String structureInclude = ParamUtil.getString(actionRequest,
                SearchCriteriaContributorWebPortletKeys.STRUCTURE_INCLUDE);
        prefs.setValue(SearchCriteriaContributorWebPortletKeys.STRUCTURE_INCLUDE, structureInclude);
        String[] searchCriteriaSelectedStructures = ParamUtil.getParameterValues(actionRequest,
                SEARCH_CRITERIA_SELECTED_STRUCTURES_TEXT);
        prefs.setValues(SearchCriteriaContributorWebPortletKeys.SEARCH_CRITERIA_SELECTED_STRUCTURES,
                searchCriteriaSelectedStructures);
        String categoriesInclude = ParamUtil.getString(actionRequest,
                SearchCriteriaContributorWebPortletKeys.CATEGORIES_INCLUDE);
        prefs.setValue(SearchCriteriaContributorWebPortletKeys.CATEGORIES_INCLUDE, categoriesInclude);
        String categoriesAll = ParamUtil.getString(actionRequest,
                SearchCriteriaContributorWebPortletKeys.CATEGORIES_ALLORANY);
        prefs.setValue(SearchCriteriaContributorWebPortletKeys.CATEGORIES_ALLORANY, categoriesAll);
        String searchCriteriaSelectedAssetIncludeCategories = ParamUtil.getString(actionRequest,
                INLCUDE_CATEGORY_SELECTOR_TEXT);
        prefs.setValues(SearchCriteriaContributorWebPortletKeys.SEARCH_CRITERIA_SELECTED_INCLUDE_ASSETCATEGORIES,
                searchCriteriaSelectedAssetIncludeCategories);
        String categoryByIncludeUuid = getCaregoryUuids(searchCriteriaSelectedAssetIncludeCategories);
        prefs.setValues(SearchCriteriaContributorWebPortletKeys.SEARCH_CRITERIA_SELECTED_INCLUDE_ASSETCATEGORIES_UUID,
                categoryByIncludeUuid);
        String categoriesExclude = ParamUtil.getString(actionRequest,
                SearchCriteriaContributorWebPortletKeys.CATEGORIES_EXCLUDE);
        prefs.setValue(SearchCriteriaContributorWebPortletKeys.CATEGORIES_EXCLUDE, categoriesExclude);
        String categoriesExcludeAll = ParamUtil.getString(actionRequest,
                SearchCriteriaContributorWebPortletKeys.CATEGORIES_ALLORANY_EXCLUDE);
        prefs.setValue(SearchCriteriaContributorWebPortletKeys.CATEGORIES_ALLORANY_EXCLUDE, categoriesExcludeAll);
        String searchCriteriaSelectedAssetExcludeCategories = ParamUtil.getString(actionRequest,
                EXLCUDE_CATEGORY_SELECTOR_TEXT);
        prefs.setValues(SearchCriteriaContributorWebPortletKeys.SEARCH_CRITERIA_SELECTED_ASSET_CATEGORIESEXLCUDE,
                searchCriteriaSelectedAssetExcludeCategories);
        String categoryByExcludeUuid = getCaregoryUuids(searchCriteriaSelectedAssetExcludeCategories);
        prefs.setValues(SearchCriteriaContributorWebPortletKeys.SEARCH_CRITERIA_SELECTED_ASSET_CATEGORIESEXLCUDE_UUID,
                categoryByExcludeUuid);
        String tagsInclude = ParamUtil.getString(actionRequest, SearchCriteriaContributorWebPortletKeys.TAGS_INCLUDE);
        prefs.setValue(SearchCriteriaContributorWebPortletKeys.TAGS_INCLUDE, tagsInclude);

        String tagsAllInclude = ParamUtil.getString(actionRequest,
                SearchCriteriaContributorWebPortletKeys.TAGS_ALLORANY);
        prefs.setValue(SearchCriteriaContributorWebPortletKeys.TAGS_ALLORANY, tagsAllInclude);

        String searchCriteriaSelectedAssetTagsInclude = ParamUtil.getString(actionRequest, INLCUDE_TAG_SELECTOR_TEXT);

        prefs.setValues(SearchCriteriaContributorWebPortletKeys.SEARCH_CRITERIA_SELECTED_ASSETTAGS,
                searchCriteriaSelectedAssetTagsInclude);
        String tagsExclude = ParamUtil.getString(actionRequest, SearchCriteriaContributorWebPortletKeys.TAGS_EXCLUDE);
        prefs.setValue(SearchCriteriaContributorWebPortletKeys.TAGS_EXCLUDE, tagsExclude);

        String tagsAllExclude = ParamUtil.getString(actionRequest,
                SearchCriteriaContributorWebPortletKeys.TAGS_ALLORANY_EXCLUDE);
        prefs.setValue(SearchCriteriaContributorWebPortletKeys.TAGS_ALLORANY_EXCLUDE, tagsAllExclude);

        String searchCriteriaSelectedAssetTagsExclude = ParamUtil.getString(actionRequest, EXLCUDE_TAG_SELECTOR_TEXT);
        prefs.setValues(SearchCriteriaContributorWebPortletKeys.SEARCH_CRITERIA_SELECTED_ASSET_TAGSEXLCUDE,
                searchCriteriaSelectedAssetTagsExclude);
        prefs.store();
        super.processAction(portletConfig, actionRequest, actionResponse);
    }

    @Override
    public void include(PortletConfig portletConfig, HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) throws Exception {
        httpServletRequest.setAttribute(SearchCriteriaConfig.class.getName(), searcgCriteriaConfiguration);
        super.include(portletConfig, httpServletRequest, httpServletResponse);
    }
    
    private String getCaregoryUuids(String catIds) {
        String catUuids = StringPool.BLANK;
        if ((Validator.isNotNull(catIds) || !catIds.isEmpty()) && catIds.contains(StringPool.COMMA)) {
            catUuids = getCatUUidsBySplit(catIds);
        } else if ((Validator.isNotNull(catIds) || !catIds.isEmpty())) {
            try {
                catUuids = assetCategoryLocalService.getAssetCategory(Long.valueOf(catIds)).getUuid();
            } catch (NumberFormatException e) {
                log.error("Number Format Exception {}", e);
            } catch (PortalException e) {
                log.error("Portal Exception {}", e);
            }
        }
        return catUuids;
    }

    private String getCatUUidsBySplit(String catIds) {
        ArrayList<String> catIdList = new ArrayList(Arrays.asList(catIds.split(",")));
        StringBuilder stringBuilder = new StringBuilder();
        int i = 0;
        for (String catId : catIdList) {
            try {
                if (i < catIdList.size() - 1) {
                    stringBuilder.append(assetCategoryLocalService.getAssetCategory(Long.valueOf(catId)).getUuid());
                    stringBuilder.append(StringPool.COMMA);
                } else {
                    stringBuilder.append(assetCategoryLocalService.getAssetCategory(Long.valueOf(catId)).getUuid());
                }
            } catch (NumberFormatException e) {
                log.error("Number Format Exception {}", e);
            } catch (PortalException e) {
                log.error("Portal Exception {}", e);
            }
            i++;
        }
        return stringBuilder.toString();
    }

    @Activate
    @Modified
    protected void activate(Map<Object, Object> properties) {
        searcgCriteriaConfiguration = ConfigurableUtil.createConfigurable(SearchCriteriaConfig.class, properties);
    }

    private volatile SearchCriteriaConfig searcgCriteriaConfiguration;
    
    @Reference
    private AssetCategoryLocalService assetCategoryLocalService;
    
    private static Log log = LogFactoryUtil.getLog(SearchCriteriaConfigAction.class);

}
