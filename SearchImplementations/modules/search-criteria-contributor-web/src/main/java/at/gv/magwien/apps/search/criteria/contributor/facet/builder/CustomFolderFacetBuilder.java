package at.gv.magwien.apps.search.criteria.contributor.facet.builder;

import at.gv.magwien.apps.search.criteria.contributor.facet.preferences.CustomFolderFacetConfiguration;
import at.gv.magwien.apps.search.criteria.contributor.facet.preferences.CustomFolderFacetConfigurationImpl;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.config.FacetConfiguration;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.search.facet.Facet;
import com.liferay.portal.search.facet.folder.FolderFacetFactory;

/**
 * Class CustomFolderFacetBuilder.
 * @author bas9004
 *
 */
public class CustomFolderFacetBuilder {
    public CustomFolderFacetBuilder(FolderFacetFactory folderFacetFactory) {
        this.folderFacetFactory = folderFacetFactory;
    }

    /**
     * Method Facet build.
     * @return
     */
    public Facet build() {
        Facet facet = folderFacetFactory.newInstance(searchContext);
        facet.setAggregationName(getAggregationName(facet.getFieldName()));
        facet.setFacetConfiguration(buildFacetConfiguration(facet));
        if (selectedFolderIds != null) {
            facet.select(ArrayUtil.toStringArray(selectedFolderIds));
        }
        return facet;
    }

    public void setFrequencyThreshold(int frequencyThreshold) {
        this.frequencyThreshold = frequencyThreshold;
    }

    public void setMaxTerms(int maxTerms) {
        this.maxTerms = maxTerms;
    }

    public void setPortletId(String portletId) {
        this.portletId = portletId;
    }

    public void setSearchContext(SearchContext searchContext) {
        this.searchContext = searchContext;
    }

    public void setSelectedFolderIds(long... selectedFolderIds) {
        this.selectedFolderIds = selectedFolderIds;
    }

    protected FacetConfiguration buildFacetConfiguration(Facet facet) {
        FacetConfiguration facetConfiguration = new FacetConfiguration();
        facetConfiguration.setFieldName(facet.getFieldName());
        facetConfiguration.setLabel("any-folder");
        facetConfiguration.setOrder("OrderHitsDesc");
        facetConfiguration.setStatic(false);
        facetConfiguration.setWeight(1.4);
        CustomFolderFacetConfiguration folderFacetConfiguration = new CustomFolderFacetConfigurationImpl(
                facetConfiguration);
        folderFacetConfiguration.setFrequencyThreshold(frequencyThreshold);
        folderFacetConfiguration.setMaxTerms(maxTerms);
        return facetConfiguration;
    }

    protected String getAggregationName(String fieldName) {
        return fieldName + StringPool.PERIOD + portletId;
    }

    private final FolderFacetFactory folderFacetFactory;
    private int frequencyThreshold;
    private int maxTerms;
    private String portletId;
    private SearchContext searchContext;
    private long[] selectedFolderIds;
}
