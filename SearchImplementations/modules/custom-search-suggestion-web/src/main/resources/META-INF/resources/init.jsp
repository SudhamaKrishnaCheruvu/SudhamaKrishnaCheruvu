<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.custom.search.suggestion.conf.CustomSearchSuggestionConfiguration" %>
<%@ page import="com.custom.search.suggestion.constants.CustomSearchSuggestionConstants" %>
<%@ page import="com.liferay.petra.string.StringPool" %>
<%@ page import="com.liferay.portal.kernel.util.Constants" %>
<%@ page import="com.liferay.portal.kernel.util.GetterUtil" %>
<%@ page import="com.liferay.portal.kernel.util.Validator" %>

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
	CustomSearchSuggestionConfiguration customSearchSuggestionConfiguration = (CustomSearchSuggestionConfiguration) renderRequest
			.getAttribute(CustomSearchSuggestionConfiguration.class.getName());

	String blueprintId = StringPool.BLANK;
	if (Validator.isNotNull(customSearchSuggestionConfiguration)) {
		blueprintId = portletPreferences.getValue(CustomSearchSuggestionConstants.PREFERENCES_BLUEPRINT_ID,
		customSearchSuggestionConfiguration.blueprintId());
	}
%>