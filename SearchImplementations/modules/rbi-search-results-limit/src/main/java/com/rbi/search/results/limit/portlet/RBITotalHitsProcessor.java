package com.rbi.search.results.limit.portlet;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.facet.collector.DefaultTermCollector;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.search.facet.collector.TermCollector;
import com.liferay.portal.kernel.search.hits.HitsProcessor;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author SudhamaKrishnaC
 *
 */
@Component(property = "sort.order=0", service = HitsProcessor.class)
public class RBITotalHitsProcessor implements HitsProcessor {

	private static final Log log = LogFactoryUtil.getLog(RBITotalHitsProcessor.class);

	@Override
	public boolean process(SearchContext searchContext, Hits hits) throws SearchException {

		// This processor only applies to search from the search page

		if (!GetterUtil.getBoolean(searchContext.getAttribute("search.page.search"))) {
			return true;
		}

		log.error(hits.getLength());

		// Make a configuration for this...

		int maxResultsCount = 10000;

		if (Validator.isNotNull(GetterUtil.getInteger(searchContext.getAttribute("search.results.limit")))) {
			maxResultsCount = GetterUtil.getInteger(searchContext.getAttribute("search.results.limit"));
		}

		// Adjust total count

		if (hits.getLength() > maxResultsCount) {
			hits.setLength(10000);
		}

		// Adjust current set, if delta > maxResultsCount

		Document[] documents = hits.getDocs();

		log.error(documents.length);

		if (documents.length > maxResultsCount) {
			hits.setDocs(ArrayUtil.subset(documents, 0, maxResultsCount));
		}

		// Adjust facet terms' total counts if > maxResultsCount

		_adjustFacetTermFreqeuencies(maxResultsCount, searchContext);

		return true;
	}

	private void _adjustFacetTermFreqeuencies(int maxResultsCount, SearchContext searchContext) {

		MapUtil.isNotEmptyForEach(searchContext.getFacets(), (term, facet) -> {
			FacetCollector facetCollector = facet.getFacetCollector();

			String fieldName = facetCollector.getFieldName();

			List<TermCollector> adjustedTermCollectors = new ArrayList<>();

			for (TermCollector termCollector : facetCollector.getTermCollectors()) {

				if (termCollector.getFrequency() > maxResultsCount) {
					adjustedTermCollectors.add(new DefaultTermCollector(termCollector.getTerm(), maxResultsCount));
				} else {
					adjustedTermCollectors.add(termCollector);
				}
			}

			facet.setFacetCollector(new FacetCollector() {

				@Override
				public String getFieldName() {
					return fieldName;
				}

				@Override
				public TermCollector getTermCollector(String term) {
					for (TermCollector termCollector : adjustedTermCollectors) {

						if (term.equals(termCollector.getTerm())) {
							return termCollector;
						}
					}

					return null;
				}

				@Override
				public List<TermCollector> getTermCollectors() {
					return adjustedTermCollectors;
				}

			});
		});
	}
}
