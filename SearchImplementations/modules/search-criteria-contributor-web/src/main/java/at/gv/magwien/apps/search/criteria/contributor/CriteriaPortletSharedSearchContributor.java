package at.gv.magwien.apps.search.criteria.contributor;

import at.gv.magwien.apps.search.criteria.contributor.facet.builder.CustomAssetCategoriesFacetBuilder;
import at.gv.magwien.apps.search.criteria.contributor.facet.builder.CustomAssetEntriesFacetBuilder;
import at.gv.magwien.apps.search.criteria.contributor.facet.builder.CustomAssetTagsFacetBuilder;
import at.gv.magwien.apps.search.criteria.contributor.facet.builder.CustomFacetBuilder;
import at.gv.magwien.apps.search.criteria.contributor.facet.builder.CustomFolderFacetBuilder;
import at.gv.magwien.apps.search.criteria.contributor.facet.preferences.CustomCategoryFacetPortletPreferences;
import at.gv.magwien.apps.search.criteria.contributor.facet.preferences.CustomCategoryFacetPortletPreferencesImpl;
import at.gv.magwien.apps.search.criteria.contributor.facet.preferences.CustomFolderFacetPortletPreferences;
import at.gv.magwien.apps.search.criteria.contributor.facet.preferences.CustomFolderFacetPortletPreferencesImpl;
import at.gv.magwien.apps.search.criteria.contributor.facet.preferences.TagFacetPortletPreferences;
import at.gv.magwien.apps.search.criteria.contributor.facet.preferences.TagFacetPortletPreferencesImpl;
import at.gv.magwien.apps.search.criteria.contributor.facet.preferences.TypeFacetPortletPreferences;
import at.gv.magwien.apps.search.criteria.contributor.facet.preferences.TypeFacetPortletPreferencesImpl;
import at.gv.magwien.apps.search.criteria.contributor.helper.CriteriaPortletSharedSearchContributorHelper;
import at.gv.magwien.apps.search.criteria.contributor.web.constants.SearchCriteriaContributorWebPortletKeys;
import at.gv.magwien.apps.search.criteria.util.SearchCriteriaConfigurationUtil;

import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalFolderLocalService;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.search.constants.SearchContextAttributes;
import com.liferay.portal.search.facet.category.CategoryFacetFactory;
import com.liferay.portal.search.facet.custom.CustomFacetFactory;
import com.liferay.portal.search.facet.folder.FolderFacetFactory;
import com.liferay.portal.search.facet.tag.AssetTagNamesFacetFactory;
import com.liferay.portal.search.facet.type.AssetEntriesFacetFactory;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchContributor;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchSettings;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Class CriteriaPortletSharedSearchContributor.
 * @author bas9004
 *
 */
@Component(immediate = true, property = "javax.portlet.name="
        + SearchCriteriaContributorWebPortletKeys.SEARCH_CRITERIA_CONTRIBUTORWEB, 
        service = PortletSharedSearchContributor.class)
public class CriteriaPortletSharedSearchContributor implements PortletSharedSearchContributor {

    @Override
    public void contribute(PortletSharedSearchSettings portletSharedSearchSettings) {
        TypeFacetPortletPreferences typeFacetPortletPreferences = new TypeFacetPortletPreferencesImpl(
                portletSharedSearchSettings.getPortletPreferences());
        Facet facet = buildFacet(typeFacetPortletPreferences, portletSharedSearchSettings);
        portletSharedSearchSettings.addFacet(facet);
        SearchContext searchContext = portletSharedSearchSettings.getSearchContext();
        searchContext.setAttribute(SearchContextAttributes.ATTRIBUTE_KEY_EMPTY_SEARCH, Boolean.TRUE);
        CustomFolderFacetPortletPreferences folderFacetPortletPreferences = new CustomFolderFacetPortletPreferencesImpl(
                portletSharedSearchSettings.getPortletPreferences());
        Facet folderFacet = buildFolderFacet(folderFacetPortletPreferences, portletSharedSearchSettings);
        portletSharedSearchSettings.addFacet(folderFacet);
        CustomCategoryFacetPortletPreferences categoryFacetPortletPreferences = 
                new CustomCategoryFacetPortletPreferencesImpl(
                portletSharedSearchSettings.getPortletPreferences());
        buildCategoryFacet(categoryFacetPortletPreferences, portletSharedSearchSettings);
        TagFacetPortletPreferences tagFacetPortletPreferences = new TagFacetPortletPreferencesImpl(
                portletSharedSearchSettings.getPortletPreferences());
        buildTagFacet(tagFacetPortletPreferences, portletSharedSearchSettings);
        Facet structureFacet = buildStructureFacet(portletSharedSearchSettings);
        portletSharedSearchSettings.addFacet(structureFacet);
    }

