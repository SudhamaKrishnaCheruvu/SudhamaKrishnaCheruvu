package com.custom.search.suggestion.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * The Class SearchHttpUtil.
 *
 * @author Dhivakar Sengottaiyan
 */
@Component(immediate = true, service = {})
public class SearchHttpUtil {

	/**
	 * Gets the complete original URL.
	 *
	 * @param httpServletRequest the http servlet request
	 * @return the complete original URL
	 */
	public static String getCompleteOriginalURL(
		HttpServletRequest httpServletRequest) {

		String requestURL = null;
		String queryString = null;

		if (HttpComponentsUtil.isForwarded(httpServletRequest)) {
			requestURL = _portal.getAbsoluteURL(
				httpServletRequest,
				(String)httpServletRequest.getAttribute(
					JavaConstants.JAVAX_SERVLET_FORWARD_REQUEST_URI));

			queryString = (String)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_SERVLET_FORWARD_QUERY_STRING);
		}
		else {
			requestURL = _portal.getAbsoluteURL(
				httpServletRequest,
				HttpComponentsUtil.getPath(
					String.valueOf(httpServletRequest.getRequestURL())));

			queryString = httpServletRequest.getQueryString();
		}

		StringBuffer sb = new StringBuffer();

		sb.append(requestURL);

		if (queryString != null) {
			sb.append(StringPool.QUESTION);
			sb.append(queryString);
		}

		String proxyPath = _portal.getPathProxy();

		if (Validator.isNotNull(proxyPath)) {
			int x =
				sb.indexOf(Http.PROTOCOL_DELIMITER) +
					Http.PROTOCOL_DELIMITER.length();

			int y = sb.indexOf(StringPool.SLASH, x);

			sb.insert(y, proxyPath);
		}

		String completeURL = sb.toString();

		if (httpServletRequest.isRequestedSessionIdFromURL()) {
			HttpSession httpSession = httpServletRequest.getSession();

			String sessionId = httpSession.getId();

			completeURL = _portal.getURLWithSessionId(completeURL, sessionId);
		}

		if (_log.isWarnEnabled() && completeURL.contains("?&")) {
			_log.warn("Invalid URL " + completeURL);
		}

		return completeURL;
	}

	/**
	 * Sets the portal.
	 *
	 * @param portal the new portal
	 */
	@Reference(unbind = "-")
	protected void setPortal(Portal portal) {
		_portal = portal;
	}

	private static final Log _log = LogFactoryUtil.getLog(SearchHttpUtil.class);

	private static Portal _portal;

}