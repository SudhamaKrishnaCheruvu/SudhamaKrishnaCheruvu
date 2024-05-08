package at.gv.magwien.apps.search.criteria.contributor.facet.preferences;

import at.gv.magwien.apps.search.criteria.contributor.facet.preferences.helper.FolderPortletPreferencesHelper;

import java.util.Optional;
import javax.portlet.PortletPreferences;

/**
 * Class CustomFolderFacetPortletPreferencesImpl.
 * @author bas9004
 *
 */
public class CustomFolderFacetPortletPreferencesImpl implements CustomFolderFacetPortletPreferences {

    public CustomFolderFacetPortletPreferencesImpl(Optional<PortletPreferences> portletPreferencesOptional) {
        portletPreferencesHelper = new FolderPortletPreferencesHelper(portletPreferencesOptional);
    }

    @Override
    public int getFrequencyThreshold() {
        return portletPreferencesHelper
                .getInteger(CustomFolderFacetPortletPreferences.PREFERENCE_KEY_FREQUENCY_THRESHOLD, 1);
    }

    @Override
    public int getMaxTerms() {
        return portletPreferencesHelper.getInteger(CustomFolderFacetPortletPreferences.PREFERENCE_KEY_MAX_TERMS, 10);
    }

    @Override
    public String getParameterName() {
        return portletPreferencesHelper.getString(CustomFolderFacetPortletPreferences.PREFERENCE_KEY_PARAMETER_NAME,
                "folder");
    }

    @Override
    public boolean isFrequenciesVisible() {
        return portletPreferencesHelper
                .getBoolean(CustomFolderFacetPortletPreferences.PREFERENCE_KEY_FREQUENCIES_VISIBLE, true);
    }

    private final FolderPortletPreferencesHelper portletPreferencesHelper;

}
