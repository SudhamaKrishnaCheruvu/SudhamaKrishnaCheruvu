
package at.gv.magwien.apps.search.criteria.contributor.helper;

import at.gv.magwien.apps.search.criteria.contributor.web.constants.SearchCriteriaContributorWebPortletKeys;
import at.gv.magwien.apps.search.criteria.util.SearchCriteriaConfigurationUtil;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetTagLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.ParseException;
import com.liferay.portal.kernel.search.Query;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchSettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * The Class CriteriaPortletSharedSearchContributorHelper.
 */
/*
 * @author bas9004
 */
public class CriteriaPortletSharedSearchContributorHelper {

    private static final String LONG_STRING = "0";
    private static final String GROUP_ID = "groupId";
    private static Log log = LogFactoryUtil.getLog(CriteriaPortletSharedSearchContributorHelper.class);

    /**
     * Instantiates a new criteria portlet shared search contributor helper.
     */
    private CriteriaPortletSharedSearchContributorHelper() {

    }

    /**
     * Convert array.
     *
     * @param array
     *            the array
     * @param selctedFolderIds
     *            the selcted folder ids
     * @return the list
     */

    public static List<Long> convertArray(long[] array, List<Long> selctedFolderIds) {

        for (long item : array) {
            selctedFolderIds.add(item);
        }
        return selctedFolderIds;
    }

    /**
     * Gets the asset types class names.
     *
     * @param portletSharedSearchSettings
     *            the portlet shared search settings
     * @return the asset types class names
     */

    public static String[] getAssetTypesClassNames(PortletSharedSearchSettings portletSharedSearchSettings) {

        List<String> assetTypes = new ArrayList<>();

        String[] def = {};

        Optional<javax.portlet.PortletPreferences> portletPreferences =
            portletSharedSearchSettings.getPortletPreferences();
        if (portletPreferences.isPresent()) {
            String[] assetTypeIds = portletPreferences.get().getValues(
                SearchCriteriaContributorWebPortletKeys.SEARCH_CRITERIA_SELECTED_ASSETTYPES, def);
            if (assetTypeIds != null && assetTypeIds.length > 0) {
                addSearchAssetType(assetTypes, assetTypeIds);
            } else {
                Map<String, String> allAssetTypes = SearchCriteriaConfigurationUtil.getClassNameIds(
                    portletSharedSearchSettings.getThemeDisplay().getCompanyId(),
                    portletSharedSearchSettings.getThemeDisplay().getLocale());
                for (String assetTypeId : allAssetTypes.keySet()) {
                    try {
                        assetTypes.add(ClassNameLocalServiceUtil.getClassName(Long.valueOf(assetTypeId)).getValue());
                    } catch (NumberFormatException | PortalException e) {
                        log.error("Exception occured " + e);
                    }
                }
            }
        }
        return assetTypes.toArray(new String[0]);
    }

    /**
     * Adds the search asset type.
     *
     * @param assetTypes
     *            the asset types
     * @param assetTypeIds
     *            the asset type ids
     */

    private static void addSearchAssetType(List<String> assetTypes, String[] assetTypeIds) {

        for (String assetTypeId : assetTypeIds) {
            try {
                assetTypes.add(ClassNameLocalServiceUtil.getClassName(Long.valueOf(assetTypeId)).getValue());
            } catch (NumberFormatException | PortalException e) {
                log.error("Exception occured " + e);
            }
        }
    }

    /**
     * Gets the available data.
     *
     * @param valueSet
     *            the value set
     * @param selectedFolderIds
     *            the selected folder ids
     * @param longJournalArray
     *            the long journal array
     * @return the available data
     */

    public static long[] getAvailableData(Set<String> valueSet, List<Long> selectedFolderIds, long[] longJournalArray) {

        String[] availableFolder = valueSet.toArray(new String[0]);
        long[] longSelectedArray = Arrays.stream(availableFolder).mapToLong(i -> Long.valueOf(i)).toArray();
        for (Long l : longJournalArray) {
            longSelectedArray = ArrayUtil.remove(longSelectedArray, l);
        }
        selectedFolderIds.add(0L);
        convertArray(longSelectedArray, selectedFolderIds);
        return longSelectedArray;
    }

