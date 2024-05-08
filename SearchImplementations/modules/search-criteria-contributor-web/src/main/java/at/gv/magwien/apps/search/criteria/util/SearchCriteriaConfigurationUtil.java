package at.gv.magwien.apps.search.criteria.util;

import at.gv.magwien.apps.search.criteria.contributor.web.constants.SearchCriteriaContributorWebPortletKeys;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetVocabularyLocalServiceUtil;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLFolderLocalServiceUtil;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalFolderLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * The Class SearchCriteriaConfigurationUtil.
 */
public class SearchCriteriaConfigurationUtil {

    private static Log log = LogFactoryUtil.getLog(SearchCriteriaConfigurationUtil.class);

    private SearchCriteriaConfigurationUtil() {
    }

    /**
     * Gets the journal folders.
     *
     * @param groupId
     *            the group id
     * @return the journal folders
     */
    public static Map<String, String> getJournalFolders(long groupId) {
        List<JournalFolder> siteFolders = JournalFolderLocalServiceUtil.getFolders(groupId);
        Map<String, String> folders = new HashMap<>();
        for (JournalFolder folder : siteFolders) {
            folders.put(folder.getUuid(), getJournalFolderName(folder));
        }
        return folders;
    }
    
    /**
     * Gets the journal folder ids.
     *
     * @param groupId the group id
     * @return the journal folder ids
     */
    public static Set<String> getJournalFolderIds(long groupId) {
        List<JournalFolder> siteFolders = JournalFolderLocalServiceUtil.getFolders(groupId);
        Set<String> folders = new HashSet<>();
        for (JournalFolder folder : siteFolders) {
            folders.add(String.valueOf(folder.getFolderId()));
        }
        return folders;
    }

    /**
     * Gets the DL folder ids.
     *
     * @param groupId the group id
     * @return the DL folder ids
     */
    public static Set<String> getDLFolderIds(long groupId) {
        Set<String> folders = new HashSet<>();

        List<DLFolder> allFolders = getSubFolders(DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, groupId);
        for (DLFolder folder : allFolders) {

            folders.add(String.valueOf(folder.getFolderId()));
        }
        return folders;
    }

    /**
     * Gets the journal folder name.
     *
     * @param folder
     *            the folder
     * @return the journal folder name
     */
    private static String getJournalFolderName(JournalFolder folder) {
        if (folder.getParentFolderId() == 0) {
            return folder.getName();
        } else {
            StringBuilder name = new StringBuilder();
            name.append(folder.getName() + StringPool.SPACE);

            String treePath = folder.getTreePath().substring(0, folder.getTreePath().length() - 1);
            String[] parentFolders = treePath.split(StringPool.FORWARD_SLASH);
            try {
                if (parentFolders.length > 0) {
                    name.append(StringPool.OPEN_PARENTHESIS);
                    for (int i = 1; i < parentFolders.length; i++) {
                        treePath = treePath.replace(parentFolders[i],
                                JournalFolderLocalServiceUtil.getFolder(Long.valueOf(parentFolders[i])).getName());

                    }
                    name.append(treePath + StringPool.CLOSE_PARENTHESIS);
                }
            } catch (NumberFormatException e) {
                log.error("Error in parsing folderId value " + e);
            } catch (PortalException e) {

                log.error("Error in getting folder with specified folder Id " + e);
            }
            return name.toString();
        }
    }

    /**
     * Gets the sub folders.
     *
     * @param parentFolderId
     *            the parent folder id
     * @param groupId
     *            the group id
     * @return the sub folders
     */
    private static List<DLFolder> getSubFolders(long parentFolderId, long groupId) {
        List<DLFolder> subFolders = DLFolderLocalServiceUtil.getFolders(groupId, parentFolderId, false);
        List<DLFolder> allSubFolders = new ArrayList<>();
        List<DLFolder> arrayFolders = new ArrayList<>();

        if (subFolders != null && !subFolders.isEmpty()) {
            allSubFolders.addAll(subFolders);
        }
        if (!allSubFolders.isEmpty()) {
            for (DLFolder subFolder : allSubFolders) {
                arrayFolders = getSubFolders(subFolder.getFolderId(), subFolder.getGroupId());
            }
            allSubFolders.addAll(arrayFolders);
        }
        return allSubFolders;
    }

    /**
     * Gets the DL folders.
     *
     * @param groupId
     *            the group id
     * @return the DL folders
     */
    public static Map<String, String> getDLFolders(long groupId) {
        Map<String, String> folders = new HashMap<>();

        List<DLFolder> allFolders = getSubFolders(DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, groupId);
        for (DLFolder folder : allFolders) {

            folders.put(folder.getUuid(), getDLFolderName(folder));
        }
        return folders;
    }

