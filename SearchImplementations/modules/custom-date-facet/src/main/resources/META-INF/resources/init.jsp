<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@page import="com.liferay.custom.date.facet.portlet.CustomDateFacetConfiguration"%>
<%@page import="com.liferay.petra.string.StringPool"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<liferay-theme:defineObjects />

<portlet:defineObjects />


<%
CustomDateFacetConfiguration messageDisplayConfiguration =
		(CustomDateFacetConfiguration)
		renderRequest.getAttribute(CustomDateFacetConfiguration.class.getName());

	String customFieldName = StringPool.BLANK;

	if (Validator.isNotNull(messageDisplayConfiguration)) {
		customFieldName = portletPreferences.getValue("customFieldName", messageDisplayConfiguration.customFieldName());

	}
%>
<script type="text/javascript">
var fieldName = '<%=customFieldName %>';
</script>
