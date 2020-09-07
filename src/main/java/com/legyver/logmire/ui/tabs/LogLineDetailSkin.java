package com.legyver.logmire.ui.tabs;

import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogLineDetailSkin extends SkinBase<LogLineDetail> {
	private static final Logger logger = LogManager.getLogger(LogLineDetailSkin.class);

	private final GridPane gridPane;
	public LogLineDetailSkin(LogLineDetail logLineDetail) {
		super(logLineDetail);
		gridPane = new GridPane();
		gridPane.setVgap(10);
		gridPane.setHgap(10);
		gridPane.setPadding(new Insets(10));

		try {
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

			Label rootErrorLabel = new Label("Root error");
			Node rootError = make(new TextField(), logLineDetail.rootErrorProperty());

			Label rootLocationLabel = new Label("Root location");
			Node rootLocation = make(new TextField(), logLineDetail.rootLocationProperty());

			Label errorsLabel = new Label("Errors");

			Label stackLabel = new Label("Stack trace");


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
			copyRow(row, messageLabel, message, logLineDetail.copyableMessageProperty());

			row++;
			copyRow(row, rootErrorLabel, rootError, logLineDetail.rootErrorProperty());

			row++;
			copyRow(row, rootLocationLabel, rootLocation, logLineDetail.rootLocationProperty());

		} finally {
			getChildren().add(gridPane);
		}
	}

	private void copyRow(int row, Label label, Node node, StringProperty copyableProperty) {
		gridPane.add(label, 0, row);
		gridPane.add(node, 1, row, 5, 1);
		VBox vBox = copyVBox(onClickCopy(copyableProperty));
		gridPane.add(vBox, 6, row, 1, 1);//copy icon to right of message
		GridPane.setValignment(vBox, VPos.TOP);
		GridPane.setHgrow(node, Priority.ALWAYS);
	}

	private VBox copyVBox(EventHandler<MouseEvent> onClickCopy) {
		SVGControl svgControl = new SVGControl();
		svgControl.setSvgIcon("copy, files-o");
		svgControl.setSvgIconPaint(Paint.valueOf("#68b1e3"));
		svgControl.setSvgIconSize(20);

		svgControl.setOnMouseClicked(onClickCopy);
		Region spacer = new Region();
		VBox vBox = new VBox(svgControl, spacer);
		VBox.setVgrow(spacer, Priority.ALWAYS);
		return vBox;
	}

	private EventHandler<MouseEvent> onClickCopy(StringProperty property) {
		Clipboard clipboard = Clipboard.getSystemClipboard();
		return event -> {
			final ClipboardContent content = new ClipboardContent();
			content.putString(property.get());
			clipboard.setContent(content);
		};
	}

	private Node make(TextInputControl textInputControl, StringProperty textProperty) {
		textInputControl.setEditable(false);
		textInputControl.textProperty().bind(textProperty);
		return textInputControl;
	}
}
