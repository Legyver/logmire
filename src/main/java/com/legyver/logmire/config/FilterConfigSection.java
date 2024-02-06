package com.legyver.logmire.config;

import com.legyver.fenxlib.api.config.section.ConfigSection;
import com.legyver.fenxlib.api.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

public class FilterConfigSection implements ConfigSection {
    private String version;
    private Map<String, Boolean> packageFilters = new HashMap<>();
    private Map<String, Boolean> severityFilters = new HashMap<>();

    @Override
    public String getVersion() {
        if (version == null) {
            version = ApplicationContext.getApplicationVersion();
        }
        return version;
    }

    @Override
    public String getSectionName() {
        return "com.legyver.logmire.filters";
    }

    public Map<String, Boolean> getPackageFilters() {
        return packageFilters;
    }

    public void setPackageFilters(Map<String, Boolean> packageFilters) {
        this.packageFilters = packageFilters;
    }

    public Map<String, Boolean> getSeverityFilters() {
        return severityFilters;
    }

    public void setSeverityFilters(Map<String, Boolean> severityFilters) {
        this.severityFilters = severityFilters;
    }

    public void setSeverityFilter(String severity, boolean value) {
        severityFilters.put(severity, value);
    }

    public Object getSeverityFilter(String severity) {
        return severityFilters.get(severity);
    }
}
