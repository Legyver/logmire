package com.legyver.logmire.config;

import com.legyver.fenxlib.core.config.GsonApplicationConfig;
import com.legyver.util.mapqua.mapbacked.MapBackedEntityCollection;
import com.legyver.util.mapqua.mapbacked.MapBackedMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogmireConfig extends GsonApplicationConfig {

	private final MapBackedMap packageFilters;
	private final MapBackedMap severityFilters;

	public LogmireConfig(Map map) {
		super(map);
		packageFilters = new MapBackedMap(map, "packageFilters");
		severityFilters = new MapBackedMap(map, "severityFilters");
	}

	public Map<String, Boolean> getPackageFilters() {
		return packageFilters.get();
	}

	public void setPackageFilters(Map<String, Boolean> set) {
		packageFilters.set(set);
	}

	public Map<String, Boolean> getSeverityFilters() {
		return severityFilters.get();
	}

	public void setSeverityFilters(Map<String, Boolean> set) {
		severityFilters.set(set);
	}

	public void setSeverityFilter(String severity, boolean value) {
		severityFilters.put(severity, value);
	}

	public Boolean getSeverityFilter(String severity) {
		return (Boolean) severityFilters.get(severity);
	}

}
