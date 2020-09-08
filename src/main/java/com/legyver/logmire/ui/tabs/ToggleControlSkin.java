package com.legyver.logmire.ui.tabs;

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
	public static final String GREEN = "00a123";

	public ToggleControlSkin(ToggleControl toggleControl) {
		super(toggleControl);

		Circle circle = new Circle(4);
		circle.setFill(Paint.valueOf(DARK_GREY));
		toggleControl.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
			toggleControl.setEngaged(!toggleControl.isEngaged());
			if (toggleControl.isEngaged()) {
				circle.setFill(Paint.valueOf(GREEN));
			} else {
				circle.setFill(Paint.valueOf(DARK_GREY));
			}
			logger.debug(toggleControl.getText() + ": " + (toggleControl.isEngaged() ? "ON" : "OFF"));
		});


		Label label = new Label();
		label.textProperty().bind(toggleControl.textProperty());
		label.setPrefWidth(45);
		HBox hBox = new HBox(circle, label);
		getChildren().add(hBox);
	}
}
