package com.legyver.logmire.ui.tabs;

import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class LogLineDetailSkin extends SkinBase<LogLineDetail> {

	private final GridPane gridPane;

	public LogLineDetailSkin(LogLineDetail logLineDetail) {
		super(logLineDetail);
		gridPane = new GridPane();
		gridPane.setVgap(10);
		gridPane.setHgap(10);

		Label messageLabel = new Label("Message");
		TextArea message = new TextArea();
		message.setEditable(false);
		message.textProperty().bind(logLineDetail.fullMessageProperty());

		Label reporterLabel = new Label("Reporter");
		TextField reporter = new TextField();
		reporter.setEditable(false);
		reporter.textProperty().bind(logLineDetail.reporterProperty());

		Label dateLabel = new Label("Date");
		TextField date = new TextField();
		date.setEditable(false);
		date.textProperty().bind(logLineDetail.dateProperty());

		Label timestampLabel = new Label("Timestamp");
		TextField timestamp = new TextField();
		timestamp.setEditable(false);
		timestamp.textProperty().bind(logLineDetail.timestampProperty());

		Label executorLabel = new Label("Executor");
		TextField executor = new TextField();
		executor.setEditable(false);
		executor.textProperty().bind(logLineDetail.executorProperty());

		Label severityLabel = new Label("Severity");
		TextField severity = new TextField();
		severity.setEditable(false);
		severity.textProperty().bind(logLineDetail.severityProperty());

		//9-wide (label: value,copy; label: value,copy; label: value,copy)
		int row = 0;
		gridPane.addRow(row, timestampLabel, timestamp, dateLabel, date, executorLabel, executor);
		row++;
		gridPane.add(reporterLabel, 0, row);
		gridPane.add(reporter, 1, row, 3, 1);
		gridPane.add(severityLabel, 4, row);
		gridPane.add(severity, 5, row);
		row++;
		gridPane.add(messageLabel,0, row);
		gridPane.add(message, 1, row, 5, 1);

		getChildren().add(gridPane);
	}
}
