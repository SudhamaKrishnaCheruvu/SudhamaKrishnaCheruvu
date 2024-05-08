package at.gv.magwien.apps.search.criteria.contributor.facet.preferences;

/**
 * Interface CustomFolderFacetPortletPreferences.
 * @author bas9004
 *
 */
public interface CustomFolderFacetPortletPreferences {

    public static final String PREFERENCE_KEY_FREQUENCIES_VISIBLE = "frequenciesVisible";

    public static final String PREFERENCE_KEY_FREQUENCY_THRESHOLD = "frequencyThreshold";

    public static final String PREFERENCE_KEY_MAX_TERMS = "maxTerms";

    public static final String PREFERENCE_KEY_PARAMETER_NAME = "parameterName";

    public int getFrequencyThreshold();

    public int getMaxTerms();

    public String getParameterName();

    public boolean isFrequenciesVisible();

}
