<%@page import="com.liferay.portal.kernel.util.HtmlUtil"%>
<%@page import="com.liferay.portal.kernel.language.LanguageUtil"%>
<%@page import="javax.validation.Valid"%>
<%@page import="com.liferay.portal.kernel.xml.SAXReaderUtil"%>
<%@page import="com.liferay.journal.service.JournalArticleLocalServiceUtil"%>
<%@page import="com.liferay.journal.model.JournalArticle"%>
<%@page import="com.liferay.portal.kernel.service.LayoutLocalServiceUtil"%>
<%@page import="com.liferay.portal.kernel.model.Layout"%>
<%@page import="com.liferay.document.library.util.DLURLHelperUtil"%>
<%@page import="com.liferay.document.library.kernel.util.DL"%>
<%@page import="com.liferay.document.library.kernel.service.DLFileEntryLocalServiceUtil"%>
<%@page import="com.liferay.document.library.kernel.model.DLFileEntry"%>
<%@page import="javax.portlet.WindowState"%>
<%@page import="com.liferay.portal.kernel.util.DateUtil"%>
<%@page import="java.util.Locale"%>
<%@page import="com.liferay.asset.kernel.service.AssetVocabularyLocalServiceUtil"%>
<%@page import="java.util.Date"%>
<%@page import="com.liferay.asset.kernel.model.AssetVocabulary"%>
<%@page import="com.liferay.asset.kernel.service.AssetVocabularyLocalService"%>
<%@page import="com.liferay.asset.kernel.model.AssetCategory"%>
<%@page import="com.liferay.portal.kernel.search.Field"%>
<%@page import="com.liferay.asset.kernel.model.AssetEntry"%>
<%@page import="com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="com.liferay.portal.kernel.search.Document"%>
<%@page import="com.liferay.portal.kernel.util.Validator"%>
<%@page import="com.liferay.petra.string.StringPool"%>
<%@page import="com.custom.search.suggestion.constants.CustomSearchSuggestionConstants"%>
<%@page import="com.custom.search.suggestion.portlet.CustomSearchSuggestionUtil"%>
<%@page import="com.custom.search.suggestion.util.SearchStringUtil"%>
<%@page import="com.custom.search.suggestion.display.context.builder.CustomSuggestionsPortletDisplayContextBuilder"%>
<%@page import="com.custom.search.suggestion.display.context.builder.CustomSuggestionsPortletDisplayContext"%>
<%@page import="com.custom.search.suggestion.display.context.builder.CustomSuggestionDisplayContext"%>
<%@page import="com.liferay.portal.kernel.util.WebKeys"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ include file="/init.jsp"%>
<%
	int index=0;
	Object documentObject = request.getAttribute("hitDocuments");
	List<Document> documents = new ArrayList<Document>();
	String url = null;
	String suggestedKeyword = null;
	
	if (Validator.isNotNull(documentObject)) {
		documents = (List<Document>) documentObject;
	}
	Object urlObject = request.getAttribute("redirectUrl");
	if (Validator.isNotNull(urlObject)) {
		url = (String) urlObject;
	}

	Object suggestedKeywordObject = request.getAttribute("suggestedKeyword");
	if (Validator.isNotNull(suggestedKeywordObject)) {
		suggestedKeyword = (String) suggestedKeywordObject;
	}
	if (documents.size() > 0) {
%>
<portlet:resourceURL id="/custom/search" var="customSearchSuggestionResourceURL" />
<div class="dym-search-results Search-result-content-wrap full-width"
	tabindex="0" id="skipContent">
	<div class="container">
		<div class="row">
			<div class="col-12">
				<div class="show-search-result">
					<div class="search_result_info">
						<h2 class="search_result_h2">
							<liferay-ui:message key="did-you-mean-x"
								arguments="<%=suggestedKeyword.trim()%>" />
							<liferay-ui:message key="here-are-some-top-results-x"
								arguments="<%=suggestedKeyword.trim()%>" />
						</h2>
					</div>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-12">
				<div class="Search-result-row-wrapper">
					<div class="row">
						<div class="col-12">
							<%
							 String images_folder=themeDisplay.getPathThemeImages();
								for (Document document : documents) {
									index=index+111;

										try {

											AssetEntry assetEntry = AssetEntryLocalServiceUtil.fetchEntry(
													document.get(themeDisplay.getLocale(), Field.ENTRY_CLASS_NAME),
													Long.parseLong(document.get(themeDisplay.getLocale(), Field.ENTRY_CLASS_PK)));
											List<AssetCategory> assetCategories = assetEntry.getCategories();
											String categoryName = StringPool.BLANK;
											if ("com.liferay.journal.model.JournalArticle"
													.equalsIgnoreCase(document.get(Field.ENTRY_CLASS_NAME))) {
												for (AssetCategory assetCategory : assetCategories) {
													AssetVocabulary assetVocabulary = AssetVocabularyLocalServiceUtil
															.fetchAssetVocabulary(assetCategory.getVocabularyId());
													String vocabularyName = assetVocabulary.getName();
													if ("newsectionmaster".equalsIgnoreCase(vocabularyName)
															&& assetCategory.getTitle(Locale.US) != "Index to rbi circular") {
														categoryName = assetCategory.getTitle(locale);
													}
												}
											}
							%>
							<div class="Search-result-row-each">
								<div class="Search-result-row-each-inner">
									<%
										if (!"com.liferay.portal.kernel.model.Layout"
															.equalsIgnoreCase(document.get(Field.ENTRY_CLASS_NAME))) {
									%>
									<div class="Search-result-date">
										<%
											String assetPublishedDate = StringPool.BLANK;
															if (Validator.isNotNull(assetEntry.getPublishDate())) {
																assetPublishedDate = DateUtil.getDate(assetEntry.getPublishDate(), "MMM dd, yyyy",
																		locale);

															}
										%>
										<%=assetPublishedDate%>
										<div class="tag-new d-none">
											<span>New</span>
										</div>
										<%
											if (!categoryName.isEmpty()) {
										%>
										<div class="item-tags">
											<span> <%=categoryName%>
											</span>
										</div>
										<%
											}
										%>
									</div>
									<%
										}
									%>

									<div class="Search-result-description">
										<%
											String assetURL = StringPool.BLANK;
														if ("com.liferay.journal.model.JournalArticle"
																.equalsIgnoreCase(document.get(Field.ENTRY_CLASS_NAME))) {
															assetURL = assetEntry.getAssetRenderer().getURLViewInContext(liferayPortletRequest, liferayPortletResponse, assetURL);
																try{
																	JournalArticle journalArticle = JournalArticleLocalServiceUtil.fetchLatestArticle(Long.parseLong(document.get(Field.ENTRY_CLASS_PK)));
																	assetURL = CustomSearchSuggestionUtil.getRedirectionURLInSearchResults(assetCategories, themeDisplay, journalArticle, assetURL);
																} catch(Exception nja){
																	if(assetURL.isEmpty()){
																		assetURL = "/web" + themeDisplay.getScopeGroup().getFriendlyURL() + "/-/" + document.get(Field.getLocalizedName(Locale.US, "urlTitle"));
																	}
																}
														} else if ("com.liferay.document.library.kernel.model.DLFileEntry"
																.equalsIgnoreCase(document.get(Field.ENTRY_CLASS_NAME))) {
															assetURL = assetEntry.getAssetRenderer().getURLDownload(themeDisplay)
																	.replace(assetEntry.getClassUuid(), "").replace("&download=true", "");
															//assetURL=assetEntry.getAssetRenderer().getURLImagePreview(renderRequest);
														} else {
															Layout assetLayout = LayoutLocalServiceUtil.getLayout(assetEntry.getClassPK());
															assetURL = "/web" + assetLayout.getGroup().getFriendlyURL()
																	+ assetLayout.getFriendlyURL(locale);
														}
										%>
										
										<% if(assetURL.contains("https://www.youtube.com")) { %>
										
										<a href="javascript:void(0);" data-toggle="modal" data-target="#videoPopUp-<%=index%>" class="mtm_list_item_heading" >
											<div
												class="global-search-result-heading c-tooltips position-relative">
												<span class="mtm_list_item_heading search-result-title">
													<%=document.get(themeDisplay.getLocale(), Field.TITLE)%>
												</span>
												<div class="custom-tooltip global-search-tooltip">
													<div class="tooltip-arrow-up"></div>
													<div class="tooltip-content search-result-content">
														<%=assetEntry.getSummary(locale)%></div>
												</div>
												<div style="display: none"><%=assetEntry.getSummary(locale)%></div>
											</div>
										</a>
										
										<!--video modal popup start -->
													<div class="modal fade video-overlay youtube-video-popup" id="videoPopUp-<%=index%>" role="dialog" style="display:none;">
														<div class="modal-dialog">
															<!-- Modal content-->
															<div class="modal-content">
																<div class="closePopUp-wrapper" data-id="videoPopUp-<%=index%>"> <img src="<%=images_folder%>/rbi-main/icon-cross-search.svg" alt="close the popup" title="close the popup" data-dismiss="modal"> </div>
																<div class="modal-body">
																	<div class="youtube-video-overlay-content ">
																		<!--You tube video area-->
																		<div class="row no-gutters">
																			<div class="col-12"> <iframe width="100%" height="400" data-src="<%=assetURL%>" title="YouTube video player" frameborder="0" allowfullscreen> </iframe> </div>
																			<div class="col-12 d-none">
																				<div class="caption-for-video">
																					<p class="video-caption-txt">Governorâs message on Digital Payments</p>
																				</div>
																			</div>
																		</div>
																	</div>
																	<!--You tube video area-->
																</div>
															</div>
														</div>
													</div> 
										<!-- Modal videoPopUp end -->
										
										<% } else if(assetURL.contains(".mp3")) { %>
										
										<a href="javascript:void(0);" data-toggle="modal" data-target="#audioPopUp-<%=index%>" class="mtm_list_item_heading" >
											<div
												class="global-search-result-heading c-tooltips position-relative">
												<span class="mtm_list_item_heading search-result-title">
													<%=document.get(themeDisplay.getLocale(), Field.TITLE)%>
												</span>
												<div class="custom-tooltip global-search-tooltip">
													<div class="tooltip-arrow-up"></div>
													<div class="tooltip-content search-result-content">
														<%=assetEntry.getSummary(locale)%></div>
												</div>
												<div style="display: none"><%=assetEntry.getSummary(locale)%></div>
											</div>
										</a>
										
										<!--audio modal popup start -->
													<div class="modal fade video-overlay audio-popup" id="audioPopUp-<%=index%>" role="dialog" style="display:none;" data-mp3="<%=assetURL%>?audioPreview=1&type=mp3">
														<div class="modal-dialog">
															<!-- Modal content-->
															<div class="modal-content">
																<!-- <div class="closePopUp-wrapper"> <img src="<%=images_folder%>/rbi-main/icon-cross-search.svg"
																					alt="close the popup" title="close the popup" data-dismiss="modal"> </div> -->
																<div class="modal-body">
																	<!--audio player area-->
																	<div class="audio-overlay-content">
																		<div class="row no-gutters">
																			<div class="col-12">
																				<!--audio popup HTML code-->
                                                                                <div id="audioPlayer" class="audio-player" style="display: none;">
                                                                                    <div class="audioPlayer-loader">
                                                                                        <strong>LOADING...</strong>
                                                                                    </div>
                                                                                    <div class="audioPlayer-container d-none">
                                                                                        <img data-dismiss="modal" class="close template-custom-close-button" src="<%=images_folder%>/rbi-main/audio_close.svg" alt="close">
                                                                                        <div class="audio-top-section">
                                                                                            <span id="current-time" class="time"></span>
                                                                                            <audio src="" preload="metadata"></audio>
                                                                                            <input type="range" id="seek-slider" class="timeline" max="169"
                                                                                                value="0"> <span id="duration" class="time"></span>
                                                                                        </div>
                                                                                        <div class="audio-bottom-section dropup" id="dropdownCustom">
                                                                                            <button class="player-button shift-btn">
                                                                                                <img src="<%=images_folder%>/rbi-main/play_new.svg" alt="play">
                                                                                            </button>
                                                                                            <div class="volume-section">
                                                                                                <div class="volume-slider-bar">
                                                                                                    <div class="volume-bar">
                                                                                                        <div class="volume-percentage"></div>
                                                                                                    </div>
                                                                                                </div>
                                                                                                <button class="sound-button">
                                                                                                    <img src="<%=images_folder%>/rbi-main/audio_full.svg" alt="sound">
                                                                                                </button>
                                                                                            </div>
                                                                                            <button class="playback-button" onclick="downloadMP3()" style="font-size: large;">
                                                                                                <img src="<%=images_folder%>/rbi-main/audio_download.svg" alt="download">
                                                                                            </button>
                                                                                            <button class="player-button dropdownEvent audio-speed" id="triangleShown"
                                                                                                data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                                                                                <div class="triangle"></div>
                                                                                                <img src="<%=images_folder%>/rbi-main/audio_speed.svg"
                                                                                                    alt="playback speed">
                                                                                                <ul class="dropdown-menu" id="dropdownCustomList">
                                                                                                    <li class="drop-text dropdown-item" data-speed="0.25">0.25</li>
                                                                                                    <li class="drop-text dropdown-item" data-speed="0.50">0.50</li>
                                                                                                    <li class="drop-text dropdown-item" data-speed="0.75">0.75</li>
                                                                                                    <li class="drop-text dropdown-item" data-speed="Normal"><%=LanguageUtil.get(locale,"AUDIO_NORMAL")%></li>
                                                                                                    <li class="drop-text dropdown-item" data-speed="1.25">1.25</li>
                                                                                                    <li class="drop-text dropdown-item" data-speed="1.5">1.5</li>
                                                                                                    <li class="drop-text dropdown-item" data-speed="1.75">1.75</li>
                                                                                                    <li class="drop-text dropdown-item" data-speed="2">2</li>
                                                                                                </ul>
                                                                                            </button>
                                                                                        </div>
                                                                                    </div>
                                                                                </div>
                                                                                
                                                                            </div>
																		</div>
																	</div>
																	<!--audio area-->
																</div>
															</div>
														</div>
													</div> 
										<!-- Modal auduioPopUp end -->
										
										<% } else { %>
										
										<a href="<%=assetURL%>" class="mtm_list_item_heading">
											<div
												class="global-search-result-heading c-tooltips position-relative">
												<span class="mtm_list_item_heading search-result-title">
													<%=document.get(themeDisplay.getLocale(), Field.TITLE)%>
												</span>
												<div class="custom-tooltip global-search-tooltip">
													<div class="tooltip-arrow-up"></div>
													<div class="tooltip-content search-result-content">
														<%=assetEntry.getSummary(locale)%></div>
												</div>
												<div style="display: none"><%=assetEntry.getSummary(locale)%></div>
											</div>
										</a>
										
										<% } %>
										
									</div>
								</div>
							</div>
							<%
								} catch (Exception e) {
							%>
							<%=document.get(themeDisplay.getLocale(), Field.TITLE)%>

							<%
								}
									}
							%>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="more_results">
			<a href='<%=url%>'> <liferay-ui:message
						key="click-here-for-results-on-x"
						arguments="<%=suggestedKeyword.trim()%>" />
			</a>

		</div>
	</div>
</div>
<style type="text/css">
.portlet-search-results {
	display: none !important;
}

.filter-btn {
	display: none !important;
}

.search-bar-filter-wrapper {
	width: 100% !important;
	min-height: 55px !important;	
}
@media screen and (max-width:768px) {
	.search-bar-filter-wrapper{
		min-height: 40px !important;
	}
}
.search_guide {
	display: none !important;
}

.outer-search-bar-wrapper .readMore-wrap {
	display: none !important;
}
#filter-tags {
	display: none !important;
}

</style>
<%
	}
%>
