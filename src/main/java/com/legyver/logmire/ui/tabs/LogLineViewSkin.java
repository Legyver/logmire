package com.legyver.logmire.ui.tabs;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextAlignment;

public class LogLineViewSkin extends SkinBase<LogLineView> {
	private final HBox mainBox;
	private final Label text;

	public LogLineViewSkin(LogLineView logLineView) {
		super(logLineView);
		mainBox = new HBox();
		mainBox.setAlignment(Pos.BASELINE_LEFT);

		text = new Label(logLineView.getTruncated());
		text.setTextAlignment(TextAlignment.LEFT);
		mainBox.getChildren().add(text);

		getChildren().add(mainBox);
	}
}
