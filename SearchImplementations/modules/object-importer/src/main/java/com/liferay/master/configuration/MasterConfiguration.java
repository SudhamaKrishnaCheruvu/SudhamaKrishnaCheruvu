package com.liferay.master.configuration;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import aQute.bnd.annotation.metatype.Meta;

/**
 * @author krishna
 *
 */
@ExtendedObjectClassDefinition(category = "edelweiss-configurations", scope = ExtendedObjectClassDefinition.Scope.COMPANY)
@Meta.OCD(id = "com.liferay.master.configuration.MasterConfiguration", name = "Backup Master Configuration", localization = "content/Language")
public interface MasterConfiguration {

	@Meta.AD(required = false, deflt = "0 0 0 * * ?", description = "Objects  backup will be taken based on the given cron expression", name = "Object Cron Expression")
	public String getObjectCRONExp();

	@Meta.AD(required = false, deflt = "0 0 0 * * ?", description = "Pick List backup will be taken based on the given cron expression", name = "PickList Cron Expression")
	public String getPickListCRONExp();

	@Meta.AD(required = false, name = "Show Object Actions")
	public boolean getShowActions();

	@Meta.AD(required = false, deflt = "", description = "Base 64 encoded value of admin creds to access Pick", name = "Base 64 User Cred")
	public String getBase64AuthCode();

}
