package groovyscript.check.portlet;

import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;

public class GoovyCodeCheck {

	private void myGroovyScripts() {

		{
			long groupId = 0;
			String articleId = null;
			Indexer<JournalArticle> journalArticleIndexer = IndexerRegistryUtil.getIndexer(JournalArticle.class);
			try {
				JournalArticle journalArticle = JournalArticleLocalServiceUtil.getArticle(groupId, articleId);
				System.out.println(journalArticle.getTitle());
				journalArticleIndexer.reindex(journalArticle);
			} catch (SearchException e) {
				e.printStackTrace();
			} catch (PortalException e) {
				e.printStackTrace();
			}
		}

		{

			try {
				System.out.println("started ");
				Layout layout = LayoutLocalServiceUtil.getLayout(995);
				Indexer<Layout> layoutIndexer = IndexerRegistryUtil.getIndexer(Layout.class);
				System.out.println("before ");
				layoutIndexer.reindex(layout);
				System.out.println("after ");
			} catch (PortalException e) {
				System.out.println(e);
			}

		}

		// method ends
	}

	// class ends

}
