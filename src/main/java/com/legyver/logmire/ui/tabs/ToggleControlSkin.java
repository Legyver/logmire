package com.legyver.logmire.ui.tabs;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ToggleControlSkin extends SkinBase<ToggleControl> {
	private static final Logger logger = LogManager.getLogger();
	public static final String DARK_GREY = "#36423d";
	public static final String GREEN = "#00a123";

	private final Circle circle;

	public ToggleControlSkin(ToggleControl toggleControl) {
		super(toggleControl);

		circle = new Circle(4);
		setColor(null, !toggleControl.isEngaged(), toggleControl.isEngaged());
		toggleControl.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
			toggleControl.setEngaged(!toggleControl.isEngaged());
			logger.debug(toggleControl.getText() + ": " + (toggleControl.isEngaged() ? "ON" : "OFF"));
		});
		toggleControl.engagedProperty().addListener(this::setColor);

		Label label = new Label();
		label.textProperty().bind(toggleControl.textProperty());
		label.setPrefWidth(45);
		HBox hBox = new HBox(circle, label);
		getChildren().add(hBox);
	}

	private void setColor(ObservableValue observableValue, Boolean oldValue, Boolean newValue) {
		if (newValue) {
			circle.setFill(Paint.valueOf(GREEN));
		} else {
			circle.setFill(Paint.valueOf(DARK_GREY));
		}
	}

}
