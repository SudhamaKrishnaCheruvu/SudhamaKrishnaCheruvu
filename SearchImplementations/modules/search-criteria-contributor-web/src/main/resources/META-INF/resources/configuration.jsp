<%@page import="com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil"%>
<%@page import="com.liferay.portal.kernel.util.StringUtil"%>
<%@ include file="init.jsp" %>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%> 

<liferay-portlet:actionURL portletConfiguration="<%= true %>"
    var="configurationActionURL" />

<liferay-portlet:renderURL portletConfiguration="<%= true %>"
    var="configurationRenderURL" />
    
    
    <liferay-ui:icon-delete
			url="<%= configurationActionURL %>"
		/>

<div id="searchCriteriaContributorWebDiv" class="searchCriteriaContributorWebDiv">
<aui:form name="searchConfigurationFm" method="post" action="<%= configurationActionURL %>" id="configurationForm">


<!--  Joural Folder selection -->

 
 <liferay-ui:panel collapsible="<%= true %>" defaultState="open" title="journal-folder" >
 <aui:fieldset cssClass="source-container" label="" collapsed="<%= false %>" collapsible="<%= true %>" >

 
<div>
<c:choose>
<c:when test="<%= journalTypeInclude.equalsIgnoreCase("include") %>">
<aui:input name='journalTypeInclude' type="radio" label=""  value="include" checked="true"></aui:input>
 <label for="journalTypeInclude"><liferay-ui:message key='include'/></label>


<aui:input name='journalTypeInclude' type="radio" label=""  value="exclude" />
 <label for="journalTypeInclude"><liferay-ui:message key='exclude'/></label>
</c:when>
<c:otherwise>
<aui:input name='journalTypeInclude' type="radio" label=""  value="include" ></aui:input>
 <label for="journalTypeInclude"><liferay-ui:message key='include'/></label>


<aui:input name='journalTypeInclude' type="radio" label=""  value="exclude" checked="true" /><label for="journalTypeExclude"><liferay-ui:message key='exclude'/></label>
</c:otherwise>
</c:choose>
</div>	

<aui:input name="searchCriteriaSelectedJournalFolderText" type="hidden" />

<liferay-ui:input-move-boxes
	leftBoxName="searchCriteriaSelectedJournalFolder"
	leftList="<%= leftJournalFolderList %>"
	leftReorder="<%= Boolean.TRUE.toString() %>"
	leftTitle="current"
	rightBoxName="availableJournalFolders"
	rightList="<%= rightJournalFolderList %>"
	rightTitle="available"
/>

 </aui:fieldset> 
</liferay-ui:panel>
 <!--  Document Folder selection -->
  <liferay-ui:panel collapsible="<%= true %>" defaultState="open" title="document-folder" >
 <aui:fieldset cssClass="source-container" label="document-folder" collapsed="<%= false %>" collapsible="<%= true %>">
 
 <div>


 <c:choose>
<c:when test="<%= documentTypeInclude.equalsIgnoreCase("include") %>">
<aui:input name='documentTypeInclude' type="radio" label=""  value="include" checked="true" ></aui:input>
 <label for="documentTypeInclude"><liferay-ui:message key='include'/></label>


<aui:input name='documentTypeInclude' type="radio" label=""  value="exclude" />
 <label for="documentTypeInclude"><liferay-ui:message key='exclude'/></label>
</c:when>
<c:otherwise>
<aui:input name='documentTypeInclude' type="radio" label=""  value="include"  ></aui:input>
 <label for="documentTypeInclude"><liferay-ui:message key='include'/></label>


<aui:input name='documentTypeInclude' type="radio" label=""  value="exclude" checked="true" />
 <label for="documentTypeInclude"><liferay-ui:message key='exclude'/></label>
</c:otherwise>
</c:choose>
 
 
</div>	

<aui:input name="searchCriteriaSelectedDocumentFolderText" type="hidden" />

<liferay-ui:input-move-boxes
	leftBoxName="searchCriteriaSelectedDocumentFolder"
	leftList="<%= leftDocumentFolderList %>"
	leftReorder="<%= Boolean.TRUE.toString() %>"
	leftTitle="current"
	rightBoxName="availableDLFolders"
	rightList="<%= rightDocumentFolderList %>"
	rightTitle="available"
/>

 </aui:fieldset> 
 
 </liferay-ui:panel>
 
<!--  Asset Type selection -->
  <liferay-ui:panel collapsible="<%= true %>" defaultState="open" title="asset-types-and-structures" >
 <aui:fieldset cssClass="source-container" label="asset-types-and-structures" collapsed="<%= false %>" collapsible="<%= true %>">
 <h3><liferay-ui:message key='assets'/></h3>
  <div>

  <c:choose>
