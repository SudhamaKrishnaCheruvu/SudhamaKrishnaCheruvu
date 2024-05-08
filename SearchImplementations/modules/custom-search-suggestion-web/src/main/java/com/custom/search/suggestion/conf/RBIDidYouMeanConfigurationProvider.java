package com.custom.search.suggestion.conf;

import com.liferay.portal.kernel.settings.definition.ConfigurationBeanDeclaration;

import org.osgi.service.component.annotations.Component;

@Component(immediate = true, service = ConfigurationBeanDeclaration.class)
public class RBIDidYouMeanConfigurationProvider implements ConfigurationBeanDeclaration {

	@Override
	public Class<?> getConfigurationBeanClass() {

		return RBIDidYouMeanConfiguration.class;
	}

}
