package groovyscript.check.portlet;

import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;

import groovyscript.check.constants.GroovyscriptCheckPortletKeys;

/**
 * @author SudhamaKrishnaC
 */
@Component(immediate = true, property = { "com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.header-portlet-css=/css/main.css", "com.liferay.portlet.instanceable=true",
		"javax.portlet.display-name=GroovyscriptCheck", "javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + GroovyscriptCheckPortletKeys.GROOVYSCRIPTCHECK,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user" }, service = Portlet.class)
public class GroovyscriptCheckPortlet extends MVCPortlet {

	@Override
	public void render(RenderRequest renderRequest, RenderResponse renderResponse)
			throws IOException, PortletException {
		{
			List<DDMStructure> ddmStructures = DDMStructureLocalServiceUtil.getDDMStructures(QueryUtil.ALL_POS,
					QueryUtil.ALL_POS);

			for (DDMStructure ddmStructure : ddmStructures) {

				System.out.println(ddmStructure.getStructureId() + "@@" + ddmStructure.getStructureKey() + "@@"
						+ ddmStructure.getName(Locale.US) + "@@" + ddmStructure.getGroupId() + "@@"
						+ ddmStructure.getType());

			}
		}
	}
}