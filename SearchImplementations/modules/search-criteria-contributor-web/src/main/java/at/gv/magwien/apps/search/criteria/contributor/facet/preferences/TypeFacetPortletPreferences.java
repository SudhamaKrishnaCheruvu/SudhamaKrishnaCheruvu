package at.gv.magwien.apps.search.criteria.contributor.facet.preferences;

import com.liferay.portal.kernel.util.KeyValuePair;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Interface TypeFacetPortletPreferences.
 * @author bas9004
 *
 */
public interface TypeFacetPortletPreferences {

    public static final String PREFERENCE_KEY_ASSET_TYPES = "assetTypes";

    public static final String PREFERENCE_KEY_FREQUENCIES_VISIBLE = "frequenciesVisible";

    public static final String PREFERENCE_KEY_FREQUENCY_THRESHOLD = "frequencyThreshold";

    public static final String PREFERENCE_KEY_PARAMETER_NAME = "parameterName";

    public Optional<String[]> getAssetTypesArray();

    public String getAssetTypesString();

    public List<KeyValuePair> getAvailableAssetTypes(long companyId, Locale locale);

    public List<KeyValuePair> getCurrentAssetTypes(long companyId, Locale locale);

    public String[] getCurrentAssetTypesArray(long companyId);

    public int getFrequencyThreshold();

    public String getParameterName();

    public boolean isFrequenciesVisible();

}
