
package at.gv.magwien.apps.search.criteria.contributor.web.portlet;

import at.gv.magwien.apps.search.criteria.contributor.web.constants.SearchCriteriaContributorWebPortletKeys;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.service.permission.PortletPermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.IOException;
import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/*
 * @author bas9004
 */
@Component(immediate = true, property = { "com.liferay.portlet.display-category=category.search",
        "com.liferay.portlet.instanceable=false", "javax.portlet.init-param.template-path=/",
        "javax.portlet.init-param.view-template=/view.jsp",
        "javax.portlet.name=" + SearchCriteriaContributorWebPortletKeys.SEARCH_CRITERIA_CONTRIBUTORWEB,
        "javax.portlet.resource-bundle=content.Language",
        "javax.portlet.security-role-ref=power-user,user" }, service = Portlet.class)
public class SearchCriteriaContributorWebPortlet extends MVCPortlet {

    private static Log log = LogFactoryUtil.getLog(SearchCriteriaContributorWebPortlet.class);

    @Override
    public void doView(RenderRequest renderRequest, RenderResponse renderResponse)
            throws IOException, PortletException {

        ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY);
        try {
            if (!portletPermission.contains(themeDisplay.getPermissionChecker(), themeDisplay.getPlid(),
                    portal.getPortletId(renderRequest), "CONFIGURATION")) {

                renderRequest.setAttribute(WebKeys.PORTLET_CONFIGURATOR_VISIBILITY, Boolean.TRUE);
            }
            super.doView(renderRequest, renderResponse);
        } catch (PortalException e) {
            log.error("Portal Exception occured " + e);
        }
    }

    @Reference
    protected Portal portal;

    @Reference
    protected PortletPermission portletPermission;
}
