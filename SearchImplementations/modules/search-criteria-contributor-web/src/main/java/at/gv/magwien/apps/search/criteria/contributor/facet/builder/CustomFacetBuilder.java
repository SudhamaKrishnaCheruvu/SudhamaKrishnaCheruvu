package at.gv.magwien.apps.search.criteria.contributor.facet.builder;

import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.config.FacetConfiguration;
import com.liferay.portal.search.facet.Facet;
import com.liferay.portal.search.facet.custom.CustomFacetFactory;

/**
 * Class CustomFacetBuilder.
 * @author bas9004
 *
 */
public class CustomFacetBuilder {

    public CustomFacetBuilder(CustomFacetFactory customFacetFactory) {
        this.customFacetFactory = customFacetFactory;
    }

    /**
     * Method Facet build.
     * @return
     */
    public Facet build() {
        Facet facet = customFacetFactory.newInstance(searchContext);
        facet.setFieldName(fieldToAggregate);
        facet.setFacetConfiguration(buildFacetConfiguration(facet));
        facet.select(selectedValues);
        facet.setAggregationName(aggregationName);
        return facet;
    }

    public void setAggregationName(String aggregationName) {
        this.aggregationName = aggregationName;
    }

    public void setFieldToAggregate(String fieldToAggregate) {
        this.fieldToAggregate = fieldToAggregate;
    }

    public void setSearchContext(SearchContext searchContext) {
        this.searchContext = searchContext;
    }

    public void setSelectedValues(String... selectedValues) {
        this.selectedValues = selectedValues;
    }

    protected FacetConfiguration buildFacetConfiguration(Facet facet) {
        FacetConfiguration facetConfiguration = new FacetConfiguration();
        facetConfiguration.setFieldName(facet.getFieldName());
        facetConfiguration.setOrder("OrderHitsDesc");
        facetConfiguration.setStatic(false);
        facetConfiguration.setWeight(1.1);
        return facetConfiguration;
    }

    private String aggregationName;
    private final CustomFacetFactory customFacetFactory;
    private String fieldToAggregate;
    private SearchContext searchContext;
    private String[] selectedValues;

}
