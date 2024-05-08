<%@page import="com.liferay.portal.kernel.util.GetterUtil"%>
<%@page import="com.liferay.document.library.kernel.service.DLAppServiceUtil"%>
<%@page import="com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker"%>
<%@page import="com.liferay.portal.kernel.util.ParamUtil"%>
<%@page
	import="com.liferay.document.library.kernel.exception.NoSuchFileEntryException"%>
<%@page
	import="com.liferay.document.library.kernel.exception.NoSuchFolderException"%>
<%@ include file="init.jsp"%>

<portlet:actionURL name="importObject" var="importObjectURL"></portlet:actionURL>
<portlet:renderURL var="folderURL"></portlet:renderURL>

<portlet:renderURL var="importObject">
	<portlet:param name="mvcPath" value="/import.jsp"/>
</portlet:renderURL>

<portlet:actionURL name="deleteObject" var="deleteObjectURL"></portlet:actionURL>
<portlet:actionURL name="publishObject" var="publishObjectURL"></portlet:actionURL>
<portlet:actionURL name="activePublishObject" var="activeObjectURL"></portlet:actionURL>
<portlet:actionURL name="exportPublishObject" var="exportObjectURL"></portlet:actionURL>
<%
long folderId = GetterUtil.getLong(request.getAttribute("folderId"), -1);

%>

<aui:button href="${importObject}" value="Import"></aui:button>
<aui:button href="${exportObjectURL}" value="Export All"></aui:button>
<c:if test="${showActions}">
	<aui:button href="${deleteObjectURL}" value="Delete"></aui:button>
	<aui:button href="${publishObjectURL}" value="Publish"></aui:button>
	<aui:button href="${activeObjectURL}" value="Active"></aui:button>
</c:if>


<aui:form name="fm" action="<%=importObjectURL%>"
	onSubmit='<%="event.preventDefault(); " + liferayPortletResponse.getNamespace() + "importObject(event);"%>'>

	<liferay-ui:error exception="<%=NoSuchFolderException.class %>"
		message="Folder does not exist"></liferay-ui:error>
	<liferay-ui:error exception="<%=NoSuchFileEntryException.class %>"
		message="Object Definition File does not exist"></liferay-ui:error>

	<aui:select name="folderId" label="Select Folder" onChange="changeFolder(event)">
		<c:forEach var="folder" items="${folders}">
			<option value="${folder.folderId}" ${folderId == folder.folderId ? 'selected' : '' }>${folder.name}</option>
		</c:forEach>
	</aui:select>

<c:if test="${show}">

		<liferay-ui:search-container emptyResultsMessage="no-results-found"  delta="200" 
		total="<%= DLAppServiceUtil.getFileEntriesCount(themeDisplay.getScopeGroupId(), folderId) %>"
		rowChecker="<%=  new EmptyOnClickRowChecker(renderResponse) %>">
		<liferay-ui:search-container-results results="<%= DLAppServiceUtil.getFileEntries(themeDisplay.getScopeGroupId(), folderId, searchContainer.getStart(), searchContainer.getEnd()) %>" />

		<liferay-ui:search-container-row
			className="com.liferay.portal.kernel.repository.model.FileEntry"
			modelVar="fileEntry" keyProperty="fileEntryId">

			<liferay-ui:search-container-column-text property="title"
				cssClass="table-cell-expand table-cell-minw-200" />

		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator  
			searchContainer="<%=searchContainer%>" />
	    </liferay-ui:search-container>

</c:if>

	<aui:button type="submit" value="Import Selected"></aui:button>
</aui:form>


<aui:script>

function changeFolder(e){
   console.log('value......',e.currentTarget.value)
   let url = Liferay.Util.addParams('<portlet:namespace />folderId=' + e.currentTarget.value, '${folderURL}');
   Liferay.Util.navigate(url);
}

function <portlet:namespace />importObject(event) {

   var rowsChecked = Array.from(
			document.querySelectorAll(
				'input[name=<portlet:namespace />rowIds]:checked'
			)
		);
   let folderName = document.getElementById('<portlet:namespace />folderId');
   
   let message = 'Are you sure you want to Add or Update selected Object Defnitions';
   
   console.log('rowsChecked........',rowsChecked)
   
   if(rowsChecked.length > 0){
           Liferay.Util.openConfirmModal({
					message: message,
					onConfirm: (isConfirmed) => {
						if (isConfirmed) {
							submitForm(document.<portlet:namespace />fm);
						}
					},
	       });
   }
   

}

</aui:script>