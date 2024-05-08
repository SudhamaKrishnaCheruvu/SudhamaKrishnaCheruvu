package at.gv.magwien.apps.search.criteria.contributor.facet.preferences;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.facet.config.FacetConfiguration;

/**
 * Class AssetEntriesFacetConfigurationImpl.
 * @author bas9004
 *
 */
public class AssetEntriesFacetConfigurationImpl implements AssetEntriesFacetConfiguration {

    public AssetEntriesFacetConfigurationImpl(FacetConfiguration facetConfiguration) {
        jsonObject = facetConfiguration.getData();
    }

    @Override
    public int getFrequencyThreshold() {
        return jsonObject.getInt("frequencyThreshold");
    }

    @Override
    public void setFrequencyThreshold(int frequencyThreshold) {
        jsonObject.put("frequencyThreshold", frequencyThreshold);
    }

    protected JSONArray toJSONArray(String... values) {
        JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

        for (String value : values) {
            jsonArray.put(value);
        }

        return jsonArray;
    }

    private final JSONObject jsonObject;

}