    protected Facet buildFacet(TypeFacetPortletPreferences typeFacetPortletPreferences,
            PortletSharedSearchSettings portletSharedSearchSettings) {
        CustomAssetEntriesFacetBuilder assetEntriesFacetBuilder = new CustomAssetEntriesFacetBuilder(
                assetEntriesFacetFactory);
        assetEntriesFacetBuilder.setFrequencyThreshold(typeFacetPortletPreferences.getFrequencyThreshold());
        assetEntriesFacetBuilder.setPortletId(portletSharedSearchSettings.getPortletId());
        assetEntriesFacetBuilder.setSearchContext(portletSharedSearchSettings.getSearchContext());
        String[] assetTypeIds = CriteriaPortletSharedSearchContributorHelper
                .getAssetTypesClassNames(portletSharedSearchSettings);
        Optional<javax.portlet.PortletPreferences> portletPreferences = portletSharedSearchSettings
                .getPortletPreferences();
        if (portletPreferences.isPresent()) {
            String assetTypeInclude = portletPreferences.get().getValue(
                    SearchCriteriaContributorWebPortletKeys.ASSET_TYPE_INCLUDE,
                    SearchCriteriaContributorWebPortletKeys.INCLUDE);
            if (assetTypeInclude.equalsIgnoreCase(SearchCriteriaContributorWebPortletKeys.INCLUDE)) {
                if (assetTypeIds.length > 0) {
                    assetEntriesFacetBuilder.setSelectedEntryClassNames(assetTypeIds);
                }
            } else {
                ArrayList<String> selectedAssetTypes = new ArrayList<>();
                Set<String> assetTypeSet = new HashSet(SearchCriteriaConfigurationUtil
                        .getClassNameIds(portletSharedSearchSettings.getThemeDisplay().getCompanyId(),
                                portletSharedSearchSettings.getThemeDisplay().getLocale())
                        .values());
                assetEntriesFacetBuilder.setSelectedEntryClassNames(CriteriaPortletSharedSearchContributorHelper
                        .getAvailableAssetTypeData(assetTypeSet, selectedAssetTypes, assetTypeIds));
            }
        }
        return assetEntriesFacetBuilder.build();
    }

    protected Facet buildStructureFacet(PortletSharedSearchSettings portletSharedSearchSettings) {
        CustomFacetBuilder customFacetBuilder = new CustomFacetBuilder(customFacetFactory);
        customFacetBuilder.setAggregationName("ddmStructureKey");
        customFacetBuilder.setFieldToAggregate("ddmStructureKey");
        customFacetBuilder.setSearchContext(portletSharedSearchSettings.getSearchContext());
        String[] test = {};
        Optional<javax.portlet.PortletPreferences> portletPreferences = portletSharedSearchSettings
                .getPortletPreferences();
        if (portletPreferences.isPresent()) {
            String[] selectedStructureIds = portletPreferences.get()
                    .getValues(SearchCriteriaContributorWebPortletKeys.SEARCH_CRITERIA_SELECTED_STRUCTURES, test);
            String structureInclude = portletPreferences.get().getValue(
                    SearchCriteriaContributorWebPortletKeys.STRUCTURE_INCLUDE,
                    SearchCriteriaContributorWebPortletKeys.INCLUDE);
            if (structureInclude.equalsIgnoreCase(SearchCriteriaContributorWebPortletKeys.INCLUDE)) {
                customFacetBuilder.setSelectedValues(selectedStructureIds);
            } else {
                Set<String> allStructures = SearchCriteriaConfigurationUtil
                        .getStructures(portletSharedSearchSettings.getThemeDisplay().getScopeGroupId(),
                                portletSharedSearchSettings.getThemeDisplay().getCompanyId(),
                                portletSharedSearchSettings.getThemeDisplay().getLocale())
                        .keySet();
                String[] allStructureArray = allStructures.toArray(new String[0]);
                for (String selectedStructure : selectedStructureIds) {
                    allStructureArray = ArrayUtil.remove(allStructureArray, selectedStructure);
                }
                customFacetBuilder.setSelectedValues(allStructureArray);
            }
        }
        return customFacetBuilder.build();
    }

