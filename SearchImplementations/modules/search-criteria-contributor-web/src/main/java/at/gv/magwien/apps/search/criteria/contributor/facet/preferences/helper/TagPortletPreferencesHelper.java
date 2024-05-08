package at.gv.magwien.apps.search.criteria.contributor.facet.preferences.helper;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Optional;

import javax.portlet.PortletPreferences;

/**
 * Class TagPortletPreferencesHelper.
 * @author bas9004
 *
 */
public class TagPortletPreferencesHelper {
    public TagPortletPreferencesHelper(Optional<PortletPreferences> portletPreferencesOptional) {

        this.portletPreferencesOptional = portletPreferencesOptional;
    }

    public Optional<Boolean> getBoolean(String key) {
        Optional<String> value = getValue(key);
        return value.map(GetterUtil::getBoolean);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        Optional<Boolean> value = getBoolean(key);
        return value.orElse(defaultValue);
    }

    public Optional<Integer> getInteger(String key) {
        Optional<String> value = getValue(key);
        return value.map(GetterUtil::getInteger);
    }

    public int getInteger(String key, int defaultValue) {
        Optional<Integer> value = getInteger(key);
        return value.orElse(defaultValue);
    }

    public Optional<String> getString(String key) {
        return getValue(key);
    }

    public String getString(String key, String defaultValue) {
        Optional<String> value = getString(key);
        return value.orElse(defaultValue);
    }

    protected Optional<String> getValue(String key) {
        return portletPreferencesOptional
                .flatMap(portletPreferences -> maybe(portletPreferences.getValue(key, StringPool.BLANK)));
    }

    protected Optional<String> maybe(String s) {
        s = StringUtil.trim(s);

        if (Validator.isBlank(s)) {
            return Optional.empty();
        }

        return Optional.of(s);
    }

    private final Optional<PortletPreferences> portletPreferencesOptional;
}
