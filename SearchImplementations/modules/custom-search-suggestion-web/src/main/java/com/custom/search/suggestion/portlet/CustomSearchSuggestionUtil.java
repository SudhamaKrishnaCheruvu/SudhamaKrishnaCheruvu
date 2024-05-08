package com.custom.search.suggestion.portlet;

import com.custom.search.suggestion.conf.RBIDidYouMeanConfiguration;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLFileEntryLocalServiceUtil;
import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.xml.DocumentException;
import com.liferay.portal.kernel.xml.SAXReaderUtil;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.portlet.PortletRequest;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.phrase.PhraseSuggestionBuilder;

/**
 * 
 * The purpose of this class is to create utility methods
 *
 * Accessibility : This method is used in portlet render method
 *
 *
 * @author Sudhama Krishna Cheruvu
 *
 */
/**
 * @author stprz090
 *
 */
/**
 * @author stprz090
 *
 */
public class CustomSearchSuggestionUtil {
	static Log log = LogFactoryUtil.getLog(CustomSearchSuggestionUtil.class.getName());

	public static String getNearestSuggestionResponse(String keyword,
			RBIDidYouMeanConfiguration rbiDidYouMeanConfiguration, PortletRequest portletRequest) {

		// ConfigurationProviderUtil.getSystemConfiguration(clazz, companyId)

		if (Validator.isNotNull(portletRequest)) {
			ThemeDisplay themeDisplay = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);

			String suggestedKeyword = CustomSearchSuggestionUtil.getQueryParams(themeDisplay.getURLCurrent(), "q");

			log.info(" suggestedKeyword : : : " + suggestedKeyword);
		}
		String[] elasticServerHosts = rbiDidYouMeanConfiguration.getElasticServerHosts();

		List<HttpHost> httpHosts = new ArrayList<HttpHost>();

		for (String elasticServerHost : elasticServerHosts) {
			HttpHost httpHost;
			String host = StringPool.BLANK;
			int port = 0;
			if (elasticServerHost.contains(StringPool.COLON)) {
				host = elasticServerHost.split(":")[0];
				port = Integer.parseInt(elasticServerHost.split(":")[1]);
				httpHost = new HttpHost(host, port);
			} else {
				host = elasticServerHost;
				httpHost = new HttpHost(host);
			}
			httpHosts.add(httpHost);
		}
		HttpHost[] httpHostArray = httpHosts.toArray(new HttpHost[0]);

		RestClientBuilder restClientBuilder = RestClient.builder(httpHostArray);

		log.info("rbiDidYouMeanConfiguration.authenticationEnabled() "
				+ rbiDidYouMeanConfiguration.authenticationEnabled());

		if (rbiDidYouMeanConfiguration.authenticationEnabled()) {

			String elasticSearchServerUserName = rbiDidYouMeanConfiguration.getUsername();
			String elasticSearchServerPassword = rbiDidYouMeanConfiguration.getPassword();

			if (Validator.isNotNull(elasticSearchServerUserName) && Validator.isNotNull(elasticSearchServerPassword)
					&& !elasticSearchServerUserName.isEmpty() && !elasticSearchServerPassword.isEmpty()) {

				log.info(" elasticSearchServerUserName " + elasticSearchServerUserName + " elasticSearchServerPassword "
						+ elasticSearchServerPassword);

				final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

				credentialsProvider.setCredentials(AuthScope.ANY,
						new UsernamePasswordCredentials(elasticSearchServerUserName, elasticSearchServerPassword));
				// Create an Elasticsearch client

				restClientBuilder = restClientBuilder.setHttpClientConfigCallback(httpClientBuilder -> {
					httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
					return httpClientBuilder;
				});

			}
		}

		RestHighLevelClient client = new RestHighLevelClient(restClientBuilder);

