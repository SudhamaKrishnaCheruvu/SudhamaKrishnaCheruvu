package at.gv.magwien.apps.search.criteria.configuration;

import aQute.bnd.annotation.metatype.Meta;

@Meta.OCD(id = "at.gv.magwien.apps.search.criteria.configuration.SearchCriteriaConfig")
public interface SearchCriteriaConfig {

    @Meta.AD(required = false)
    public String journalTypeInclude();

    @Meta.AD(required = false)
    public String[] searchCriteriaSelectedJournalFolder();

    @Meta.AD(required = false)
    public String[] searchCriteriaAvailableJournalFolder();

    @Meta.AD(required = false)
    public String documentTypeInclude();

    @Meta.AD(required = false)
    public String[] searchCriteriaSelectedDocumentFolder();

    @Meta.AD(required = false)
    public String[] searchCriteriaAvailableDocumentFolder();

    @Meta.AD(required = false)
    public String assetTypeInclude();

    @Meta.AD(required = false)
    public String[] searchCriteriaSelectedAssetTypes();

    @Meta.AD(required = false)
    public String[] searchCriteriaAvailableAssetTypes();

    @Meta.AD(required = false)
    public String structureInclude();

    @Meta.AD(required = false)
    public String[] searchCriteriaSelectedStructures();

    @Meta.AD(required = false)
    public String[] searchCriteriaAvailableStructures();

    @Meta.AD(required = false)
    public String categoriesInclude();

    @Meta.AD(required = false)
    public String categoriesAll();

    @Meta.AD(required = false)
    public String categoriesExclude();

    @Meta.AD(required = false)
    public String categoriesAllExclude();

    @Meta.AD(required = false)
    public String searchCriteriaSelectedAssetCategoriesExclude();

    @Meta.AD(required = false)
    public String tagsInclude();

    @Meta.AD(required = false)
    public String tagsAllInclude();

    @Meta.AD(required = false)
    public String tagNamesInclude();

    @Meta.AD(required = false)
    public String tagsExclude();

    @Meta.AD(required = false)
    public String tagsAllExclude();

    @Meta.AD(required = false)
    public String tagNamesExclude();

}