    /**
     * Gets the available asset type data.
     *
     * @param valueSet
     *            the value set
     * @param selectedAssetTypeIds
     *            the selected asset type ids
     * @param assetTypeArray
     *            the asset type array
     * @return the available asset type data
     */

    public static String[] getAvailableAssetTypeData(
        Set<String> valueSet, List<String> selectedAssetTypeIds, String[] assetTypeArray) {

        String[] availableAssetType = valueSet.toArray(new String[0]);
        for (String l : assetTypeArray) {
            availableAssetType = ArrayUtil.remove(availableAssetType, l);
        }
        for (String item : availableAssetType) {
            selectedAssetTypeIds.add(String.valueOf(item));
        }
        return selectedAssetTypeIds.toArray(new String[0]);
    }

    /**
     * Gets the selected asset categories.
     *
     * @param portletSharedSearchSettings
     *            the portlet shared search settings
     * @return the selected asset categories
     */
    public static long[] getSelectedAssetCategories(
        PortletSharedSearchSettings portletSharedSearchSettings) {

        List<String> selectedAssetCategories = new ArrayList<>();
        BooleanQuery query = new BooleanQueryImpl();
        Optional<javax.portlet.PortletPreferences> portletPreferences =
            portletSharedSearchSettings.getPortletPreferences();
        if (portletPreferences.isPresent()) {
            addCategories(portletSharedSearchSettings, selectedAssetCategories, query, portletPreferences);
        }
        return getLongArray(selectedAssetCategories);

    }

    /**
     * Adds the categories.
     *
     * @param portletSharedSearchSettings
     *            the portlet shared search settings
     * @param selectedAssetCategories
     *            the selected asset categories
     * @param query
     *            the query
     * @param portletPreferences
     *            the portlet preferences
     * @param vocabularyId
     *            the vocabulary id
     * @return the boolean query
     */

    private static BooleanQuery addCategories(PortletSharedSearchSettings portletSharedSearchSettings,
            List<String> selectedAssetCategories, BooleanQuery query,
            Optional<javax.portlet.PortletPreferences> portletPreferences) {

        if (portletPreferences.isPresent()) {
            String categoryAll = portletPreferences.get().getValue(
                    SearchCriteriaContributorWebPortletKeys.CATEGORIES_ALLORANY,
                    SearchCriteriaContributorWebPortletKeys.ANY);
            String selectedIncludeCategories = portletPreferences.get().getValue(
                    SearchCriteriaContributorWebPortletKeys.SEARCH_CRITERIA_SELECTED_INCLUDE_ASSETCATEGORIES_UUID,
                    StringPool.BLANK);
            String[] selectedCategoryUuIds = selectedIncludeCategories.trim().split(StringPool.COMMA);
            String[] selectedCategoryIds = getAssetCatIds(selectedCategoryUuIds);
            String categoryExcludeAll = portletPreferences.get().getValue(
                    SearchCriteriaContributorWebPortletKeys.CATEGORIES_ALLORANY_EXCLUDE,
                    SearchCriteriaContributorWebPortletKeys.ANY);
            String excludeCategories = portletPreferences.get().getValue(
                    SearchCriteriaContributorWebPortletKeys.SEARCH_CRITERIA_SELECTED_ASSET_CATEGORIESEXLCUDE_UUID,
                    StringPool.BLANK);
            String[] excludeCategoryUuIds = excludeCategories.trim().split(StringPool.COMMA);
            String[] excludeCategoryIds = getAssetCatIds(excludeCategoryUuIds);
            
            if (selectedCategoryIds.length > 0 && Validator.isNotNull(selectedIncludeCategories)) {
                selectedAssetCategories.addAll(Arrays.asList(selectedCategoryIds));
                getBooleanQuery(selectedCategoryIds, categoryAll, query,
                        SearchCriteriaContributorWebPortletKeys.INCLUDE,
                        SearchCriteriaContributorWebPortletKeys.ASSET_CATEGORYIDS);
            }
            if (excludeCategoryIds.length > 0 && Validator.isNotNull(excludeCategories)) {
                getBooleanQuery(excludeCategoryIds, categoryExcludeAll, query,
                        SearchCriteriaContributorWebPortletKeys.EXCLUDE,
                        SearchCriteriaContributorWebPortletKeys.ASSET_CATEGORYIDS);
            }
        }
        if (query.hasClauses()) {
            getNewBooleanClause(portletSharedSearchSettings, query, BooleanClauseOccur.MUST.getName());
        }
        return query;
    }
    