		try {

			String field_name = "title_en_US";
			String suggestion = "{{suggestion}}";

			// Create a search request
			SearchRequest searchRequest = new org.elasticsearch.action.search.SearchRequest(
					rbiDidYouMeanConfiguration.getShardNode());

			// Create a search source builder
			SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

			// Add the main text query
			// String keyword = "simplifig";

			PhraseSuggestionBuilder phraseSuggestionBuilder = SuggestBuilders.phraseSuggestion("title_en_US.suggest")
					.text(keyword).size(7).confidence(1.0f).maxErrors(4).collateQuery(
							String.valueOf(QueryBuilders.matchQuery(field_name, suggestion).operator(Operator.AND)));

			SuggestBuilder suggestBuilder = new SuggestBuilder().addSuggestion("suggest", phraseSuggestionBuilder)
					.setGlobalText("did_you_mean");

			sourceBuilder.suggest(suggestBuilder);

			log.info(suggestBuilder.toString());

			// Set the search source builder in the search request
			searchRequest.source(sourceBuilder);

			// Execute the search request
			SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

			Suggest suggest = searchResponse.getSuggest();

			log.info(suggest.toString());

			client.close();

			return suggest.toString();
		} catch (Exception e) {
			log.error(e);
		}
		return StringPool.BLANK;
	}

	/**
	 * Gets the query params.
	 *
	 * @param url   the url
	 * @param param the param
	 * @return the query params
	 */
	public static String getQueryParams(String url, String param) {
		if (param == null) {
			return StringPool.BLANK;
		}
		try {
			String[] urlParts = url.split("\\?");
			if (urlParts.length > 1) {
				String query = urlParts[1];
				for (String paramVal : query.split("&")) {
					String[] pair = paramVal.split("=");
					if (pair[0].contains(param)) {
						return URLDecoder.decode(pair[1], StandardCharsets.UTF_8.toString());
						// return pair[1];
					}
				}
			}

		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		return StringPool.BLANK;
	}

	/**
	 * @param assetCategories
	 * @param themeDisplay
	 * @param journalArticle
	 * @param assetRenderViewURL
	 * @return assetRenderViewURL
	 * @throws DocumentException
	 * @throws PortalException
	 */
	public static String getRedirectionURLInSearchResults(List<AssetCategory> assetCategories,
			ThemeDisplay themeDisplay, JournalArticle journalArticle, String assetRenderViewURL)
			throws DocumentException, PortalException {

		com.liferay.portal.kernel.xml.Document content = SAXReaderUtil
				.read(journalArticle.getContentByLocale(themeDisplay.getLanguageId()));
		com.liferay.portal.kernel.xml.Document localeContent = SAXReaderUtil
				.read(journalArticle.getContentByLocale("en_US"));

		switch (journalArticle.getDDMStructureKey()) {

		case "11497585":
			String titleWithoutLocale = localeContent
					.valueOf("//dynamic-element[@name='Text98851269' ]/dynamic-content");
			String pageString = localeContent
					.valueOf("//dynamic-element[@name='SelectFromList52486135' ]/dynamic-content");

			if ("Option68453029".equalsIgnoreCase(pageString)) {
				assetRenderViewURL = "/web/rbi/faq-page-1?ddm__keyword__19506552__FaqDetailPage1Title_en_US="
						+ titleWithoutLocale;
			}
			if ("Option45450404".equalsIgnoreCase(pageString)) {
				assetRenderViewURL = "/web/rbi/faq-page-2?ddm__keyword__26256231__FaqDetailPage2Title_en_US="
						+ titleWithoutLocale;
			}
			if ("Option94858029".equalsIgnoreCase(pageString)) {
				assetRenderViewURL = "/web/rbi/faq-page-3?ddm__keyword__26261231__FaqDetailPage3Title_en_US="
						+ titleWithoutLocale;
			}
			if ("Option12897622".equalsIgnoreCase(pageString)) {
				assetRenderViewURL = "/web/rbi/faq-page-4?ddm__keyword__26245192__faqdetailpage4title_en_US="
						+ titleWithoutLocale;
			}

			break;

		case "40368149":
			assetRenderViewURL = content.valueOf("//dynamic-element[@name='Text67237307' ]/dynamic-content");

			if (Validator.isNull(assetRenderViewURL)) {
				assetRenderViewURL = "complaints/banks";
			}

			break;

		case "40008162":
			String selectedListItem = content
					.valueOf("//dynamic-element[@name='SelectFromList34735715']/dynamic-content");
			if (Validator.isNotNull(selectedListItem)) {
				if (selectedListItem == "Option51744698") {
					assetRenderViewURL = "/web/rbi/publications/bank-branches-participating-in-ecs";
				}
			} else {
				assetRenderViewURL = "publications/electronic-clearing-service";
			}
			break;
		case "43618542":
			String femaNotification = content.valueOf("//dynamic-element[@name='RichText65556635']/dynamic-content");
			assetRenderViewURL = "foreign-exchange-management/foreign-exchange-management-act";

			break;
		case "39690085":
			assetRenderViewURL = "complaints/";
			break;
		case "41712483":
			assetRenderViewURL = "complaints/nbfcs";
			break;
		case "41578385":
			assetRenderViewURL = content.valueOf("//dynamic-element[@name='Text86708991' ]/dynamic-content");
			if (Validator.isNull(assetRenderViewURL)) {
				assetRenderViewURL = "complaints/prepaid-payment-instrument";
			}
			break;
		case "46230277":
			String uploadFile = content.valueOf("//dynamic-element[@name='Upload57466840' ]/dynamic-content");
			JSONObject uploadFileJson = JSONFactoryUtil.createJSONObject(uploadFile);

			String uploadFileURL = StringPool.BLANK;
			if (Validator.isNotNull(uploadFileJson)) {
				uploadFileURL = fetchFileURL(uploadFileJson.getString("uuid"), journalArticle.getGroupId(),
						themeDisplay);
			}
			log.error("uploadFileURL " + uploadFileURL);

			if (uploadFileURL.isEmpty() || Validator.isNotNull(uploadFileURL) || uploadFileURL == StringPool.BLANK) {
				uploadFileURL = uploadFileURL.replace("download=true", "");
				assetRenderViewURL = uploadFileURL;
			}
			log.error("assetRenderView U R L  : " + assetRenderViewURL);
			break;
		case "44214814":
			assetRenderViewURL = content.valueOf("//dynamic-element[@name='Text92613298' ]/dynamic-content");

			if (Validator.isNull(assetRenderViewURL)) {

				assetRenderViewURL = "home/covid19";

			}
			break;
		case "21491706":

			String page = content.valueOf("//dynamic-element[@name='SelectFromList98073587']/dynamic-content");
			String externalRedirectionURL = content.valueOf("//dynamic-element[@name='Text17979006']/dynamic-content");
			String viewDetailPageURL = assetRenderViewURL.split("?")[0];
			long redirectCategorytId = 0l;

			for (AssetCategory assetCategory : assetCategories) {
				if (assetCategory.getVocabularyId() == 24922082) {
					redirectCategorytId = assetCategory.getCategoryId();
				} else if (assetCategory.getVocabularyId() == 40300265) {
					redirectCategorytId = assetCategory.getCategoryId();
				}
				/*
				 * if(assetCategory.getVocabularyId()==21281642) {
				 * publicationFrequency=assetCategory.getTitle(locale); }
				 */
			}

			if (page == "Option54968807") {
				assetRenderViewURL = "/web/rbi/publications/chapters?category=" + redirectCategorytId;
			}
			if (page == "Option73901681") {
				assetRenderViewURL = "/web/rbi/publications/articles?category=" + redirectCategorytId;
			}
			if (page == "Option34343300") {
				assetRenderViewURL = viewDetailPageURL;
			}
			if (page == "Option87467445") {
				assetRenderViewURL = externalRedirectionURL;
			}

			if (Validator.isNull(assetRenderViewURL)) {

				assetRenderViewURL = "complaints/prepaid-payment-instrument";

			}
			break;

		case "14003553":

			String urlType = "";
			String youtubeLink = content.valueOf("//dynamic-element[@name='Text00912349']/dynamic-content");
			String audioLink = content.valueOf("//dynamic-element[@name='Text21165884']/dynamic-content");
			String externalLink = content.valueOf("//dynamic-element[@name='Text44083102']/dynamic-content");
			String pdfDocLink = content.valueOf("//dynamic-element[@name='Text03413064']/dynamic-content");
			String excelDocLink = content.valueOf("//dynamic-element[@name='Text82145864']/dynamic-content");

			if (externalLink.contains("https://youtube.com/live/")) {

				youtubeLink = externalLink;
				externalLink = audioLink;

			}
			if (Validator.isNotNull(youtubeLink)) {

				urlType = "youtubeLink";
				assetRenderViewURL = youtubeLink;

				if (assetRenderViewURL.contains("https://youtu.be/")) {
					assetRenderViewURL = assetRenderViewURL.replace("https://youtu.be/",
							"https://www.youtube.com/embed/");
				}
				if (assetRenderViewURL.contains("/watch?v=")) {
					assetRenderViewURL = assetRenderViewURL.replace("/watch?v=", "/embed/");
				}
				if (assetRenderViewURL.contains("https://youtube.com/live/")) {
					assetRenderViewURL = assetRenderViewURL.replace("https://youtube.com/live/",
							"https://www.youtube.com/embed/");
				}
				if (assetRenderViewURL.contains("https://www.youtube.com/live/")) {
					assetRenderViewURL = assetRenderViewURL.replace("https://www.youtube.com/live/",
							"https://www.youtube.com/embed/");
				}
				if (assetRenderViewURL.contains("?feature=share")) {
					assetRenderViewURL = assetRenderViewURL.replace("?feature=share", "");
				}

			} else if (Validator.isNotNull(audioLink)) {

				urlType = "audioLink";
				assetRenderViewURL = audioLink;

			} else if (Validator.isNotNull(externalLink)) {

				assetRenderViewURL = externalLink;

			} else if (Validator.isNotNull(pdfDocLink)) {

				assetRenderViewURL = pdfDocLink;

			} else if (Validator.isNotNull(excelDocLink)) {

				assetRenderViewURL = excelDocLink;

			} else {

			}
			break;

		default:

			assetRenderViewURL = "/web" + themeDisplay.getScopeGroup().getFriendlyURL() + "/-/"
					+ journalArticle.getFriendlyURLMap().get(Locale.US);
		}

		return assetRenderViewURL;
	}

	private static String fetchFileURL(String uuid, long groupId, ThemeDisplay themeDisplay) throws PortalException {
		String fileURL = StringPool.BLANK;
		try {
			DLFileEntry fileEntry = DLFileEntryLocalServiceUtil.fetchFileEntry(uuid, groupId);
			AssetEntry fileAssetEntry = AssetEntryLocalServiceUtil
					.getEntry("com.liferay.document.library.kernel.model.DLFileEntry", fileEntry.getFileEntryId());
			fileURL = fileAssetEntry.getAssetRenderer().getURLDownload(themeDisplay);
			fileURL = fileURL.replace("/" + uuid, "");
		} catch (PortalException e) {
			log.error("error while fetching the file");
		}
		return fileURL;
	}

}
