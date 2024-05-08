package search.query.mvn.portlet;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.elasticsearch7.configuration.ElasticsearchConfiguration;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.Searcher;

import java.io.IOException;
import java.util.Map;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.phrase.PhraseSuggestionBuilder;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import search.query.mvn.constants.SearchQueryMvnPortletKeys;

/**
 * @author root324
 */
@Component(property = { "com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.header-portlet-css=/css/main.css", "com.liferay.portlet.instanceable=true",
		"javax.portlet.display-name=SearchQueryMvn", "javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + SearchQueryMvnPortletKeys.SEARCHQUERYMVN,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user" }, service = Portlet.class)
public class SearchQueryMvnPortlet extends MVCPortlet {

	@Override
	public void render(RenderRequest renderRequest, RenderResponse renderResponse)
			throws IOException, PortletException {

		ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY);

		HttpHost httpHost = new HttpHost(themeDisplay.getServerName(), _configuration.embeddedHttpPort());

		RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(httpHost));

		try {
			String field_name = "title_en_US";
			String suggestion = "{{suggestion}}";
			// Create a search request
			SearchRequest searchRequest = new org.elasticsearch.action.search.SearchRequest("liferay-20097");

			// Create a search source builder
			SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

			// Add the main text query
			String keyword = "kolin";

			PhraseSuggestionBuilder phraseSuggestionBuilder = SuggestBuilders.phraseSuggestion("title_en_US.suggest")
					.text(keyword).size(3).confidence(1.0f).maxErrors(2).collateQuery(
							String.valueOf(QueryBuilders.matchQuery(field_name, suggestion).operator(Operator.AND)));

			SuggestBuilder suggestBuilder = new SuggestBuilder().addSuggestion("suggest", phraseSuggestionBuilder)
					.setGlobalText("did_you_mean");
			sourceBuilder.suggest(suggestBuilder);

			System.out.println(suggestBuilder.toString());

			// Set the search source builder in the search request
			searchRequest.source(sourceBuilder);

			// Execute the search request
			SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

			Suggest suggest = searchResponse.getSuggest();

			System.out.println(suggest);

			client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.render(renderRequest, renderResponse);
	}

	@Reference

	protected Queries queries;

	@Reference

	protected Searcher searcher;

	@Reference
	protected SearchRequestBuilderFactory searchRequestBuilderFactory;

	@Activate
	protected void activate(Map<String, Object> properties) {
		_configuration = ConfigurableUtil.createConfigurable(ElasticsearchConfiguration.class, properties);
	}

	private ElasticsearchConfiguration _configuration;
}
