package com.legyver.logmire.ui.bean;

import com.legyver.logmire.task.openlog.LogLineAccumulator;
import com.legyver.logmire.ui.search.FilterableLogContext;
import com.legyver.logmire.ui.tabs.LogLineView;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class DataSourceUI {
	private final static Logger logger = LogManager.getLogger(DataSourceUI.class);

	public static final String SOURCE_OPTIONS_TITLE = "Logfile";
	public static final String SOURCES_LABEL = "Logfile";

	private final StringProperty sourceName = new SimpleStringProperty();
	private final StringProperty sourcePath = new SimpleStringProperty();
	private final ObjectProperty<File> source = new SimpleObjectProperty<>();
	private final ObservableList<LogLineUI> lines = FXCollections.observableArrayList();
	private final List<LogLineUI> unsynced = new ArrayList<>();
	private final Semaphore mutex = new Semaphore(1);
	private final FilterableLogContext filterableLogContext;

	private LogLineAccumulator logLineAccumulator;
	private LogLineUI current;

	public DataSourceUI(File file) {
		source.set(file);
		sourcePath.setValue(file.getAbsolutePath());
		sourceName.setValue(file.getName());
		logLineAccumulator = new LogLineAccumulator();
		filterableLogContext = new FilterableLogContext(this);
//		lines.addListener((ListChangeListener<LogLineUI>) c -> {
//			acquireLock();
//			if (c.next()) {
//				if (c.wasAdded()) {
//					for (LogLineUI logLineUI : c.getAddedSubList()) {
//						LogLineView logLineView = logLineUI.getLogLineView();
//						if (logLineView == null) {
//							unsynced.add(logLineUI);
//						} else {
//							filterableLogContext.getAllItems().add(logLineView);
//						}
//					}
//					for (LogLineUI logLineUI : unsynced) {
//						LogLineView logLineView = logLineUI.getLogLineView();
//						if (logLineView != null) {
//							filterableLogContext.getAllItems().add(logLineView);
//						}
//					}
//				}
//				//TODO: handle log rollover
//			}
//			releaseLock();
//		});
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

	public FilterableLogContext getFilterableLogContext() {
		return filterableLogContext;
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

	public boolean acquireLock() {
		try {
			mutex.acquire();
			return true;
		} catch (InterruptedException e) {
			logger.error("Error acquiring lock", e);
			return false;
		}
	}

	public void releaseLock() {
		mutex.release();
	}

	public void addLine(String line) {
		LogLineUI current = logLineAccumulator.addLine(line);
		//check to see if the same instance
		if (this.current != current) {
			acquireLock();
			//we want to eagerly add lines, so we ensure the last line is always available and not waiting for a new line
			lines.add(current);
			this.current = current;
			releaseLock();
		}

	}

	public ObservableList<LogLineUI> getLines() {
		return lines;
	}
}
