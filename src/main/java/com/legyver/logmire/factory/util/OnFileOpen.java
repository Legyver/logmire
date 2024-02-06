package com.legyver.logmire.factory.util;

import com.legyver.core.function.ThrowingConsumer;
import com.legyver.fenxlib.api.files.FileOptions;
import com.legyver.logmire.task.openlog.OpenLogfileProcessor;
import com.legyver.logmire.ui.ApplicationUIModel;
import com.legyver.logmire.ui.bean.DataSourceUI;

import java.io.File;
import java.util.Optional;

public class OnFileOpen implements ThrowingConsumer<FileOptions> {
	private final ApplicationUIModel uiModel;
	private final OpenLogfileProcessor importProcessor;

	public OnFileOpen(ApplicationUIModel uiModel, OpenLogfileProcessor importProcessor) {
		this.uiModel = uiModel;
		this.importProcessor = importProcessor;
	}

	@Override
	public void accept(FileOptions fileOptions) {
		File file = fileOptions.getFile();
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
