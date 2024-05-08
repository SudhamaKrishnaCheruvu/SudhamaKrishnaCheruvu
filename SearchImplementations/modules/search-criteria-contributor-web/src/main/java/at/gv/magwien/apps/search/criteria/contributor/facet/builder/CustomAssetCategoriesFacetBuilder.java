package at.gv.magwien.apps.search.criteria.contributor.facet.builder;

import at.gv.magwien.apps.search.criteria.contributor.facet.preferences.CustomAssetCategoriesFacetConfiguration;
import at.gv.magwien.apps.search.criteria.contributor.facet.preferences.CustomAssetCategoriesFacetConfigurationImpl;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.config.FacetConfiguration;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.search.facet.Facet;
import com.liferay.portal.search.facet.category.CategoryFacetFactory;

/**
 * Class CustomAssetCategoriesFacetBuilder.
 * @author bas9004
 *
 */
public class CustomAssetCategoriesFacetBuilder {

    public CustomAssetCategoriesFacetBuilder(CategoryFacetFactory categoryFacetFactory) {
        this.categoryFacetFactory = categoryFacetFactory;
    }

    /**
     * Method Facet build.
     * @return
     */
    public Facet build() {
        Facet facet = categoryFacetFactory.newInstance(searchContext);
        facet.setAggregationName(getAggregationName(facet.getFieldName()));
        facet.setFacetConfiguration(buildFacetConfiguration(facet));
        if (selectedCategoryIds != null) {
            facet.select(ArrayUtil.toStringArray(selectedCategoryIds));
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

    public void setSelectedCategoryIds(long... selectedCategoryIds) {
        this.selectedCategoryIds = selectedCategoryIds;
    }

    protected FacetConfiguration buildFacetConfiguration(Facet facet) {
        FacetConfiguration facetConfiguration = new FacetConfiguration();
        facetConfiguration.setFieldName(facet.getFieldName());
        facetConfiguration.setLabel("any-category");
        facetConfiguration.setOrder("OrderHitsDesc");
        facetConfiguration.setStatic(false);
        facetConfiguration.setWeight(1.6);
        CustomAssetCategoriesFacetConfiguration assetCategoriesFacetConfiguration = 
                new CustomAssetCategoriesFacetConfigurationImpl(facetConfiguration);
        assetCategoriesFacetConfiguration.setFrequencyThreshold(frequencyThreshold);
        assetCategoriesFacetConfiguration.setMaxTerms(maxTerms);
        return facetConfiguration;
    }

    protected String getAggregationName(String fieldName) {
        return fieldName + StringPool.PERIOD + portletId;
    }

    private final CategoryFacetFactory categoryFacetFactory;
    private int frequencyThreshold;
    private int maxTerms;
    private String portletId;
    private SearchContext searchContext;
    private long[] selectedCategoryIds;
}
