package com.legyver.logmire.ui.tabs;

import com.legyver.logmire.ui.bean.DataSourceUI;
import com.legyver.logmire.ui.bean.LogLineUI;
import com.legyver.logmire.ui.bean.StackTraceElementUI;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ListView;
import javafx.scene.control.SkinBase;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogViewSkin extends SkinBase<LogView> {
	private static final Logger logger  = LogManager.getLogger(LogViewSkin.class);

	private final SplitPane mainSplitPane;
	private final ListView<LogLine> logs;
	private final AnchorPane detailPane;
	private final LogLineDetail logLineDetail;

	public LogViewSkin(LogView logView) {
		super(logView);
		logs = new ListView<>();
		detailPane = new AnchorPane();
		mainSplitPane = new SplitPane(logs, detailPane);

		logLineDetail = new LogLineDetail();
		logLineDetail.setVisible(false);
		detailPane.getChildren().add(logLineDetail);
		AnchorPane.setLeftAnchor(logLineDetail, 0.0);
		AnchorPane.setRightAnchor(logLineDetail, 0.0);
		AnchorPane.setTopAnchor(logLineDetail, 0.0);
		AnchorPane.setBottomAnchor(logLineDetail, 0.0);

		initFocusLogListener(logView);
		initLogs(logView);

		getChildren().add(mainSplitPane);
	}

	private void initFocusLogListener(LogView logView) {
		logView.focusLogLineProperty().addListener((observableValue, oldValue, newValue) -> {
			if (newValue == null) {
				logLineDetail.setVisible(false);
				logLineDetail.getStackTraceElements().clear();
				//not worth resetting the other values
			} else {
				logLineDetail.setVisible(true);
				logLineDetail.setDate(newValue.getDate());
				logLineDetail.setExecutor(newValue.getExecutor());
				logLineDetail.setFullInfo(newValue.getPlainText());
				logLineDetail.setSeverity(newValue.getSeverity());
				logLineDetail.setFullMessage(newValue.getLongMessage());
				logLineDetail.setCopyableMessage(newValue.getShortMessage());
				logLineDetail.setReporter(newValue.getReporter());
				logLineDetail.setTimestamp(newValue.getTimestamp());
				logLineDetail.setRootError(newValue.getRootError());
				logLineDetail.setRootLocation(newValue.getRootLocation());

				newValue.acquireLock();
				newValue.getStackTraceElements().stream().forEach(stackTraceElementUI -> logLineDetail.getStackTraceElements().add(stackTraceElementUI));
				newValue.getStackTraceElements().addListener((ListChangeListener<StackTraceElementUI>) change -> {
					if (change.next()) {
						if (change.wasAdded()) {
							newValue.getStackTraceElements().stream().forEach(stackTraceElementUI -> logLineDetail.getStackTraceElements().add(stackTraceElementUI));
						}
					}
				});

				newValue.releaseLock();
			}
		});


	}

	private void initLogs(LogView logView) {
		DataSourceUI dataSourceUI = logView.getDataSourceUI();
		ObservableList<LogLineUI> logLines = dataSourceUI.getLines();

		dataSourceUI.acquireLock();
		logLines.addListener((ListChangeListener<LogLineUI>) change -> {
			if (change.next()) {
				if (change.wasAdded()) {
					for (LogLineUI added : change.getAddedSubList()) {
						addLine(logView, added);
					}
				} else {
					//assume file was rolled-over
					logs.getItems().clear();
				}
			};
		});
		logLines.stream().forEach(logLineUI -> {
			addLine(logView, logLineUI);
		});
		dataSourceUI.releaseLock();
	}

	private void addLine(LogView logView, LogLineUI logLineUI) {
		LogLine logLine = new LogLine(logLineUI);
		logs.getItems().add(logLine);
		logs.getSelectionModel().setSelectionMode(null);
		logLine.setOnMouseClicked(new SelectableListener(logs.getItems().size() -1 , logLine, logView));
	}

	private class SelectableListener implements EventHandler<MouseEvent> {
		private final int index;
		private final LogLine logLine;
		private final LogView logView;

		private SelectableListener(int index, LogLine logLine, LogView logView) {
			this.index = index;
			this.logLine = logLine;
			this.logView = logView;
		}

		@Override
		public void handle(MouseEvent mouseEvent) {
			logView.setFocusLogLine(logLine.getValue());
			logs.getSelectionModel().clearSelection();
			logs.getSelectionModel().select(index);
//			logs.getFocusModel().focus(index);
		}
	}
}
