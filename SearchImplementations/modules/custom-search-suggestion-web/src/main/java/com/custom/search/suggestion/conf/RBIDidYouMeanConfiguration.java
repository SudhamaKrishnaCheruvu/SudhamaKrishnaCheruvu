package com.custom.search.suggestion.conf;

import com.custom.search.suggestion.constants.CustomSearchSuggestionConstants;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import aQute.bnd.annotation.metatype.Meta;

@ExtendedObjectClassDefinition(category = CustomSearchSuggestionConstants.DID_YOU_MEAN_CONFIGURATION, scope = ExtendedObjectClassDefinition.Scope.SYSTEM)
@Meta.OCD(id = CustomSearchSuggestionConstants.DID_YOU_MEAN_CONFIGURATION_ID, localization = "content/Language", name = CustomSearchSuggestionConstants.DID_YOU_MEAN_CONFIGURATION_KEY)
public interface RBIDidYouMeanConfiguration {

	/*
	 * @Meta.AD(deflt = "", required = false) public long
	 * monetoryPolicyDocumentsStructure();
	 * 
	 * @Meta.AD(deflt = "", required = false) public String
	 * monetoryPolicyDocumentsStructureFields();
	 */

	@Meta.AD(deflt = "liferay-20099", description = "remote-cluster-connection-id-help", name = "remote-cluster-connection-id", required = false)
	public String getShardNode();

	@Meta.AD(deflt = "10.130.2.105:9200", description = "network-host-addresses-help", name = "network-host-addresses", required = false)
	public String[] getElasticServerHosts();

	@Meta.AD(deflt = "false", description = "authentication-enabled-help", name = "authentication-enabled", required = false)
	public boolean authenticationEnabled();

	@Meta.AD(deflt = "elastic", description = "username-help", name = "username", required = false)
	public String getUsername();

	@Meta.AD(description = "password-help", name = "password", required = false, type = Meta.Type.Password)
	public String getPassword();

}