<%@ include file="/init.jsp"%>

<liferay-portlet:actionURL portletConfiguration="<%=true%>" var="configurationActionURL" />

<liferay-portlet:renderURL portletConfiguration="<%=true%>" var="configurationRenderURL" />

<aui:container fluid="false">
	<aui:row cssClass="mt-5">
		<aui:form action="<%=configurationActionURL%>" method="post" name="fm">
			<aui:input name="<%=Constants.CMD%>" type="hidden" value="<%=Constants.UPDATE%>" />
			<aui:input name="redirect" type="hidden" value="<%=configurationRenderURL%>" />

			<aui:fieldset>
				<aui:input type="text"
					name="<%=CustomSearchSuggestionConstants.PREFERENCES_BLUEPRINT_ID%>"
					label="blueprint-id" value="<%=blueprintId%>"></aui:input>
			</aui:fieldset>

			<aui:button-row>
				<aui:button type="submit"></aui:button>
			</aui:button-row>
		</aui:form>
	</aui:row>
</aui:container>