    /**
     * Gets the DL folder name.
     *
     * @param folder
     *            the folder
     * @return the DL folder name
     */
    private static String getDLFolderName(DLFolder folder) {
        if (folder.getParentFolderId() == 0) {
            return folder.getName();
        } else {
            StringBuilder name = new StringBuilder();
            name.append(folder.getName() + StringPool.SPACE);
            String treePath = folder.getTreePath().substring(0, folder.getTreePath().length() - 1);
            log.info("inside getDLFolderName method .. treePath of folder is " + treePath);
            String[] parentFolders = treePath.split(StringPool.FORWARD_SLASH);
            log.info("inside getDLFolderName method .. parentFolders array length is " + parentFolders.length);
            try {
                if (parentFolders.length > 0) {
                    name.append(StringPool.OPEN_PARENTHESIS);
                    for (int i = 1; i < parentFolders.length; i++) {
                        treePath = treePath.replace(parentFolders[i],
                                DLFolderLocalServiceUtil.getFolder(Long.valueOf(parentFolders[i])).getName());

                    }
                    name.append(treePath);
                    name.append(StringPool.CLOSE_PARENTHESIS);
                }
            } catch (NumberFormatException e) {
                log.error("Error in parsing folderId value " + e);
            } catch (PortalException e) {

                log.error("Error in getting folder with specified folder Id " + e);
            }
            log.info("inside getDLFolderName method .. name of folder is " + name.toString());
            return name.toString();
        }
    }

    /**
     * Gets the class name ids.
     *
     * @param companyId
     *            the company id
     * @param locale
     *            the locale
     * @return the class name ids
     */
    public static Map<String, String> getClassNameIds(long companyId, Locale locale) {
        long[] classNames = AssetRendererFactoryRegistryUtil.getClassNameIds(companyId, true);
        Map<String, String> classNameIdsList = new HashMap<>();

        for (long classNameId : classNames) {
            try {
                ClassName className = ClassNameLocalServiceUtil.getClassName(classNameId);
                classNameIdsList.put(String.valueOf(className.getClassNameId()),
                        LanguageUtil.get(locale, PortalUtil.getClassName(classNameId)));
            } catch (PortalException e) {
                log.error("Exception in getting className for id " + classNameId);
            }

        }
        return classNameIdsList;

    }

    /**
     * Gets the structures.
     *
     * @param groupId
     *            the group id
     * @param companyId
     *            the company id
     * @param locale
     *            the locale
     * @return the structures
     */
    public static Map<String, String> getStructures(long groupId, long companyId, Locale locale) {
        List<com.liferay.dynamic.data.mapping.model.DDMStructure> structures = DDMStructureLocalServiceUtil
                .getStructures(groupId);
        Map<String, String> structuresList = new HashMap<>();
        List<DDMStructure> globalStructures = new ArrayList<>();
        try {
            long companyGroupId = GroupLocalServiceUtil.getCompanyGroup(companyId).getGroupId();
            globalStructures = DDMStructureLocalServiceUtil.getStructures(companyGroupId);

        } catch (PortalException e) {
            log.error("Error in fetching global level structures");
        }
        for (com.liferay.dynamic.data.mapping.model.DDMStructure structure : structures) {
            structuresList.put(String.valueOf(structure.getStructureKey()), structure.getName(locale));
        }
        for (com.liferay.dynamic.data.mapping.model.DDMStructure structure : globalStructures) {
            structuresList.put(String.valueOf(structure.getStructureKey()),
                    structure.getName(locale) + SearchCriteriaContributorWebPortletKeys.GLOBAL);
        }
        return structuresList;

    }

    /**
     * Gets the asset vocabularies.
     *
     * @param groupId
     *            the group id
     * @return the asset vocabularies
     */
    public static Map<String, String> getAssetVocabularies(long groupId) {
        DynamicQuery query = AssetVocabularyLocalServiceUtil.dynamicQuery();
        query.add(RestrictionsFactoryUtil.eq("groupId", groupId));
        List<AssetVocabulary> vocabularies = AssetVocabularyLocalServiceUtil.dynamicQuery(query);
        Map<String, String> vocabulariesList = new HashMap<>();
        if (!vocabularies.isEmpty()) {
            for (AssetVocabulary assetVocabulary : vocabularies) {
                vocabulariesList.put(String.valueOf(assetVocabulary.getVocabularyId()), assetVocabulary.getName());
            }

        }
        return vocabulariesList;

    }

    /**
     * Gets the sites.
     *
     * @param companyId
     *            the company id
     * @return the sites
     */
    public static Map<String, String> getSites(long companyId) {
        List<Group> groups = GroupLocalServiceUtil.getGroups(companyId, 0, false);
        Map<String, String> groupsList = new HashMap<>();
        if (!groups.isEmpty()) {
            for (Group grp : groups) {
                groupsList.put(String.valueOf(grp.getGroupId()), grp.getName());
            }
        }
        return groupsList;

    }
}
