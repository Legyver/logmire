package com.legyver.logmire.ui.tabs;

import com.legyver.logmire.ui.bean.DataSourceUI;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SkinBase;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
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
//		logs.setCellFactory(logLineListView -> new LeftAlignedListViewCell());
		logs.setCenterShape(false);
		detailPane = new StackPane();
		mainSplitPane = new SplitPane(logs, detailPane);

		initLogs(logView);

		getChildren().add(mainSplitPane);
	}

	private void initLogs(LogView logView) {
		DataSourceUI dataSourceUI = logView.getDataSourceUI();
		ObservableList<String> logLines = dataSourceUI.getLines();

		dataSourceUI.acquireLock();
		dataSourceUI.getLines().addListener((ListChangeListener<String>) change -> {
			if (change.next()) {
				if (change.wasAdded()) {
					for (String added : change.getAddedSubList()) {
						addLine(added);
					}
				} else {
					//assume file was rolled-over
					logs.getItems().clear();
				}
			};
		});
		logLines.stream().forEach(s -> {
			addLine(s);
		});
		dataSourceUI.releaseLock();
	}

	private void addLine(String s) {
		logs.getItems().add(new LogLine(s));
	}

//	private class LeftAlignedListViewCell extends javafx.scene.control.ListCell<LogLine> {
//		@Override
//		protected void updateItem(LogLine item, boolean empty) {
//			super.updateItem(item, empty);
//			if (empty) {
//				setGraphic(null);
//			} else {
//				// Create the HBox
//				HBox hBox = new HBox();
//				hBox.setAlignment(Pos.BASELINE_LEFT);
//
//				hBox.getChildren().add(item);
//				setGraphic(hBox);
//			}
//		}
//	}
}
