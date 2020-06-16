package com.legyver.logmire.config;

import com.legyver.fenxlib.tuktukfx.config.TukTukFxApplicationOptions;
import com.legyver.logmire.ui.ApplicationUIModel;
import javafx.stage.Stage;

import java.io.IOException;

public class LogmireApplicationOptions extends TukTukFxApplicationOptions<ApplicationUIModel> {
	public LogmireApplicationOptions(Stage primaryStage) throws IOException, IllegalAccessException {
		super("Logmire", map -> new LogmireConfig(map), primaryStage, new ApplicationUIModel());
	}
}
