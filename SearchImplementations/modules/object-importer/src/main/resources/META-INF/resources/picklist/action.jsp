<%@page import="com.liferay.list.type.model.ListTypeDefinition"%>
<%@page import="com.liferay.portal.kernel.util.WebKeys"%>
<%@page import="com.liferay.portal.kernel.dao.search.ResultRow"%>
<%@include file="/init.jsp" %>

<%
ResultRow row = (ResultRow)request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
ListTypeDefinition listTypeDefinition = (ListTypeDefinition)row.getObject();
%>

<portlet:resourceURL id="/pick_list/export_import_pick_list" var="listExportURL">
	<portlet:param name="id" value="<%= String.valueOf(listTypeDefinition.getListTypeDefinitionId()) %>" />
</portlet:resourceURL>

<liferay-ui:icon url="<%=listExportURL %>" icon="download" markupView="lexicon" message="" />