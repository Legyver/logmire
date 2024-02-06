package com.legyver.logmire.config;

import com.legyver.fenxlib.api.config.section.ApplicationVersionedConfigSection;
import com.legyver.fenxlib.api.config.section.ConfigPersisted;
import com.legyver.fenxlib.core.config.CoreApplicationConfig;

import java.util.Map;

public class LogmireConfig extends CoreApplicationConfig implements ApplicationVersionedConfigSection {

	@ConfigPersisted
	private FilterConfigSection filters = new FilterConfigSection();

	public LogmireConfig() {
		super();
	}

	public FilterConfigSection getFilters() {
		return filters;
	}

	public Map<String, Boolean> getPackageFilters() {
		return filters.getPackageFilters();
	}

	public void setPackageFilters(Map<String, Boolean> set) {
		filters.setPackageFilters(set);
	}

	public Map<String, Boolean> getSeverityFilters() {
		return filters.getSeverityFilters();
	}

	public void setSeverityFilters(Map<String, Boolean> set) {
		filters.setSeverityFilters(set);
	}

	public void setSeverityFilter(String severity, boolean value) {
		filters.setSeverityFilter(severity, value);
	}

	public Boolean getSeverityFilter(String severity) {
		return (Boolean) filters.getSeverityFilter(severity);
	}

}