    private static String[] getAssetCatIds(String[] selectedCategoryUuIds) {
        List<String> categoryIds = new ArrayList<>();
        for (String uuid : selectedCategoryUuIds) {
            List<AssetCategory> categories = AssetCategoryLocalServiceUtil.getAssetCategoriesByUuidAndCompanyId(uuid,
                    PortalUtil.getDefaultCompanyId());
            if (!categories.isEmpty()) {
                categoryIds.add(String.valueOf(categories.get(0).getCategoryId()));
            }
        }
        String[] categories = new String[categoryIds.size()];
        int i = 0;
        for (String catId : categoryIds) {
            categories[i++] = catId;
        }
        return categories;
    }

    /**
     * Gets the selected asset tags.
     *
     * @param portletSharedSearchSettings
     *            the portlet shared search settings
     */

    public static void getSelectedAssetTags(PortletSharedSearchSettings portletSharedSearchSettings) {

        BooleanQuery query = new BooleanQueryImpl();
        Optional<javax.portlet.PortletPreferences> portletPreferences =
            portletSharedSearchSettings.getPortletPreferences();
        if (portletPreferences.isPresent()) {
            String tagIncludeAll = portletPreferences.get().getValue(
                SearchCriteriaContributorWebPortletKeys.TAGS_ALLORANY, SearchCriteriaContributorWebPortletKeys.ANY);
            String selectedTagNames = portletPreferences.get().getValue(
                SearchCriteriaContributorWebPortletKeys.SEARCH_CRITERIA_SELECTED_ASSETTAGS, StringPool.BLANK);
            String selectedTagNamesExclude = portletPreferences.get().getValue(
                SearchCriteriaContributorWebPortletKeys.SEARCH_CRITERIA_SELECTED_ASSET_TAGSEXLCUDE, StringPool.BLANK);

            String[] strIncludeTagsArr = selectedTagNames.trim().split(StringPool.COMMA);
            String[] strExcludeTagsArr = selectedTagNamesExclude.trim().split(StringPool.COMMA);
            long groupId = Long.parseLong(portletPreferences.get().getValue(GROUP_ID, LONG_STRING));
            String[] selectedTagNamesListToInclude = new String[strIncludeTagsArr.length];
            String[] selectedTagNamesListToExclude = new String[strExcludeTagsArr.length];
            getAssetTagIds(
                strIncludeTagsArr, strExcludeTagsArr, groupId, selectedTagNamesListToInclude,
                selectedTagNamesListToExclude);

            String tagExcludeAll = portletPreferences.get().getValue(
                SearchCriteriaContributorWebPortletKeys.TAGS_ALLORANY_EXCLUDE,
                SearchCriteriaContributorWebPortletKeys.ANY);

            if (selectedTagNamesListToInclude.length > 0) {
                getBooleanQuery(
                    selectedTagNamesListToInclude, tagIncludeAll, query,
                    SearchCriteriaContributorWebPortletKeys.INCLUDE,
                    SearchCriteriaContributorWebPortletKeys.ASSET_TAGNAMES);
            }
            if (selectedTagNamesListToExclude.length > 0) {
                getBooleanQuery(
                    selectedTagNamesListToExclude, tagExcludeAll, query,
                    SearchCriteriaContributorWebPortletKeys.EXCLUDE,
                    SearchCriteriaContributorWebPortletKeys.ASSET_TAGNAMES);
            }
            if (query.hasClauses()) {
                getNewBooleanClause(portletSharedSearchSettings, query, BooleanClauseOccur.MUST.getName());
            }
        }

    }

