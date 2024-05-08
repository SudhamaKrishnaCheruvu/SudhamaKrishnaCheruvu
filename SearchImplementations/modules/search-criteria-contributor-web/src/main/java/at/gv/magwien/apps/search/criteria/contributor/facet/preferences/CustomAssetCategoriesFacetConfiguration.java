package at.gv.magwien.apps.search.criteria.contributor.facet.preferences;

/**
 * Inteface CustomAssetCategoriesFacetConfiguration.
 * @author bas9004
 *
 */
public interface CustomAssetCategoriesFacetConfiguration {

    public int getFrequencyThreshold();

    public int getMaxTerms();

    public void setFrequencyThreshold(int frequencyThreshold);

    public void setMaxTerms(int maxTerms);
}
