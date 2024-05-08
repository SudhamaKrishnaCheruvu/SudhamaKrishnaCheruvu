package at.gv.magwien.apps.search.criteria.contributor.facet.preferences;

/**
 * Interface CustomAssetTagsFacetConfiguration.
 * @author bas9004
 *
 */
public interface CustomAssetTagsFacetConfiguration {

    public int getFrequencyThreshold();

    public int getMaxTerms();

    public void setFrequencyThreshold(int frequencyThreshold);

    public void setMaxTerms(int maxTerms);

}
