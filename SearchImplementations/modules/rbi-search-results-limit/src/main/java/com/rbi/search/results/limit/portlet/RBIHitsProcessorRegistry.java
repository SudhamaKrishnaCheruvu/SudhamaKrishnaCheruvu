package com.rbi.search.results.limit.portlet;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.osgi.service.tracker.collections.map.PropertyServiceReferenceComparator;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.hits.HitsProcessor;
import com.liferay.portal.kernel.search.hits.HitsProcessorRegistry;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.Collections;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author SudhamaKrishnaC
 *
 */
@Component(service = HitsProcessorRegistry.class, property = { "service.ranking:Integer=100" })
public class RBIHitsProcessorRegistry implements HitsProcessorRegistry {

	@Override
	public boolean process(SearchContext searchContext, Hits hits) throws SearchException {

		if (!GetterUtil.getBoolean(searchContext.getAttribute("search.page.search"))) {

			return true;
		}
		/*
		 * if ((_serviceTrackerList.size() == 0)) {
		 * 
		 * return false; }
		 */

		QueryConfig queryConfig = searchContext.getQueryConfig();

		if (!queryConfig.isHitsProcessingEnabled()) {
			return false;
		}

		for (HitsProcessor hitsProcessor : _serviceTrackerList) {
			if (!hitsProcessor.process(searchContext, hits)) {
				break;
			}
		}

		return true;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerList = ServiceTrackerListFactory.open(bundleContext, HitsProcessor.class,
				Collections.reverseOrder(new PropertyServiceReferenceComparator<>("sort.order")));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerList.close();
	}

	private ServiceTrackerList<HitsProcessor> _serviceTrackerList;
}