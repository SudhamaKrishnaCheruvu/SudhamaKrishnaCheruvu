package at.gv.magwien.apps.search.criteria.contributor.facet.preferences;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.facet.config.FacetConfiguration;

/**
 * Class CustomAssetTagsFacetConfigurationImpl.
 * @author bas9004
 *
 */
public class CustomAssetTagsFacetConfigurationImpl implements CustomAssetTagsFacetConfiguration {

    public CustomAssetTagsFacetConfigurationImpl(FacetConfiguration facetConfiguration) {

        jsonObject = facetConfiguration.getData();
    }

    @Override
    public int getFrequencyThreshold() {
        return jsonObject.getInt("frequencyThreshold");
    }

    @Override
    public int getMaxTerms() {
        return jsonObject.getInt("maxTerms");
    }

    @Override
    public void setFrequencyThreshold(int frequencyThreshold) {
        jsonObject.put("frequencyThreshold", frequencyThreshold);
    }

    @Override
    public void setMaxTerms(int maxTerms) {
        jsonObject.put("maxTerms", maxTerms);
    }

    private final JSONObject jsonObject;

}
