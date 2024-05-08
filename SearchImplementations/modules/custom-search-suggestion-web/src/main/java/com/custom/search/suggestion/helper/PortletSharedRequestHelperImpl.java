package com.custom.search.suggestion.helper;

import com.custom.search.suggestion.util.SearchArrayUtil;
import com.custom.search.suggestion.util.SearchHttpUtil;
import com.custom.search.suggestion.util.SearchStringUtil;
import com.liferay.portal.kernel.util.Portal;

import java.util.Optional;

import javax.portlet.RenderRequest;
import javax.portlet.ResourceRequest;
import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Dhivakar Sengottaiyan
 */
@Component(immediate = true, service = PortletSharedRequestHelper.class)
public class PortletSharedRequestHelperImpl
	implements PortletSharedRequestHelper {

	@Override
	public <T> Optional<T> getAttribute(
		String name, RenderRequest renderRequest) {

		return Optional.ofNullable(
			getAttribute(name, _getSharedHttpServletRequest(renderRequest)));
	}

	@Override
	public String getCompleteURL(RenderRequest renderRequest) {
		return SearchHttpUtil.getCompleteOriginalURL(
			portal.getHttpServletRequest(renderRequest));
	}

	@Override
	public Optional<String> getParameter(
		String name, RenderRequest renderRequest) {

		HttpServletRequest httpServletRequest = _getSharedHttpServletRequest(
			renderRequest);

		return SearchStringUtil.maybe(httpServletRequest.getParameter(name));
	}

	@Override
	public Optional<String[]> getParameterValues(
		String name, RenderRequest renderRequest) {

		HttpServletRequest httpServletRequest = _getSharedHttpServletRequest(
			renderRequest);

		return SearchArrayUtil.maybe(
			httpServletRequest.getParameterValues(name));
	}

	@Override
	public void setAttribute(
		String name, Object attributeValue, RenderRequest renderRequest) {

		HttpServletRequest httpServletRequest = _getSharedHttpServletRequest(
			renderRequest);

		httpServletRequest.setAttribute(name, attributeValue);
	}

	@SuppressWarnings("unchecked")
	protected <T> T getAttribute(
		String name, HttpServletRequest httpServletRequest) {

		return (T)httpServletRequest.getAttribute(name);
	}

	@Reference
	protected Portal portal;

	private HttpServletRequest _getSharedHttpServletRequest(
		RenderRequest renderRequest) {

		return portal.getOriginalServletRequest(
			portal.getHttpServletRequest(renderRequest));
	}

	@Override
	public String getCompleteURL(ResourceRequest resourceRequest) {
		return SearchHttpUtil.getCompleteOriginalURL(
				portal.getHttpServletRequest(resourceRequest));
	}

}