    /**
     * Gets the asset tag ids.
     *
     * @param strIncludeTagsArr
     *            the str include tags arr
     * @param strExcludeTagsArr
     *            the str exclude tags arr
     * @param groupId
     *            the group id
     * @param selectedTagNamesListToInclude
     *            the selected tag names list to include
     * @param selectedTagNamesListToExclude
     *            the selected tag names list to exclude
     * @return the asset tag ids
     */
    private static void getAssetTagIds(
        String[] strIncludeTagsArr, String[] strExcludeTagsArr, long groupId, String[] selectedTagNamesListToInclude,
        String[] selectedTagNamesListToExclude) {

        try {
            for (int i = 0; i < strIncludeTagsArr.length; i++) {
                if (Validator.isNotNull(strIncludeTagsArr[i])) {
                    AssetTag assetTag = AssetTagLocalServiceUtil.getTag(groupId, strIncludeTagsArr[i]);
                    selectedTagNamesListToInclude[i] = String.valueOf(assetTag.getTagId());
                }
            }
            for (int i = 0; i < strExcludeTagsArr.length; i++) {
                if (Validator.isNotNull(strExcludeTagsArr[i])) {
                    AssetTag assetTag = AssetTagLocalServiceUtil.getTag(groupId, strExcludeTagsArr[i]);
                    selectedTagNamesListToExclude[i] = String.valueOf(assetTag.getTagId());
                }
            }
        } catch (PortalException e) {
            log.error("Unable to get Asset Tag Id " + e.getMessage());
        }
    }

    /**
     * Gets the new boolean clause.
     *
     * @param portletSharedSearchSettings
     *            the portlet shared search settings
     * @param query
     *            the query
     * @param occur
     *            the occur
     * @return the new boolean clause
     */
    private static void getNewBooleanClause(
        PortletSharedSearchSettings portletSharedSearchSettings, BooleanQuery query, String occur) {

        BooleanClause<Query>[] originalBooleanClauseArray =
            portletSharedSearchSettings.getSearchContext().getBooleanClauses();
        if (originalBooleanClauseArray == null) {
            BooleanClause[] newBooleanClauseArray = {
                BooleanClauseFactoryUtil.create(query, occur)
            };
            portletSharedSearchSettings.getSearchContext().setBooleanClauses(newBooleanClauseArray);
        } else {
            BooleanClause<Query>[] newBooleanClauseArray = new BooleanClause[originalBooleanClauseArray.length + 1];
            for (int i = 0; i < originalBooleanClauseArray.length; i++) {
                newBooleanClauseArray[i] = originalBooleanClauseArray[i];
            }
            newBooleanClauseArray[newBooleanClauseArray.length - 1] = BooleanClauseFactoryUtil.create(query, occur);
            portletSharedSearchSettings.getSearchContext().setBooleanClauses(newBooleanClauseArray);
        }

    }

    /**
     * Gets the boolean query.
     *
     * @param selectedCategoryIds
     *            the selected category ids
     * @param categoryAll
     *            the category all
     * @param query
     *            the query
     * @param include
     *            the include
     * @param fieldName
     *            the field name
     * @return the boolean query
     */
    private static BooleanQuery getBooleanQuery(
        String[] selectedCategoryIds, String categoryAll, BooleanQuery query, String include, String fieldName) {

        try {
            BooleanQuery excludeAny;
            if (include.equalsIgnoreCase(SearchCriteriaContributorWebPortletKeys.INCLUDE)) {
                getIncludeQuery(selectedCategoryIds, fieldName, categoryAll, query);
            } else {
                excludeAny = new BooleanQueryImpl();
                if (categoryAll.equalsIgnoreCase(SearchCriteriaContributorWebPortletKeys.ANY)) {
                    for (String selectedCategoryExclude : selectedCategoryIds) {
                        addBooleanTerms(categoryAll, fieldName, excludeAny, selectedCategoryExclude);
                    }
                    if (excludeAny.hasChildren()) {
                        query.add(excludeAny, BooleanClauseOccur.MUST_NOT);
                    }
                } else {
                    getExcludeAllQuery(selectedCategoryIds, fieldName, query);
                }
            }
        } catch (ParseException e) {
            log.error("Error in parsing on adding field " + fieldName + e);
        }
        return query;
    }