    protected Facet buildFolderFacet(CustomFolderFacetPortletPreferences folderFacetPortletPreferences,
            PortletSharedSearchSettings portletSharedSearchSettings) {
        CustomFolderFacetBuilder folderFacetBuilder = new CustomFolderFacetBuilder(folderFacetFactory);
        folderFacetBuilder.setFrequencyThreshold(folderFacetPortletPreferences.getFrequencyThreshold());
        folderFacetBuilder.setMaxTerms(folderFacetPortletPreferences.getMaxTerms());
        folderFacetBuilder.setPortletId(portletSharedSearchSettings.getPortletId());
        folderFacetBuilder.setSearchContext(portletSharedSearchSettings.getSearchContext());
        ArrayList<Long> selectedFolderIds = new ArrayList<>();
        String[] test = {};
        Optional<javax.portlet.PortletPreferences> portletPreferences = portletSharedSearchSettings
                .getPortletPreferences();
        if (portletPreferences.isPresent()) {
            String journalFolderInclude = portletPreferences.get().getValue(
                    SearchCriteriaContributorWebPortletKeys.JOURNAL_TYPE_INCLUDE,
                    SearchCriteriaContributorWebPortletKeys.INCLUDE);
            String[] journalFolderIds = portletPreferences.get()
                    .getValues(SearchCriteriaContributorWebPortletKeys.SEARCH_CRITERIA_SELECTED_JOURNALFOLDER, test);
            long[] longJournalArray = getJournalFolderIds(journalFolderIds);
            if (journalFolderInclude.equalsIgnoreCase(SearchCriteriaContributorWebPortletKeys.INCLUDE)) {
                if (longJournalArray.length > 0) {
                    CriteriaPortletSharedSearchContributorHelper.convertArray(longJournalArray, selectedFolderIds);
                }
            } else {
                Set<String> journalSet = SearchCriteriaConfigurationUtil
                        .getJournalFolderIds(portletSharedSearchSettings.getThemeDisplay().getScopeGroupId());
                CriteriaPortletSharedSearchContributorHelper.getAvailableData(journalSet, selectedFolderIds,
                        longJournalArray);
            }
            String[] folderIds = portletPreferences.get()
                    .getValues(SearchCriteriaContributorWebPortletKeys.SEARCH_CRITERIA_SELECTED_DOCUMENTFOLDER, test);
            long[] longDocFolderArray = getDLFolderIds(folderIds);
            String documentFolderInclude = portletPreferences.get().getValue(
                    SearchCriteriaContributorWebPortletKeys.DOCUMENT_TYPE_INCLUDE,
                    SearchCriteriaContributorWebPortletKeys.INCLUDE);
            if (documentFolderInclude.equalsIgnoreCase(SearchCriteriaContributorWebPortletKeys.INCLUDE)) {
                if (longDocFolderArray.length > 0) {
                    CriteriaPortletSharedSearchContributorHelper.convertArray(longDocFolderArray, selectedFolderIds);
                }
            } else {
                Set<String> documentSet = SearchCriteriaConfigurationUtil
                        .getDLFolderIds(portletSharedSearchSettings.getThemeDisplay().getScopeGroupId());
                CriteriaPortletSharedSearchContributorHelper.getAvailableData(documentSet, selectedFolderIds,
                        longDocFolderArray);
            }
            folderFacetBuilder.setSelectedFolderIds(ArrayUtil.toLongArray(selectedFolderIds));
        }
        return folderFacetBuilder.build();
    }
    
