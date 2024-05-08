package search.query.mvn.portlet;

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

public class DemoSearch {
    public static void demo() {
        System.out.println("In demo");
        HttpHost httpHost = new HttpHost("localhost", 9200);
        // Create an Elasticsearch client
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(httpHost));

        try {
            String field_name = "title_en_US";
            String suggestion = "{{suggestion}}";
            // Create a search request
            SearchRequest searchRequest = new SearchRequest("liferay-20097");

            // Create a search source builder
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();


            // Add the main text query
            String keyword = "hoem";

            PhraseSuggestionBuilder phraseSuggestionBuilder = SuggestBuilders.phraseSuggestion("title_en_US.suggest")
                    .text(keyword)
                    .size(3)
                    .confidence(1.0f)
                    .maxErrors(2)
                    .collateQuery(String.valueOf(QueryBuilders.matchQuery(field_name, suggestion).operator(Operator.AND)))
                    ;
            SuggestBuilder suggestBuilder = new SuggestBuilder().addSuggestion("suggest", phraseSuggestionBuilder).setGlobalText("did_you_mean");
            sourceBuilder.suggest(suggestBuilder);


            // Set the search source builder in the search request
            searchRequest.source(sourceBuilder);

            // Execute the search request
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            // Process the search response
            Suggest hits = searchResponse.getSuggest();

            // Iterate over the search hits
            System.out.println(hits);

            // Close the Elasticsearch client
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
