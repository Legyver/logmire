package com.legyver.logmire.task.openlog;

import com.legyver.logmire.config.BindingFactory;
import com.legyver.logmire.config.LogmireConfig;
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
import java.util.Properties;

public class OpenLogfileProcessor {
	private static final Logger logger = LogManager.getLogger(OpenLogfileProcessor.class);

	private final TaskFactory taskFactory;
	private final BindingFactory bindingFactory;
	private final LogmireConfig applicationConfig;
	private final Properties buildProperties;

	public OpenLogfileProcessor(TaskFactory taskFactory, BindingFactory bindingFactory, LogmireConfig applicationConfig, Properties buildProperties) {
		this.taskFactory = taskFactory;
		this.bindingFactory = bindingFactory;
		this.applicationConfig = applicationConfig;
		this.buildProperties = buildProperties;
	}

	public DataSourceUI onNewLogfileSelected(File logFile) {
		DataSourceUI dataSource = new DataSourceUI(logFile);
		bindingFactory.bindNewTab(dataSource);

		LogLineAccumulator logLineAccumulator = new LogLineAccumulator(dataSource);
		int lines = 0;
		int characters = 0;
		try {
			WatchService watcher = FileSystems.getDefault().newWatchService();
			try (BufferedReader in = new BufferedReader(new FileReader(logFile))) {
				String line;
				while ((line = in.readLine()) != null) {
					lines++;
					characters += line.length() + System.lineSeparator().length();
					logLineAccumulator.addLine(line);
				}
			}
			logFile.toPath().getParent().register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);

		} catch (IOException e) {

		}

		return dataSource;
	}
}
