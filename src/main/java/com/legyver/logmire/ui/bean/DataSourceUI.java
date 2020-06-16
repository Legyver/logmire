package com.legyver.logmire.ui.bean;

import javafx.beans.property.*;

import java.io.File;

public class DataSourceUI {
	public static final String SOURCE_OPTIONS_TITLE = "Logfile";
	public static final String SOURCES_LABEL = "Logfile";

	private final StringProperty sourceName = new SimpleStringProperty();
	private final StringProperty sourcePath = new SimpleStringProperty();
	private final ObjectProperty<File> source = new SimpleObjectProperty<>();

	public DataSourceUI(File file) {
		source.set(file);
		sourcePath.setValue(file.getAbsolutePath());
		sourceName.setValue(file.getName());
	}

	public String getSourceName() {
		return sourceName.get();
	}

	//Read-only because it should always hold the simple name the file
	public ReadOnlyStringProperty sourceNameProperty() {
		return sourceName;
	}


	public String getSourcePath() {
		return sourcePath.get();
	}

	public ReadOnlyStringProperty sourcePathProperty() {
		return sourcePath;
	}

	public File getSource() {
		return source.get();
	}

	public ObjectProperty<File> sourceProperty() {
		return source;
	}

	public void setSource(File source) {
		this.source.set(source);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DataSourceUI that = (DataSourceUI) o;

		return getSourceName().equals(that.getSourceName());
	}

	@Override
	public int hashCode() {
		int result = sourceName.get() != null ? sourceName.get().hashCode() : 0;
		result = 31 * result + (source != null ? source.hashCode() : 0);
		return result;
	}
}