<c:when test="<%= assetTypeInclude.equalsIgnoreCase("include") %>">
<aui:input name='assetTypeInclude' type="radio" label=""  value="include" checked="true"></aui:input>
 <label for="assetTypeInclude"><liferay-ui:message key='include'/></label>


<aui:input name='assetTypeInclude' type="radio" label=""  value="exclude" />
 <label for="assetTypeInclude"><liferay-ui:message key='exclude'/></label>
</c:when>
<c:otherwise>
<aui:input name='assetTypeInclude' type="radio" label=""  value="include"></aui:input>
 <label for="assetTypeInclude"><liferay-ui:message key='include'/></label>


<aui:input name='assetTypeInclude' type="radio" label=""  value="exclude" checked="true" />
 <label for="assetTypeInclude"><liferay-ui:message key='exclude'/></label>
</c:otherwise>
</c:choose>
 
 
 
</div>	
<aui:input name="preferencesAssetTypes" type="hidden" />
<aui:input name="searchCriteriaSelectedAssetTypesText" type="hidden" />

 <liferay-ui:input-move-boxes
	leftBoxName="searchCriteriaSelectedAssetTypes"
	leftList="<%= leftAssetTypeList %>"
	leftReorder="<%= Boolean.TRUE.toString() %>"
	leftTitle="current"
	rightBoxName="availableAssets"
	rightList="<%= rightAssetTypeList %>"
	rightTitle="available"
/>
 

 <!-- Structure selection -->
 
 
 <h3><liferay-ui:message key='structures'/></h3>
 <div>

   <c:choose>
<c:when test="<%= structureInclude.equalsIgnoreCase("include") %>">
<aui:input name='structureInclude' type="radio" label=""  value="include" checked="true"></aui:input>
 <label for="structureInclude"><liferay-ui:message key='include'/></label>


<aui:input name='structureInclude' type="radio" label=""  value="exclude" />
 <label for="structureInclude"><liferay-ui:message key='exclude'/></label>
</c:when>
<c:otherwise>
<aui:input name='structureInclude' type="radio" label=""  value="include"></aui:input>
 <label for="structureInclude"><liferay-ui:message key='include'/></label>


<aui:input name='structureInclude' type="radio" label=""  value="exclude" checked="true" />
 <label for="structureInclude"><liferay-ui:message key='exclude'/></label>
</c:otherwise>
</c:choose>
 
 
</div>	
<aui:input name="searchCriteriaSelectedStructuresText" type="hidden" />
<liferay-ui:input-move-boxes
	leftBoxName="searchCriteriaSelectedStructures"
	leftList="<%= leftStructureList %>"
	leftReorder="<%= Boolean.TRUE.toString() %>"
	leftTitle="current"
	rightBoxName="availableStructures"
	rightList="<%= rightStructureList %>"
	rightTitle="available"
/>
</aui:fieldset>
 </liferay-ui:panel>
 <liferay-ui:panel collapsible="<%= true %>" defaultState="open" title="asset-categories" >
 <aui:fieldset cssClass="source-container" label="" collapsed="<%= false %>" collapsible="<%= true %>">
 

<%
Map<String, String> vocabMap = SearchCriteriaConfigurationUtil.getAssetVocabularies(groupId);
List<String> vocabList = new ArrayList(vocabMap.keySet());
%> 
<c:set var="vocabularyList" value="<%= vocabList %>" /> 

<c:set var="vocabularyId" value="${vocabulary.getKey()}" />
<%
String[] defaultVal = {};
String[] searchCriteriaSelectedCategories = portletPreferences.getValues("searchCriteriaSelectedCategories" ,defaultVal );
%>

<aui:fieldset  label="" collapsed="<%= false %>" collapsible="<%= true %>">
<div class="half-column-left">
<liferay-asset:asset-categories-selector categoryIds="<%=includeCategories %>" hiddenInput="inlcudeCategorySelectorText"/>
<aui:input  type="hidden"  id="inlcudeCategorySelectorText" name="inlcudeCategorySelectorText" />
</div>
<div class="half-column-right">
<aui:input name="categoriesInclude" value='<%= LanguageUtil.get(locale, "include") %>' disabled="true" label="" />
<aui:select name="categoriesAll" label="" value="<%=categoriesAll %>">
<aui:option value="all"><liferay-ui:message key="all"></liferay-ui:message></aui:option>
<aui:option value="any"><liferay-ui:message key="any"></liferay-ui:message></aui:option>
</aui:select>
</div>
</aui:fieldset>

<aui:fieldset  label="" collapsed="<%= false %>" collapsible="<%= true %>">
<div class="half-column-left">
<liferay-asset:asset-categories-selector categoryIds="<%=excludeCategories %>" hiddenInput="excludeCategorySelectorText"/>
<aui:input  type="hidden"  id="excludeCategorySelectorText" name="excludeCategorySelectorText" />
</div>
<div class="half-column-right">
<aui:input name="categoriesExclude" value='<%= LanguageUtil.get(locale, "exclude") %>' disabled="true" label="" />
<aui:select name="categoriesExcludeAll" label="" value="<%=categoriesExcludeAll %>">
<aui:option value="all"><liferay-ui:message key="all"></liferay-ui:message></aui:option>
<aui:option value="any"><liferay-ui:message key="any"></liferay-ui:message></aui:option>
</aui:select>
</div>
</aui:fieldset>

