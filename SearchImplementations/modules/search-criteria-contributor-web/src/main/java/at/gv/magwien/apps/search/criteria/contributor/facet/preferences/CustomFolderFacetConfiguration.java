package at.gv.magwien.apps.search.criteria.contributor.facet.preferences;

/**
 * Interface CustomFolderFacetConfiguration.
 * @author bas9004
 *
 */
public interface CustomFolderFacetConfiguration {

    public int getFrequencyThreshold();

    public int getMaxTerms();

    public void setFrequencyThreshold(int frequencyThreshold);

    public void setMaxTerms(int maxTerms);

}
