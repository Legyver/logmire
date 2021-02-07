package com.legyver.logmire.config;

import com.legyver.core.exception.CoreException;
import com.legyver.fenxlib.widgets.about.AboutPageOptions;

import java.util.Properties;

public class LogmireVersionInfo {
	private final AboutPageOptions aboutPageOptions;

	public LogmireVersionInfo() throws CoreException {
		aboutPageOptions = new AboutPageOptions.Builder(getClass())
				.dependenciesFile("licenses/license.properties")
				.buildPropertiesFile("buildlabel.properties")
				.copyrightPropertiesFile("licenses/copyright.properties")
				.title("Logmire")
				.intro("An logfile monitoring desktop client")
				.build();
	}

	public AboutPageOptions getAboutPageOptions() {
		return aboutPageOptions;
	}

	public Properties getBuildProperties() {
		return aboutPageOptions.getBuildProperties();
	}
}
