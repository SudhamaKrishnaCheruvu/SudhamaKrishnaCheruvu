package at.gv.magwien.apps.search.criteria.contributor.facet.preferences;

import at.gv.magwien.apps.search.criteria.contributor.facet.preferences.helper.PortletPreferencesHelper;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

import javax.portlet.PortletPreferences;

/**
 * Class TypeFacetPortletPreferencesImpl.
 * @author bas9004
 *
 */
public class TypeFacetPortletPreferencesImpl implements TypeFacetPortletPreferences {

    public TypeFacetPortletPreferencesImpl(Optional<PortletPreferences> portletPreferencesOptional) {

        portletPreferencesHelper = new PortletPreferencesHelper(portletPreferencesOptional);
    }

    @Override
    public Optional<String[]> getAssetTypesArray() {
        Optional<String> assetTypes = portletPreferencesHelper
                .getString(TypeFacetPortletPreferences.PREFERENCE_KEY_ASSET_TYPES);

        return assetTypes.map(StringUtil::split);
    }

    @Override
    public String getAssetTypesString() {
        return portletPreferencesHelper.getString(TypeFacetPortletPreferences.PREFERENCE_KEY_ASSET_TYPES,
                StringPool.BLANK);
    }

    @Override
    public List<KeyValuePair> getAvailableAssetTypes(long companyId, Locale locale) {

        Optional<String[]> assetTypesOptional = getAssetTypesArray();

        String[] allAssetTypes = getAllAssetTypes(companyId);

        String[] assetTypes = assetTypesOptional.orElse(allAssetTypes);

        List<KeyValuePair> availableAssetTypes = new ArrayList<>();

        for (String className : allAssetTypes) {
            if (!ArrayUtil.contains(assetTypes, className)) {
                availableAssetTypes.add(getKeyValuePair(locale, className));
            }
        }

        return availableAssetTypes;
    }

    @Override
    public List<KeyValuePair> getCurrentAssetTypes(long companyId, Locale locale) {

        String[] assetTypes = getCurrentAssetTypesArray(companyId);

        List<KeyValuePair> currentAssetTypes = new ArrayList<>();

        for (String className : assetTypes) {
            currentAssetTypes.add(getKeyValuePair(locale, className));
        }

        return currentAssetTypes;
    }

    @Override
    public String[] getCurrentAssetTypesArray(long companyId) {
        Optional<String[]> assetTypesOptional = getAssetTypesArray();

        String[] allAssetTypes = getAllAssetTypes(companyId);

        return assetTypesOptional.orElse(allAssetTypes);
    }

    @Override
    public int getFrequencyThreshold() {
        return portletPreferencesHelper.getInteger(TypeFacetPortletPreferences.PREFERENCE_KEY_FREQUENCY_THRESHOLD, 1);
    }

    @Override
    public String getParameterName() {
        return portletPreferencesHelper.getString(TypeFacetPortletPreferences.PREFERENCE_KEY_PARAMETER_NAME, "type");
    }

    @Override
    public boolean isFrequenciesVisible() {
        return portletPreferencesHelper.getBoolean(TypeFacetPortletPreferences.PREFERENCE_KEY_FREQUENCIES_VISIBLE,
                true);
    }

    protected String[] getAllAssetTypes(long companyId) {
        List<AssetRendererFactory<?>> assetRendererFactories = AssetRendererFactoryRegistryUtil
                .getAssetRendererFactories(companyId);

        Stream<AssetRendererFactory<?>> assetRendererFactoriesStream = assetRendererFactories.stream();

        return assetRendererFactoriesStream.filter(AssetRendererFactory::isSearchable)
                .map(AssetRendererFactory::getClassName).toArray(String[]::new);
    }

    protected KeyValuePair getKeyValuePair(Locale locale, String className) {
        return new KeyValuePair(className, ResourceActionsUtil.getModelResource(locale, className));
    }

    private final PortletPreferencesHelper portletPreferencesHelper;

}
