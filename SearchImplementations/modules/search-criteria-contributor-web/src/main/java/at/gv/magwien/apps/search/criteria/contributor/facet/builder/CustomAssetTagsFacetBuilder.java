package at.gv.magwien.apps.search.criteria.contributor.facet.builder;

import at.gv.magwien.apps.search.criteria.contributor.facet.preferences.CustomAssetTagsFacetConfiguration;
import at.gv.magwien.apps.search.criteria.contributor.facet.preferences.CustomAssetTagsFacetConfigurationImpl;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.config.FacetConfiguration;
import com.liferay.portal.search.facet.Facet;
import com.liferay.portal.search.facet.tag.AssetTagNamesFacetFactory;

/**
 * Class CustomAssetTagsFacetBuilder.
 * @author bas9004
 *
 */
public class CustomAssetTagsFacetBuilder {

    public CustomAssetTagsFacetBuilder(AssetTagNamesFacetFactory assetTagNamesFacetFactory) {
        this.assetTagNamesFacetFactory = assetTagNamesFacetFactory;
    }

    /**
     * Method Facet build.
     * @return
     */
    public Facet build() {
        Facet facet = assetTagNamesFacetFactory.newInstance(searchContext);
        facet.setAggregationName(getAggregationName(facet.getFieldName()));
        facet.setFacetConfiguration(buildFacetConfiguration(facet));
        facet.select(selectedTagNames);
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

    public void setSelectedTagNames(String... selectedTagNames) {
        this.selectedTagNames = selectedTagNames;
    }

    protected FacetConfiguration buildFacetConfiguration(Facet facet) {
        FacetConfiguration facetConfiguration = new FacetConfiguration();
        facetConfiguration.setFieldName(facet.getFieldName());
        facetConfiguration.setLabel("any-tag");
        facetConfiguration.setOrder("OrderHitsDesc");
        facetConfiguration.setStatic(false);
        facetConfiguration.setWeight(1.4);
        CustomAssetTagsFacetConfiguration assetTagsFacetConfiguration = new CustomAssetTagsFacetConfigurationImpl(
                facetConfiguration);
        assetTagsFacetConfiguration.setFrequencyThreshold(frequencyThreshold);
        assetTagsFacetConfiguration.setMaxTerms(maxTerms);
        return facetConfiguration;
    }

    protected String getAggregationName(String fieldName) {
        return fieldName + StringPool.PERIOD + portletId;
    }

    private final AssetTagNamesFacetFactory assetTagNamesFacetFactory;
    private int frequencyThreshold;
    private int maxTerms;
    private String portletId;
    private SearchContext searchContext;
    private String[] selectedTagNames;

}