    private long[] getJournalFolderIds(String[] journalFolderIds) {
        List<Long> folderIds = new ArrayList<>();
        for (String uuid : journalFolderIds) {
            List<JournalFolder> folders = journalFolderLocalService.getJournalFoldersByUuidAndCompanyId(uuid,
                    PortalUtil.getDefaultCompanyId());
            if (!folders.isEmpty()) {
                folderIds.add(folders.get(0).getFolderId());
            }
        }
        long[] folders = new long[folderIds.size()];
        int i = 0;
        for (Long folderId : folderIds) {
            folders[i++] = folderId;
        }
        return folders;
    }

    private long[] getDLFolderIds(String[] dlFolderIds) {
        List<Long> folderIds = new ArrayList<>();
        for (String uuid : dlFolderIds) {
            List<DLFolder> dlFolders = dlLFolderLocalService.getDLFoldersByUuidAndCompanyId(uuid,
                    PortalUtil.getDefaultCompanyId());
            if (!dlFolders.isEmpty()) {
                folderIds.add(dlFolders.get(0).getFolderId());
            }
        }
        long[] folders = new long[folderIds.size()];
        int i = 0;
        for (Long folderId : folderIds) {
            folders[i++] = folderId;
        }
        return folders;
    }

    protected Facet buildCategoryFacet(CustomCategoryFacetPortletPreferences categoryFacetPortletPreferences,
            PortletSharedSearchSettings portletSharedSearchSettings) {
        SearchContext searchContext1 = new SearchContext();
        CustomAssetCategoriesFacetBuilder assetCategoriesFacetBuilder = new CustomAssetCategoriesFacetBuilder(
                categoryFacetFactory);
        searchContext1.setAndSearch(true);
        assetCategoriesFacetBuilder.setFrequencyThreshold(categoryFacetPortletPreferences.getFrequencyThreshold());
        assetCategoriesFacetBuilder.setMaxTerms(categoryFacetPortletPreferences.getMaxTerms());
        assetCategoriesFacetBuilder.setPortletId(portletSharedSearchSettings.getPortletId());
        assetCategoriesFacetBuilder.setSearchContext(portletSharedSearchSettings.getSearchContext());
        CriteriaPortletSharedSearchContributorHelper.getSelectedAssetCategories(portletSharedSearchSettings);
        return assetCategoriesFacetBuilder.build();
    }

    protected Facet buildTagFacet(TagFacetPortletPreferences tagFacetPortletPreferences,
            PortletSharedSearchSettings portletSharedSearchSettings) {
        CustomAssetTagsFacetBuilder assetTagsFacetBuilder = new CustomAssetTagsFacetBuilder(assetTagNamesFacetFactory);
        assetTagsFacetBuilder.setFrequencyThreshold(tagFacetPortletPreferences.getFrequencyThreshold());
        assetTagsFacetBuilder.setMaxTerms(tagFacetPortletPreferences.getMaxTerms());
        assetTagsFacetBuilder.setPortletId(portletSharedSearchSettings.getPortletId());
        assetTagsFacetBuilder.setSearchContext(portletSharedSearchSettings.getSearchContext());
        CriteriaPortletSharedSearchContributorHelper.getSelectedAssetTags(portletSharedSearchSettings);
        return assetTagsFacetBuilder.build();
    }

    public static <T> void copy(Supplier<Optional<T>> from, Consumer<T> to) {
        Optional<T> optional = from.get();
        optional.ifPresent(to);
    }

    @Reference
    protected AssetTagNamesFacetFactory assetTagNamesFacetFactory;

    @Reference
    protected CategoryFacetFactory categoryFacetFactory;

    @Reference
    protected FolderFacetFactory folderFacetFactory;

    @Reference
    protected AssetEntriesFacetFactory assetEntriesFacetFactory;
    
    @Reference
    private JournalFolderLocalService journalFolderLocalService;
    
    @Reference
    private DLFolderLocalService dlLFolderLocalService;

    @Reference
    protected CustomFacetFactory customFacetFactory;

}
