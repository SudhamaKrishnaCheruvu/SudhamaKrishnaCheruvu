package sample.indexer.portlet;

import com.liferay.analytics.dxp.entity.rest.dto.v1_0.Field;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.IndexerPostProcessor;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.search.filter.BooleanFilter;

import java.util.Date;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;

/**
 * @author SudhamaKrishnaC
 *
 *         Post Processor indexer to add the fields from the custom struture to
 *         the indexer to use in the search and sorting
 *
 */

@Component(immediate = true, property = {
		"indexer.class.name=com.liferay.journal.model.JournalArticle" }, service = IndexerPostProcessor.class)
public class JournalArticleIndexerPostProcessor implements IndexerPostProcessor {

	/**
	 * Method holds the logic for post processor indexer with the document and the
	 * model related object available
	 */
	@Override
	public void postProcessDocument(Document document, Object obj) throws Exception {

		JournalArticle journalArticle = (JournalArticle) obj;

		JournalArticle journalArticleLatest = JournalArticleLocalServiceUtil
				.getLatestArticle(journalArticle.getResourcePrimKey());

		if (journalArticleLatest.getVersion() == journalArticle.getVersion()) {

			Field myTestDateField;

			document.addDate("mytestDate", new Date());
			document.addDateSortable("mytestDate", new Date());

		}

	}

	@Override
	public void postProcessContextBooleanFilter(BooleanFilter booleanFilter, SearchContext searchContext)
			throws Exception {
		log.debug("postProcessContextBooleanFilter");
	}

	@Override
	public void postProcessFullQuery(BooleanQuery fullQuery, SearchContext searchContext) throws Exception {
		log.debug("postProcessFullQuery");
	}

	@Override
	public void postProcessSearchQuery(BooleanQuery searchQuery, BooleanFilter booleanFilter,
			SearchContext searchContext) throws Exception {
		log.debug("postProcessSearchQuery");
	}

	@Override
	public void postProcessSummary(Summary summary, Document document, Locale locale, String snippet) {
		log.debug("postProcessSummary");
	}

	private Log log = LogFactoryUtil.getLog(JournalArticleIndexerPostProcessor.class);

}
