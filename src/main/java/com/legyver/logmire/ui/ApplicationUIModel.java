package com.legyver.logmire.ui;

import com.legyver.fenxlib.core.uimodel.DefaultFileOptions;
import com.legyver.fenxlib.core.uimodel.FileOptions;
import com.legyver.fenxlib.core.uimodel.RecentFileAware;
import com.legyver.logmire.ui.bean.DataSourceUI;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Collections;
import java.util.List;

public class ApplicationUIModel implements RecentFileAware {
	private final ObservableList<DataSourceUI> openSources = FXCollections.observableArrayList();
	private final ObjectProperty<DataSourceUI> activeSource = new SimpleObjectProperty<>();

	private ObservableList<FileOptions> recentFiles = FXCollections.observableArrayList();

	public DataSourceUI getActiveSource() {
		return activeSource.get();
	}

	public ObjectProperty<DataSourceUI> activeSourceProperty() {
		return activeSource;
	}

	public void setActiveSource(DataSourceUI activeSource) {
		this.activeSource.set(activeSource);
	}

	public void addSource(DataSourceUI source) {
		openSources.add(source);
		activeSource.set(source);
	}

	public void removeSource(DataSourceUI source)  {
		openSources.remove(source);
		if (source.equals(activeSource.get())) {
			if (openSources.size() > 1) {
				activeSource.set(openSources.get(openSources.size() - 1));
			} else {
				activeSource.set(null);
			}
		}
	}

	public ObservableList<DataSourceUI> getOpenSources() {
		return openSources;
	}

	@Override
	public List<FileOptions> getRecentFiles() {
		return recentFiles;
	}

	public void setRecentFiles(List<FileOptions> recentFiles) {
		recentFiles.stream()
				.filter(fileOptions -> !this.recentFiles.contains(fileOptions))
				.forEach(fileOptions -> this.recentFiles.add(fileOptions));
		Collections.sort(this.recentFiles);
	}

}
