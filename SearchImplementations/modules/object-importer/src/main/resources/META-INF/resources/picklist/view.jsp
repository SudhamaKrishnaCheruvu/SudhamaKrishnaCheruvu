<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%@page import="com.liferay.portal.kernel.util.HtmlUtil"%>
<%@page import="com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder"%>
<%@page import="com.liferay.list.type.model.ListTypeDefinition"%>
<%@page import="com.liferay.list.type.service.ListTypeDefinitionLocalServiceUtil"%>
<%@ include file="/init.jsp" %>


<portlet:renderURL var="importPickList">
	<portlet:param name="mvcPath" value="/picklist/import_pick_list.jsp"/>
</portlet:renderURL>

<portlet:actionURL name="deletePickList" var="deletePickListURL"></portlet:actionURL>

<aui:button href="${importPickList}" value="Import"></aui:button>

<portlet:actionURL name="exportPickList" var="exportPickListURL"></portlet:actionURL>

<aui:button href="${exportPickListURL}" value="Export All"></aui:button>

<aui:button href="${deletePickListURL}" value="Delete All"></aui:button>


<liferay-ui:search-container
		emptyResultsMessage="no-results-found"
		total="<%= ListTypeDefinitionLocalServiceUtil.getListTypeDefinitionsCount()%>"
	>
		<liferay-ui:search-container-results
			results="<%= ListTypeDefinitionLocalServiceUtil.getListTypeDefinitions( searchContainer.getStart(), searchContainer.getEnd()) %>"
		/>

		<liferay-ui:search-container-row
			className="com.liferay.list.type.model.ListTypeDefinition"
			modelVar="listTypeDefinition"
		>	
			
		<liferay-ui:search-container-column-text
				property="name"
				 cssClass="table-cell-expand table-cell-minw-200"
			/>
			

		<liferay-ui:search-container-column-jsp
			path="/picklist/action.jsp"
			 cssClass=""
		/>

		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			markupView="lexicon"
			searchContainer="<%= searchContainer %>"
		/>
	</liferay-ui:search-container>



<aui:script>
function exportList(id){
     alert(id);
     var formData = new FormData();
     formData.append('<portlet:namespace />id', id);
     Liferay.Util.fetch(
			'<liferay-portlet:resourceURL copyCurrentRenderParameters="<%= false %>" id="/pick_list/export_import_pick_list" />',
			{
				body: formData,
				method: 'POST',
			}
	 )       
}

function importList(){
 
 Liferay.Util.openModal({
				height: '350px',
				id: '<portlet:namespace />importDialog',				
				size: 'md',
				title: 'Import Pick List',
				url:
					'<portlet:renderURL windowState="<%= LiferayWindowState.POP_UP.toString() %>"><portlet:param name="mvcPath" value="/picklist/import_pick_list.jsp" /></portlet:renderURL>',
			});
}

</aui:script>