package com.legyver.logmire.task.openlog;

import com.legyver.core.exception.CoreException;
import com.legyver.logmire.config.BindingFactory;
import com.legyver.logmire.task.TaskFactory;
import com.legyver.logmire.ui.bean.DataSourceUI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchService;

public class OpenLogfileProcessor {
	private static final Logger logger = LogManager.getLogger(OpenLogfileProcessor.class);

	private final TaskFactory taskFactory;
	private final BindingFactory bindingFactory;

	public OpenLogfileProcessor(TaskFactory taskFactory, BindingFactory bindingFactory) {
		this.taskFactory = taskFactory;
		this.bindingFactory = bindingFactory;
	}

	public DataSourceUI onNewLogfileSelected(File logFile) {
		DataSourceUI dataSource = new DataSourceUI(logFile);

		try {
			try (BufferedReader in = new BufferedReader(new FileReader(logFile))) {
				String line;
				while ((line = in.readLine()) != null) {
					dataSource.addLine(line);
				}
			}
			WatchService watcher = FileSystems.getDefault().newWatchService();
			logFile.toPath().getParent().register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
			bindingFactory.bindNewTab(dataSource);

		} catch (IOException e) {
			logger.error("Errors reading logfile", e);
		} catch (CoreException e) {
            logger.error("Error binding tab", e);
        }

        return dataSource;
	}
}
