package com.liferay.custom.daterange.facet.builder;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.facet.Facet;
import com.liferay.portal.search.facet.FacetFactory;
import com.liferay.portal.search.filter.FilterBuilders;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bryan Engler
 */
@Component(
	immediate = true, service = {FacetFactory.class, CustomDateFacetFactory.class}
)
public class CustomDateFacetFactoryImpl implements CustomDateFacetFactory {

	@Override
	public String getFacetClassName() {
		return "Custom Date";
	}

	@Override
	public Facet newInstance(SearchContext searchContext) {
		String fieldName = StringPool.BLANK;
		if(Validator.isNotNull(searchContext)) {
			fieldName = (String)searchContext.getAttribute("fieldName");
		}
		return new CustomDateFacetImpl(
				fieldName, searchContext, filterBuilders);
		
	}

	@Reference
	protected FilterBuilders filterBuilders;

}