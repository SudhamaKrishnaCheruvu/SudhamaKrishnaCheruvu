<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@page import="at.gv.magwien.apps.search.criteria.configuration.SearchCriteriaConfig"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/asset" prefix="liferay-asset" %><%@
taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/comment" prefix="liferay-comment" %><%@
taglib uri="http://liferay.com/tld/ddm" prefix="liferay-ddm" %><%@
taglib uri="http://liferay.com/tld/flags" prefix="liferay-flags" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/site" prefix="liferay-site" %><%@
taglib uri="http://liferay.com/tld/soy" prefix="soy" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>



<%@page import="com.liferay.portal.kernel.util.WebKeys"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="com.liferay.portal.kernel.theme.ThemeDisplay"%>
<%@page import="java.util.Map"%>
<%@page import="at.gv.magwien.apps.search.criteria.util.SearchCriteriaConfigurationUtil"%>
<%@page import="com.liferay.portal.kernel.util.KeyValuePairComparator"%>
<%@page import="com.liferay.portal.kernel.util.ListUtil"%>
<%@page import="java.util.Arrays"%>
<%@page import="com.liferay.portal.kernel.util.KeyValuePair"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="com.liferay.portal.kernel.util.StringPool"%>
<%@page import="com.liferay.portal.kernel.language.LanguageUtil"%>
<%@page import="com.liferay.asset.kernel.model.AssetVocabulary"%>
<%@page import="com.liferay.portal.kernel.model.ClassName"%>
<%@page import="com.liferay.portal.kernel.util.PortalUtil"%>
<%@page import="com.liferay.portal.kernel.service.ClassNameLocalServiceUtil"%>
<%@page import="com.liferay.portal.kernel.security.permission.ResourceActionsUtil"%>


<liferay-frontend:defineObjects />
<liferay-theme:defineObjects />
<portlet:defineObjects />

