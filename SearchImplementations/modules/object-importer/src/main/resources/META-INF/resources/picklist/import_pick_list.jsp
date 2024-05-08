<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%@ include file="/init.jsp" %>

<portlet:actionURL name="/pick_list/export_import_pick_list" var="importURL"></portlet:actionURL>

<aui:form cssClass="modal-body" action="<%=importURL %>" enctype="multipart/form-data">

<liferay-ui:error key="error"
		message="A survey with this name already exist please try with new name" />
<aui:input name="redirect" value="<%=themeDisplay.getLayoutFriendlyURL(themeDisplay.getLayout()) %>" type="hidden"></aui:input>
<aui:field-wrapper>
	<aui:input name="pickListDefinitionJSON" type="file" label="JSON File" multiple="true" required="true"></aui:input>
</aui:field-wrapper>


<aui:button-row cssClass="position-fixed">
    
	<aui:button type="submit" value="import"></aui:button>
	<aui:button type="cancel" href="<%=themeDisplay.getLayoutFriendlyURL(themeDisplay.getLayout()) %>"/>
</aui:button-row>
</aui:form>