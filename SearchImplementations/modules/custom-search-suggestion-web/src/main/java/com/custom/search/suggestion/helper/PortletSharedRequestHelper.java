package com.custom.search.suggestion.helper;

import java.util.Optional;

import javax.portlet.RenderRequest;
import javax.portlet.ResourceRequest;

/**
 * The Interface PortletSharedRequestHelper.
 *
 * @author Dhivakar Sengottaiyan
 */
public interface PortletSharedRequestHelper {

	/**
	 * Gets the attribute.
	 *
	 * @param <T> the generic type
	 * @param name the name
	 * @param renderRequest the render request
	 * @return the attribute
	 */
	public <T> Optional<T> getAttribute(
		String name, RenderRequest renderRequest);

	/**
	 * Gets the complete URL.
	 *
	 * @param renderRequest the render request
	 * @return the complete URL
	 */
	public String getCompleteURL(RenderRequest renderRequest);
	
	/**
	 * Gets the complete URL.
	 *
	 * @param resourceRequest the resource request
	 * @return the complete URL
	 */
	public String getCompleteURL(ResourceRequest resourceRequest);

	/**
	 * Gets the parameter.
	 *
	 * @param name the name
	 * @param renderRequest the render request
	 * @return the parameter
	 */
	public Optional<String> getParameter(
		String name, RenderRequest renderRequest);

	/**
	 * Gets the parameter values.
	 *
	 * @param name the name
	 * @param renderRequest the render request
	 * @return the parameter values
	 */
	public Optional<String[]> getParameterValues(
		String name, RenderRequest renderRequest);

	/**
	 * Sets the attribute.
	 *
	 * @param name the name
	 * @param attributeValue the attribute value
	 * @param renderRequest the render request
	 */
	public void setAttribute(
		String name, Object attributeValue, RenderRequest renderRequest);

}