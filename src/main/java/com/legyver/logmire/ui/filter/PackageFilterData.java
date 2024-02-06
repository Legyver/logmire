package com.legyver.logmire.ui.filter;

import com.legyver.fenxlib.api.context.ApplicationContext;
import com.legyver.logmire.ui.ApplicationUIModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableMap;

import java.util.Map;
import java.util.stream.Collectors;

public class PackageFilterData {
	private final BooleanProperty filterEnabled = new SimpleBooleanProperty(true);

	public boolean isFilterEnabled() {
		return filterEnabled.get();
	}

	public BooleanProperty filterEnabledProperty() {
		return filterEnabled;
	}

	public void setFilterEnabled(boolean filterEnabled) {
		this.filterEnabled.set(filterEnabled);
	}

	@Override
	public int hashCode() {
		ApplicationUIModel applicationUIModel = (ApplicationUIModel) ApplicationContext.getUiModel();
		ObservableMap<String, BooleanProperty> packageFilters = applicationUIModel.getPackageFilters();
		//smash everything together into a big string, then return the hashcode of it
		return packageFilters.entrySet().stream().map(this::mapEntry).collect(Collectors.joining("::")).hashCode();
	}

	private String mapEntry(Map.Entry<String, BooleanProperty> stringBooleanPropertyEntry) {
		return stringBooleanPropertyEntry.getKey() + "=" + stringBooleanPropertyEntry.getValue();
	}
}
