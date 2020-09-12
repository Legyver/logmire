package com.legyver.logmire.factory.util;

import com.legyver.logmire.task.openlog.OpenLogfileProcessor;
import com.legyver.logmire.ui.ApplicationUIModel;
import com.legyver.logmire.ui.bean.DataSourceUI;

import java.io.File;
import java.util.Optional;
import java.util.function.Consumer;

public class OnFileOpen implements Consumer<File> {
	private final ApplicationUIModel uiModel;
	private final OpenLogfileProcessor importProcessor;

	public OnFileOpen(ApplicationUIModel uiModel, OpenLogfileProcessor importProcessor) {
		this.uiModel = uiModel;
		this.importProcessor = importProcessor;
	}

	@Override
	public void accept(File file) {
		Optional<DataSourceUI> preexistingDataSource = uiModel.getOpenSources().stream()
				.filter(ds -> ds.getSource().getAbsolutePath().equals(file.getAbsolutePath()))
				.findFirst();
		if (preexistingDataSource.isPresent()) {
			//if it is already open, make tab active
			uiModel.setActiveSource(preexistingDataSource.get());
		} else {
			//import it and add it to tabs
			DataSourceUI dataSource = importProcessor.onNewLogfileSelected(file);
			uiModel.addSource(dataSource);
		}
	}
}
