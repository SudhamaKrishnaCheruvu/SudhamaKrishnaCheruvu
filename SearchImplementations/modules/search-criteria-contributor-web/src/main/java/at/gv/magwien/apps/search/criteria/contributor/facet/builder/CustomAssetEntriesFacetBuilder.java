package at.gv.magwien.apps.search.criteria.contributor.facet.builder;

import at.gv.magwien.apps.search.criteria.contributor.facet.preferences.AssetEntriesFacetConfiguration;
import at.gv.magwien.apps.search.criteria.contributor.facet.preferences.AssetEntriesFacetConfigurationImpl;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.config.FacetConfiguration;
import com.liferay.portal.search.facet.Facet;
import com.liferay.portal.search.facet.type.AssetEntriesFacetFactory;

/**
 * Class CustomAssetEntriesFacetBuilder.
 * @author bas9004
 *
 */
public class CustomAssetEntriesFacetBuilder {

    public CustomAssetEntriesFacetBuilder(AssetEntriesFacetFactory assetEntriesFacetFactory) {
        this.assetEntriesFacetFactory = assetEntriesFacetFactory;
    }

    /**
     * Method Facet build.
     * @return
     */
    public Facet build() {
        Facet facet = assetEntriesFacetFactory.newInstance(searchContext);
        facet.setAggregationName(getAggregationName(facet.getFieldName()));
        facet.setFacetConfiguration(buildFacetConfiguration(facet));
        facet.select(selectedEntryClassNames);
        return facet;
    }

    public void setFrequencyThreshold(int frequencyThreshold) {
        this.frequencyThreshold = frequencyThreshold;
    }

    public void setPortletId(String portletId) {
        this.portletId = portletId;
    }

    public void setSearchContext(SearchContext searchContext) {
        this.searchContext = searchContext;
    }

    public void setSelectedEntryClassNames(String... selectedEntryClassNames) {
        this.selectedEntryClassNames = selectedEntryClassNames;
    }

    protected FacetConfiguration buildFacetConfiguration(Facet facet) {
        FacetConfiguration facetConfiguration = new FacetConfiguration();
        facetConfiguration.setFieldName(facet.getFieldName());
        facetConfiguration.setLabel("any-asset");
        facetConfiguration.setOrder("OrderHitsDesc");
        facetConfiguration.setStatic(false);
        facetConfiguration.setWeight(1.6);
        AssetEntriesFacetConfiguration assetEntriesFacetConfiguration = new AssetEntriesFacetConfigurationImpl(
                facetConfiguration);
        assetEntriesFacetConfiguration.setFrequencyThreshold(frequencyThreshold);
        return facetConfiguration;
    }

    protected String getAggregationName(String fieldName) {
        return fieldName + StringPool.PERIOD + portletId;
    }

    private final AssetEntriesFacetFactory assetEntriesFacetFactory;
    private int frequencyThreshold;
    private String portletId;
    private SearchContext searchContext;
    private String[] selectedEntryClassNames;

}
