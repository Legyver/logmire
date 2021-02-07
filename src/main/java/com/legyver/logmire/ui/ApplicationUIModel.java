package com.legyver.logmire.ui;

import com.legyver.fenxlib.core.impl.uimodel.RecentFileAware;
import com.legyver.fenxlib.core.impl.uimodel.FileOptions;
import com.legyver.logmire.ui.bean.DataSourceUI;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.util.List;

public class ApplicationUIModel implements RecentFileAware {
	private final ObservableList<DataSourceUI> openSources = FXCollections.observableArrayList();
	private final ObjectProperty<DataSourceUI> activeSource = new SimpleObjectProperty<>();
	private final ObservableList<FileOptions> recentFiles = FXCollections.observableArrayList();
	private final ObservableMap<String, BooleanProperty> packageFilters = FXCollections.observableHashMap();
	private final ObservableMap<String, BooleanProperty> severityFilters = FXCollections.observableHashMap();

	public void setActiveSource(DataSourceUI activeSource) {
		this.activeSource.set(activeSource);
	}

	public void addSource(DataSourceUI source) {
		openSources.add(source);
		activeSource.set(source);
	}

	public ObservableList<DataSourceUI> getOpenSources() {
		return openSources;
	}

	@Override
	public List<FileOptions> getRecentFiles() {
		return recentFiles;
	}

	public ObservableMap<String, BooleanProperty> getPackageFilters() {
		return packageFilters;
	}

	public ObservableMap<String, BooleanProperty> getSeverityFilters() {
		return severityFilters;
	}
}
