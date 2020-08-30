package com.legyver.logmire.ui.tabs;

import com.legyver.logmire.ui.bean.DataSourceUI;
import com.legyver.logmire.ui.bean.LogLineUI;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.SkinBase;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogViewSkin extends SkinBase<LogView> {
	private static final Logger logger  = LogManager.getLogger(LogViewSkin.class);

	private final SplitPane mainSplitPane;
	private final ListView<LogLine> logs;
	private final StackPane detailPane;

	public LogViewSkin(LogView logView) {
		super(logView);
		logs = new ListView<>();
		detailPane = new StackPane();
		mainSplitPane = new SplitPane(logs, detailPane);

		initLogs(logView);

		getChildren().add(mainSplitPane);
	}

	private void initLogs(LogView logView) {
		DataSourceUI dataSourceUI = logView.getDataSourceUI();
		ObservableList<LogLineUI> logLines = dataSourceUI.getLines();

		dataSourceUI.acquireLock();
		logLines.addListener((ListChangeListener<LogLineUI>) change -> {
			if (change.next()) {
				if (change.wasAdded()) {
					for (LogLineUI added : change.getAddedSubList()) {
						addLine(added);
					}
				} else {
					//assume file was rolled-over
					logs.getItems().clear();
				}
			};
		});
		logLines.stream().forEach(logLineUI -> {
			addLine(logLineUI);
		});
		dataSourceUI.releaseLock();
	}

	private void addLine(LogLineUI logLineUI) {
		logs.getItems().add(new LogLine(logLineUI));
	}

}
