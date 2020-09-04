package com.legyver.logmire.ui.tabs;

import com.legyver.core.exception.CoreException;
import com.legyver.fenxlib.core.factory.SvgIconFactory;
import com.legyver.fenxlib.core.factory.decorator.ButtonIconDecorator;
import com.legyver.fenxlib.core.factory.decorator.ButtonTooltipDecorator;
import com.legyver.fenxlib.core.factory.options.IconOptions;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
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

		try {

			final SvgIconFactory iconFactory = new SvgIconFactory(
					new IconOptions("file-text-o", "#68b1e3", 20, null));

			Label messageLabel = new Label("Message");
			Node message = make(new TextArea(), logLineDetail.fullMessageProperty());
			EventHandler<ActionEvent> onClickCopy = onClickCopy(logLineDetail.copyableMessageProperty());
			Button copyMessageButton = decorate("Copy top row", iconFactory, onClickCopy);

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

			gridPane.add(messageLabel, 0, row);
			gridPane.add(message, 1, row, 5, 1);
			gridPane.add(copyMessageButton, 6, row, 1, 1);//copy icon to right of message
			GridPane.setValignment(copyMessageButton, VPos.TOP);
			GridPane.setHgrow(message, Priority.ALWAYS);

		} catch (CoreException coreException) {
			boolean breakHere = true;
		} finally {
			getChildren().add(gridPane);
		}
	}

	private Button decorate(String copyMessage, SvgIconFactory iconFactory, EventHandler<ActionEvent> onClickCopy) throws CoreException {
		return new ButtonTooltipDecorator(copyMessage, new ButtonIconDecorator(onClickCopy, iconFactory)).makeNode(null);
	}

	private EventHandler<ActionEvent> onClickCopy(StringProperty property) {
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
