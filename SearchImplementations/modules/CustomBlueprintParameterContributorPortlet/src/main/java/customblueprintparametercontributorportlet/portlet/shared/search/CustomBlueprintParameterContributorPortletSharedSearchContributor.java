package customblueprintparametercontributorportlet.portlet.shared.search;

import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchContributor;
import customblueprintparametercontributorportlet.constants.CustomBlueprintParameterContributorPortletKeys;
import customblueprintparametercontributorportlet.portlet.CustomBlueprintParameterContributorPortlet;

import org.osgi.service.component.annotations.Component;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchSettings;

import java.util.Optional;

import javax.portlet.PortletPreferences;

@Component(
        property = "javax.portlet.name=" + CustomBlueprintParameterContributorPortletKeys.CUSTOMBLUEPRINTPARAMETERCONTRIBUTOR,
        service = PortletSharedSearchContributor.class
)
public class CustomBlueprintParameterContributorPortletSharedSearchContributor implements PortletSharedSearchContributor {
    @Override
    public void contribute(
            PortletSharedSearchSettings portletSharedSearchSettings) {
    	_log.info("----------------------------------------------------------Inside contribute -------------------------------------------------------------------------------");
        Optional<PortletPreferences> portletPreferencesOptional = portletSharedSearchSettings.getPortletPreferencesOptional();

        PortletPreferences portletPreferences = portletPreferencesOptional.get();
        
        Optional<String> optionalFederatedSearchKey = Optional.ofNullable(portletPreferences.getValue("federatedSearchKey", ""));

        SearchRequestBuilder searchRequestBuilder =
                portletSharedSearchSettings.getFederatedSearchRequestBuilder(optionalFederatedSearchKey);

        if (searchRequestBuilder == null) {
        	_log.debug("Search builder is null....................................");
            return;
        }

        ThemeDisplay themeDisplay =
                portletSharedSearchSettings.getThemeDisplay();

        if(themeDisplay.getRequest().getParameter("orderBy") != null) {
            if (themeDisplay.getRequest().getParameter("orderBy").equals("newest")) {
                searchRequestBuilder.withSearchContext(
                        searchContext -> {
                            searchContext.setAttribute(
                                    "custom.orderBy", "newest");
                        }
                );
                _log.info("--------------------------OrderBy Oldest ------------------------------------------------------");
            }
            if (themeDisplay.getRequest().getParameter("orderBy").equals("oldest")) {
                searchRequestBuilder.withSearchContext(
                        searchContext -> {
                            searchContext.setAttribute(
                                    "custom.orderBy", "oldest");
                        }
                );
                _log.info("--------------------------OrderBy Newest ------------------------------------------------------");
            }
        }
    }
    private static final Log _log = LogFactoryUtil.getLog(CustomBlueprintParameterContributorPortletSharedSearchContributor.class);
}