    /**
     * Adds the boolean terms.
     *
     * @param categoryAll
     *            the category all
     * @param fieldName
     *            the field name
     * @param excludeAny
     *            the exclude any
     * @param selectedCategoryExclude
     *            the selected category exclude
     * @throws ParseException
     *             the parse exception
     */
    private static void addBooleanTerms(
        String categoryAll, String fieldName, BooleanQuery excludeAny, String selectedCategoryExclude)
        throws ParseException {

        BooleanQuery subQuery;
        if (Validator.isNotNull(selectedCategoryExclude)) {
            subQuery = new BooleanQueryImpl();
            subQuery.addExactTerm(fieldName, selectedCategoryExclude);
            if (categoryAll.equalsIgnoreCase(SearchCriteriaContributorWebPortletKeys.ANY)) {
                excludeAny.add(subQuery, BooleanClauseOccur.SHOULD);
            } else {
                excludeAny.add(subQuery, BooleanClauseOccur.MUST_NOT);
            }
        }
    }

    /**
     * Gets the include query.
     *
     * @param selectedCategoryIds
     *            the selected category ids
     * @param fieldName
     *            the field name
     * @param categoryAll
     *            the category all
     * @param query
     *            the query
     * @return the include query
     * @throws ParseException
     *             the parse exception
     */
    private static BooleanQuery getIncludeQuery(
        String[] selectedCategoryIds, String fieldName, String categoryAll, BooleanQuery query)
        throws ParseException {

        for (String selectedCategory : selectedCategoryIds) {
            if (Validator.isNotNull(selectedCategory)) {
                BooleanQuery subQuery = new BooleanQueryImpl();
                subQuery.addExactTerm(fieldName, selectedCategory);
                if (categoryAll.equalsIgnoreCase(SearchCriteriaContributorWebPortletKeys.ANY)) {
                    query.add(subQuery, BooleanClauseOccur.SHOULD);
                } else {
                    query.add(subQuery, BooleanClauseOccur.MUST);
                }
            }
        }
        return query;
    }

    /**
     * Gets the exclude all query.
     *
     * @param selectedCategoryIds
     *            the selected category ids
     * @param fieldName
     *            the field name
     * @param query
     *            the query
     * @return the exclude all query
     * @throws ParseException
     *             the parse exception
     */
    private static BooleanQuery getExcludeAllQuery(String[] selectedCategoryIds, String fieldName, BooleanQuery query)
        throws ParseException {

        BooleanQuery subQuery = new BooleanQueryImpl();
        for (String selectedCategory : selectedCategoryIds) {
            if (Validator.isNotNull(selectedCategory)) {
                BooleanQuery termQuery = new BooleanQueryImpl();
                termQuery.addExactTerm(fieldName, selectedCategory);
                subQuery.add(termQuery, BooleanClauseOccur.MUST);
            }
        }
        if (subQuery.hasChildren()) {
            query.add(subQuery, BooleanClauseOccur.MUST_NOT);
        } else {
            query.add(subQuery, BooleanClauseOccur.MUST);
        }
        return query;
    }

    /**
     * Gets the long array.
     *
     * @param stringList
     *            the string list
     * @return the long array
     */
    private static long[] getLongArray(List<String> stringList) {

        long[] longArray = new long[stringList.size()];
        for (int i = 0; i < stringList.size(); i++) {
            longArray[i] = Long.valueOf(stringList.get(i));
        }
        return longArray;
    }
}