</aui:fieldset>
</liferay-ui:panel>

 <liferay-ui:panel collapsible="<%= true %>" defaultState="open" title="asset-Tags" >
 <aui:fieldset cssClass="source-container" label="" collapsed="<%= false %>" collapsible="<%= true %>">

<aui:fieldset  label="" collapsed="<%= false %>" collapsible="<%= true %>">
<div class="half-column-left">
<liferay-asset:asset-tags-selector allowAddEntry="false" id="inlcudeTagSelector" tagNames="<%=currentSelectedTags %>" hiddenInput="inlcudeTagSelectorText" />

<aui:input  type="hidden"  id="inlcudeTagSelectorText" name="inlcudeTagSelectorText" />
</div>
<div class="half-column-right">

<aui:input name="tagsInclude" value='<%= LanguageUtil.get(locale, "include") %>' disabled="true" label="" />

<aui:select name="tagsIncludeAll" label="" value="<%= tagsAll %>">
<aui:option value="all"><liferay-ui:message key="all"></liferay-ui:message></aui:option>
<aui:option value="any"><liferay-ui:message key="any"></liferay-ui:message></aui:option>
</aui:select>
</div>
</aui:fieldset>
<!-- Exclude Tag -->
<aui:fieldset  label="" collapsed="<%= false %>" collapsible="<%= true %>">
<div class="half-column-left">
<liferay-asset:asset-tags-selector allowAddEntry="false" id="exlcudeTagSelector" tagNames="<%=currentSelectedTagsExclude %>" hiddenInput="exlcudeTagSelectorText" />

<aui:input type="hidden"  id="exlcudeTagSelectorText" name="exlcudeTagSelectorText"/>

</div>
<div class="half-column-right">

<aui:input name="include" value='<%= LanguageUtil.get(locale, "exclude") %>'  disabled="true" label=""></aui:input>

<aui:select name="tagsExcludeAll" label="" value="<%=tagsExcludeAll %>">
<aui:option value="all"><liferay-ui:message key="all"></liferay-ui:message></aui:option>
<aui:option value="any"><liferay-ui:message key="any"></liferay-ui:message></aui:option>
</aui:select>
</div>
</aui:fieldset>

</aui:fieldset>
</liferay-ui:panel>
<aui:fieldset>
 
 <aui:button type="submit" value="save" />

</aui:fieldset>
</aui:form>
 
 </div>

<aui:script use="liferay-util-list-fields">
A.one('#<portlet:namespace/>searchConfigurationFm').on('submit', function(event) {
	var vocabularyList=[];
	vocabularyList = ${vocabularyList};
	var searchCriteriaSelectedStructuresValues = Liferay.Util.listSelect('#<portlet:namespace/>searchCriteriaSelectedStructures');
    var searchCriteriaSelectedJournalFolderValues = Liferay.Util.listSelect('#<portlet:namespace/>searchCriteriaSelectedJournalFolder');
    var searchCriteriaSelectedDocumentFolderValues = Liferay.Util.listSelect('#<portlet:namespace/>searchCriteriaSelectedDocumentFolder');
    var searchCriteriaSelectedAssetTypesValues = Liferay.Util.listSelect('#<portlet:namespace/>searchCriteriaSelectedAssetTypes');
    A.one('#<portlet:namespace/>searchCriteriaSelectedStructuresText').val(searchCriteriaSelectedStructuresValues);
    A.one('#<portlet:namespace/>searchCriteriaSelectedJournalFolderText').val(searchCriteriaSelectedJournalFolderValues);
    A.one('#<portlet:namespace/>searchCriteriaSelectedDocumentFolderText').val(searchCriteriaSelectedDocumentFolderValues);
    A.one('#<portlet:namespace/>searchCriteriaSelectedAssetTypesText').val(searchCriteriaSelectedAssetTypesValues);
	for (var i = 0; i < vocabularyList.length; i++) {
		var searchCategory= '#<portlet:namespace/>searchCriteriaSelectedCategories'.concat(vocabularyList[i]);
		var searchCategoryText= '#<portlet:namespace/>searchCriteriaSelectedCategoriesText'.concat(vocabularyList[i]);
		if($(searchCategory).length!=0){
			var searchCategoryValues = Liferay.Util.listSelect(searchCategory);
			A.one(searchCategoryText).val(searchCategoryValues);
		}		
	}
   
    submitForm('#<portlet:namespace/>searchConfigurationFm');
});
</aui:script>