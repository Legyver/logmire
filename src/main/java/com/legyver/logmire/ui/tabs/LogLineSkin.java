package com.legyver.logmire.ui.tabs;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextAlignment;

public class LogLineSkin extends SkinBase<LogLine> {
	private final HBox mainBox;
	private final Label text;

	public LogLineSkin(LogLine logLine) {
		super(logLine);
		mainBox = new HBox();
		mainBox.setAlignment(Pos.BASELINE_LEFT);

		text = new Label(logLine.getTruncated());
		text.setTextAlignment(TextAlignment.LEFT);
		mainBox.getChildren().add(text);

		getChildren().add(mainBox);
	}
}
