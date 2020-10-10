package com.legyver.logmire.ui.tabs;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextToggleControlSkin extends SkinBase<TextToggleControl> {
	private static final Logger logger = LogManager.getLogger(TextToggleControlSkin.class);
	public static final String DARK_GREY = "#36423d";
	public static final String DARK_RED = "#660202";
	private Text text;

	public TextToggleControlSkin(TextToggleControl textToggleControl) {
		super(textToggleControl);
		text = new Text(textToggleControl.getText());
//		text.setSize(20);
		setColor(null, !textToggleControl.getShowLevel(), textToggleControl.getShowLevel());
		textToggleControl.showLevelProperty().addListener(this::setColor);

		textToggleControl.setOnMouseClicked(actionEvent -> {
			if (MouseButton.PRIMARY == actionEvent.getButton()) {
				textToggleControl.setShowLevel(!textToggleControl.getShowLevel());
			}
		});
		getChildren().add(text);
	}

	private void setColor(ObservableValue observableValue, Boolean oldValue, Boolean newValue) {
		if (newValue) {
			text.setFill(Paint.valueOf(DARK_RED));
		} else {
			text.setFill(Paint.valueOf(DARK_GREY));
		}
	}
}