<%
SearchCriteriaConfig exampleConfiguration = (SearchCriteriaConfig)renderRequest.getAttribute(SearchCriteriaConfig.class.getName());

 String journalTypeInclude = StringPool.BLANK;
 String documentTypeInclude = StringPool.BLANK;
 String assetTypeInclude = StringPool.BLANK;
 String structureInclude = StringPool.BLANK;
 long groupId= themeDisplay.getScopeGroupId();  
 
 String categoriesInclude = StringPool.BLANK;
 String categoriesAll = StringPool.BLANK;
 String includeCategories = StringPool.BLANK;
 
 String categoriesExclude = StringPool.BLANK;
 String categoriesExcludeAll = StringPool.BLANK;
 String excludeCategories = StringPool.BLANK;
 
 String tagsInclude = StringPool.BLANK;
 String tagsAll = StringPool.BLANK;
 String currentSelectedTags = StringPool.BLANK;
 
 String tagsExclude = StringPool.BLANK;
 String tagsExcludeAll = StringPool.BLANK;
 String currentSelectedTagsExclude = StringPool.BLANK;
    
   Map<String,String> journalFolders = SearchCriteriaConfigurationUtil.getJournalFolders(themeDisplay.getScopeGroupId());
  
   List leftJournalFolderList = new ArrayList();
   List journalFolderMetadataFields = new ArrayList();
   List rightJournalFolderList = new ArrayList();
   
   
   Map<String,String> documentFolders = SearchCriteriaConfigurationUtil.getDLFolders(themeDisplay.getScopeGroupId());

   List leftDocumentFolderList = new ArrayList();
   List documentFolderMetadataFields = new ArrayList();
   List rightDocumentFolderList = new ArrayList();
   
   
   Map<String,String> availableClassNameIdsSet = SearchCriteriaConfigurationUtil.getClassNameIds(themeDisplay.getCompanyId(),themeDisplay.getLocale());

   List leftAssetTypeList = new ArrayList();
   List assetTypeMetadataFields = new ArrayList();
   List rightAssetTypeList = new ArrayList();

   
   Map<String,String> availableStructureSet = SearchCriteriaConfigurationUtil.getStructures(themeDisplay.getScopeGroupId(),themeDisplay.getCompanyId(),themeDisplay.getLocale());
   List leftStructureList = new ArrayList();
   List structureMetadataFields = new ArrayList();
   List rightStructureList = new ArrayList();
   
   Map<String,String> availableVocabulariesList = SearchCriteriaConfigurationUtil.getAssetVocabularies(themeDisplay.getScopeGroupId());
   
   
   
   if (Validator.isNotNull(exampleConfiguration)) {
     journalTypeInclude =
           portletPreferences.getValue(
               "journalTypeInclude", exampleConfiguration.journalTypeInclude());
     if(journalTypeInclude == null){
        journalTypeInclude = "include";
     }
	// get already selected journal folder Ids
     String[] searchCriteriaSelectedJournalFolderIds =
            portletPreferences.getValues(
                "searchCriteriaSelectedJournalFolder", exampleConfiguration.searchCriteriaSelectedJournalFolder());
     
     
    
     if(searchCriteriaSelectedJournalFolderIds != null && searchCriteriaSelectedJournalFolderIds.length >0){
     for(String id : searchCriteriaSelectedJournalFolderIds){
       journalFolderMetadataFields.add(journalFolders.get(id));
       leftJournalFolderList.add(new KeyValuePair(id, journalFolders.get(id)));
       
     }
     }
     
     for (Map.Entry<String,String>  folderColumn : journalFolders.entrySet()) {
   	if (Arrays.binarySearch(journalFolderMetadataFields.toArray(), folderColumn.getValue()) < 0) {
   		rightJournalFolderList.add(new KeyValuePair(folderColumn.getKey(), folderColumn.getValue()));
   	}
   }
     rightJournalFolderList = ListUtil.sort(rightJournalFolderList, new KeyValuePairComparator(false, true));
     
     
     // Docuement Folder related
     
     documentTypeInclude =
         portletPreferences.getValue(
             "documentTypeInclude", exampleConfiguration.documentTypeInclude());
 
     if(documentTypeInclude == null){
       documentTypeInclude = "include";
    }


	// get already selected document folder Ids
  String[] searchCriteriaSelectedDocumentFolderIds =
         portletPreferences.getValues(
             "searchCriteriaSelectedDocumentFolder", exampleConfiguration.searchCriteriaSelectedDocumentFolder());
  
  
 
  if(searchCriteriaSelectedDocumentFolderIds != null && searchCriteriaSelectedDocumentFolderIds.length >0){
  for(String id : searchCriteriaSelectedDocumentFolderIds){
    documentFolderMetadataFields.add(documentFolders.get(id));
    leftDocumentFolderList.add(new KeyValuePair(id, documentFolders.get(id)));
    
  }
  }
  
  for (Map.Entry<String,String>  folderColumn : documentFolders.entrySet()) {
	if (Arrays.binarySearch(documentFolderMetadataFields.toArray(), folderColumn.getValue()) < 0) {
		rightDocumentFolderList.add(new KeyValuePair(folderColumn.getKey(), folderColumn.getValue()));
	}
}
  rightDocumentFolderList = ListUtil.sort(rightDocumentFolderList, new KeyValuePairComparator(false, true));
     
    
//Asset Type selection related
  
  assetTypeInclude =
      portletPreferences.getValue(
          "assetTypeInclude", exampleConfiguration.assetTypeInclude());

  if(assetTypeInclude == null){
    assetTypeInclude = "include";
 }


	// get already selected asset type Ids
String[] searchCriteriaSelectedAssetTypesIds =
      portletPreferences.getValues(
          "searchCriteriaSelectedAssetTypes", exampleConfiguration.searchCriteriaSelectedAssetTypes());



if(searchCriteriaSelectedAssetTypesIds != null && searchCriteriaSelectedAssetTypesIds.length >0){
for(String id : searchCriteriaSelectedAssetTypesIds){
 assetTypeMetadataFields.add(availableClassNameIdsSet.get(id));
 leftAssetTypeList.add(new KeyValuePair(id, availableClassNameIdsSet.get(id)));
 
}
}

for (Map.Entry<String,String>  assetType : availableClassNameIdsSet.entrySet()) {
	if (Arrays.binarySearch(assetTypeMetadataFields.toArray(), assetType.getValue()) < 0) {
		rightAssetTypeList.add(new KeyValuePair(assetType.getKey(), assetType.getValue()));
	}
}
rightAssetTypeList = ListUtil.sort(rightAssetTypeList, new KeyValuePairComparator(false, true));


//Structure selection related

structureInclude =
    portletPreferences.getValue(
        "structureInclude", exampleConfiguration.structureInclude());

if(structureInclude == null){
  structureInclude = "include";
}


	// get already selected asset type Ids
String[] searchCriteriaSelectedStructureIds =
    portletPreferences.getValues(
        "searchCriteriaSelectedStructures", exampleConfiguration.searchCriteriaSelectedStructures());



if(searchCriteriaSelectedStructureIds != null && searchCriteriaSelectedStructureIds.length >0){
for(String id : searchCriteriaSelectedStructureIds){
structureMetadataFields.add(availableStructureSet.get(id));
leftStructureList.add(new KeyValuePair(id, availableStructureSet.get(id)));

}
}

for (Map.Entry<String,String>  structure : availableStructureSet.entrySet()) {
	if (Arrays.binarySearch(assetTypeMetadataFields.toArray(), structure.getValue()) < 0) {
		rightStructureList.add(new KeyValuePair(structure.getKey(), structure.getValue()));
	}
}
rightStructureList = ListUtil.sort(rightStructureList, new KeyValuePairComparator(false, true));


//get already selected Asset Categories
categoriesInclude = portletPreferences.getValue(
        "categoriesInclude", exampleConfiguration.categoriesInclude());
categoriesAll = portletPreferences.getValue(
        "categoriesAll", exampleConfiguration.categoriesAll());
if(categoriesAll == null){
    categoriesAll = "any";
  }
includeCategories = portletPreferences.getValue(
        "includeCategories", exampleConfiguration.categoriesInclude());

categoriesExclude = portletPreferences.getValue(
        "categoriesExclude", exampleConfiguration.categoriesInclude());
categoriesExcludeAll = portletPreferences.getValue(
        "categoriesExcludeAll", exampleConfiguration.categoriesAll());
if(categoriesExcludeAll == null){
    categoriesExcludeAll = "any";
  }
excludeCategories = portletPreferences.getValue(
        "excludeCategories", exampleConfiguration.categoriesInclude());

	// get already selected asset type Ids




 currentSelectedTags =
     portletPreferences.getValue(
         "tagNamesInclude", exampleConfiguration.tagNamesInclude());

 tagsAll = portletPreferences.getValue(
         "tagsIncludeAll", "any");
 
 
 


     if(tagsAll == null){
       tagsAll = "any";
     }

      currentSelectedTagsExclude =
          portletPreferences.getValue(
              "tagNamesExclude", exampleConfiguration.tagNamesExclude());

      tagsExcludeAll = portletPreferences.getValue(
              "tagsExcludeAll", "any");

          if(tagsExcludeAll == null){
            tagsExcludeAll = "any";
          }

    }
%>