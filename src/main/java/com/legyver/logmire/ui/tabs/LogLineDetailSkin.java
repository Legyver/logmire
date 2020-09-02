package com.legyver.logmire.ui.tabs;

import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class LogLineDetailSkin extends SkinBase<LogLineDetail> {

	private final GridPane gridPane;

	public LogLineDetailSkin(LogLineDetail logLineDetail) {
		super(logLineDetail);
		gridPane = new GridPane();
		gridPane.setVgap(10);
		gridPane.setHgap(10);
		gridPane.setPadding(new Insets(10));
//		gridPane.prefWidthProperty().bind(logLineDetail.parentWidthProperty());

		Label messageLabel = new Label("Message");
		Node message = make(new TextArea(), logLineDetail.fullMessageProperty());

		Label reporterLabel = new Label("Reporter");
		Node reporter = make(new TextField(), logLineDetail.reporterProperty());

		Label dateLabel = new Label("Date");
		Node date = make(new TextField(), logLineDetail.dateProperty());

		Label timestampLabel = new Label("Timestamp");
		Node timestamp = make(new TextField(), logLineDetail.timestampProperty());

		Label executorLabel = new Label("Executor");
		Node executor = make(new TextField(), logLineDetail.executorProperty());

		Label severityLabel = new Label("Severity");
		Node severity = make(new TextField(), logLineDetail.severityProperty());

		//9-wide (label: value,copy; label: value,copy; label: value,copy)
		int row = 0;
		gridPane.addRow(row, timestampLabel, timestamp, dateLabel, date, executorLabel, executor);
		GridPane.setHgrow(executor, Priority.ALWAYS);
		row++;

		gridPane.add(severityLabel, 0, row);
		gridPane.add(severity, 1, row);
		gridPane.add(reporterLabel, 2, row);
		gridPane.add(reporter, 3, row, 3, 1);
		GridPane.setHgrow(reporter, Priority.ALWAYS);
		row++;

		gridPane.add(messageLabel,0, row);
		gridPane.add(message, 1, row, 5, 1);
		GridPane.setHgrow(message, Priority.ALWAYS);

		getChildren().add(gridPane);
	}

	private Node make(TextInputControl textInputControl, StringProperty textProperty) {
		textInputControl.setEditable(false);
		textInputControl.textProperty().bind(textProperty);
		return textInputControl;
	}
}
