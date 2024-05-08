package com.custom.search.suggestion.helper;

import com.liferay.portal.kernel.search.Document;

import java.util.List;

public class CustomSuggestionResourceResponse {
	
	private List<Document> hitDocuments;
	private String redirectURL; 
	private String suggestedKeyword;
	
	public List<Document> getHitDocuments() {
		return hitDocuments;
	}
	public void setHitDocuments(List<Document> hitDocuments) {
		this.hitDocuments = hitDocuments;
	}
	public String getRedirectURL() {
		return redirectURL;
	}
	public void setRedirectURL(String redirectURL) {
		this.redirectURL = redirectURL;
	}
	public String getSuggestedKeyword() {
		return suggestedKeyword;
	}
	public void setSuggestedKeyword(String suggestedKeyword) {
		this.suggestedKeyword = suggestedKeyword;
	}
	
	public CustomSuggestionResourceResponse(List<Document> hitDocuments, String redirectURL, String suggestedKeyword) {
		super();
		this.hitDocuments = hitDocuments;
		this.redirectURL = redirectURL;
		this.suggestedKeyword = suggestedKeyword;
	}
	
}
