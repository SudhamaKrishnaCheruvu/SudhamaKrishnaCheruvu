package at.gv.magwien.apps.search.criteria.contributor.facet.preferences;

import at.gv.magwien.apps.search.criteria.contributor.facet.preferences.helper.PortletPreferencesHelper;

import java.util.Optional;
import javax.portlet.PortletPreferences;

/**
 * Class CustomCategoryFacetPortletPreferencesImpl.
 * @author bas9004
 *
 */
public class CustomCategoryFacetPortletPreferencesImpl implements CustomCategoryFacetPortletPreferences {

    public CustomCategoryFacetPortletPreferencesImpl(Optional<PortletPreferences> portletPreferencesOptional) {

        portletPreferencesHelper = new PortletPreferencesHelper(portletPreferencesOptional);
    }

    @Override
    public String getDisplayStyle() {
        return portletPreferencesHelper.getString(CustomCategoryFacetPortletPreferences.PREFERENCE_KEY_DISPLAY_STYLE,
                "cloud");
    }

    @Override
    public int getFrequencyThreshold() {
        return portletPreferencesHelper
                .getInteger(CustomCategoryFacetPortletPreferences.PREFERENCE_KEY_FREQUENCY_THRESHOLD, 1);
    }

    @Override
    public int getMaxTerms() {
        return portletPreferencesHelper.getInteger(CustomCategoryFacetPortletPreferences.PREFERENCE_KEY_MAX_TERMS, 10);
    }

    @Override
    public String getParameterName() {
        return portletPreferencesHelper.getString(CustomCategoryFacetPortletPreferences.PREFERENCE_KEY_PARAMETER_NAME,
                "category");
    }

    @Override
    public boolean isDisplayStyleCloud() {
        String displayStyle = getDisplayStyle();

        return displayStyle.equals("cloud");
    }

    @Override
    public boolean isDisplayStyleList() {
        String displayStyle = getDisplayStyle();

        return displayStyle.equals("list");
    }

    @Override
    public boolean isFrequenciesVisible() {
        return portletPreferencesHelper
                .getBoolean(CustomCategoryFacetPortletPreferences.PREFERENCE_KEY_FREQUENCIES_VISIBLE, true);
    }

    private final PortletPreferencesHelper portletPreferencesHelper;

}
