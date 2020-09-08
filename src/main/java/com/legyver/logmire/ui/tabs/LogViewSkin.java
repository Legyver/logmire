package com.legyver.logmire.ui.tabs;

import com.legyver.fenxlib.core.factory.SvgIconFactory;
import com.legyver.logmire.ui.bean.CausalSectionUI;
import com.legyver.logmire.ui.bean.DataSourceUI;
import com.legyver.logmire.ui.bean.LogLineUI;
import com.legyver.logmire.ui.bean.StackTraceElementUI;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogViewSkin extends SkinBase<LogView> {
	private static final Logger logger  = LogManager.getLogger(LogViewSkin.class);

	private final SplitPane mainSplitPane;
	private final ListView<LogLine> logs;
	private final AnchorPane detailPane;
	private final LogLineDetail logLineDetail;

	public LogViewSkin(LogView logView) {
		super(logView);
		logs = new ListView<>();

		BorderPane logPane = new BorderPane();
		logPane.setTop(filterControl(logView));
		logPane.setCenter(logs);

		detailPane = new AnchorPane();
		mainSplitPane = new SplitPane(logPane, detailPane);

		logLineDetail = new LogLineDetail();
		logLineDetail.setVisible(false);
		detailPane.getChildren().add(logLineDetail);
		AnchorPane.setLeftAnchor(logLineDetail, 0.0);
		AnchorPane.setRightAnchor(logLineDetail, 0.0);
		AnchorPane.setTopAnchor(logLineDetail, 0.0);
		AnchorPane.setBottomAnchor(logLineDetail, 0.0);

		initFocusLogListener(logView);
		initLogs(logView);

		getChildren().add(mainSplitPane);
	}

	private Node filterControl(LogView logView) {
		CheckBox checkBoxInternals = new CheckBox("Hide Internals");
		Group checkboxGroup = new Group(checkBoxInternals);

		TextField searchField = new TextField();
		searchField.setPrefWidth(300);
		searchField.setMaxWidth(600);
		SVGControl searchControl = new SVGControl();
		searchControl.setSvgIcon("search");
		searchControl.setSvgIconPaint(Paint.valueOf("#68b1e3"));
		searchControl.setSvgIconSize(15);
		searchControl.setTooltip(new Tooltip("Search"));

		ToggleControl toggleTrace = new ToggleControl("TRACE");
		ToggleControl debugTrace = new ToggleControl("DEBUG");
		ToggleControl infoTrace = new ToggleControl("INFO");
		ToggleControl warningTrace = new ToggleControl("WARN");
		ToggleControl errorTrace = new ToggleControl("ERROR");
		ToggleControl fatalTrace = new ToggleControl("FATAL");

		Label hideLabel = new Label("Hide");
		hideLabel.getStyleClass().add("log-menu-bar");
		VBox hideOptions = new VBox(hideLabel, checkboxGroup);

		Label searchLabel = new Label("Search");
		searchLabel.getStyleClass().add("log-menu-bar");
		HBox searchBox = new HBox(searchField, searchControl);
		searchBox.setSpacing(4);
		HBox.setHgrow(searchBox, Priority.SOMETIMES);

		VBox searchOptions = new VBox(searchLabel, searchBox);


		Label severityLabel = new Label("Severity");
		severityLabel.getStyleClass().add("log-menu-bar");
		HBox severityFilterBox = new HBox(toggleTrace, debugTrace, infoTrace, warningTrace, errorTrace, fatalTrace);
		HBox.setHgrow(severityFilterBox, Priority.NEVER);
		VBox severityOptions = new VBox(severityLabel, severityFilterBox);

		HBox hbox = new HBox(hideOptions, new Separator(Orientation.VERTICAL), searchOptions, new Separator(Orientation.VERTICAL), severityOptions);
		hbox.setPadding(new Insets(10));
		hbox.setSpacing(5);
		HBox.setHgrow(searchOptions, Priority.SOMETIMES);

		return hbox;
	}

	private void initFocusLogListener(LogView logView) {
		logView.focusLogLineProperty().addListener((observableValue, oldValue, newValue) -> {
			if (newValue == null) {
				logLineDetail.setVisible(false);
				logLineDetail.getStackTraceElements().clear();
				//not worth resetting the other values
			} else {
				logLineDetail.setVisible(true);
				logLineDetail.setDate(newValue.getDate());
				logLineDetail.setExecutor(newValue.getExecutor());
				logLineDetail.setFullInfo(newValue.getPlainText());
				logLineDetail.setSeverity(newValue.getSeverity());
				logLineDetail.setFullMessage(newValue.getLongMessage());
				logLineDetail.setCopyableMessage(newValue.getShortMessage());
				logLineDetail.setReporter(newValue.getReporter());
				logLineDetail.setTimestamp(newValue.getTimestamp());
				logLineDetail.setRootError(newValue.getRootError());
				logLineDetail.setRootLocation(newValue.getRootLocation());

				newValue.acquireLock();
				newValue.getStackTraceElements().stream().forEach(stackTraceElementUI -> logLineDetail.getStackTraceElements().add(stackTraceElementUI));
				newValue.getStackTraceElements().addListener((ListChangeListener<StackTraceElementUI>) change -> {
					if (change.next()) {
						if (change.wasAdded()) {
							newValue.getStackTraceElements().stream().forEach(stackTraceElementUI -> logLineDetail.getStackTraceElements().add(stackTraceElementUI));
						}
					}
				});

				newValue.getCausalStackTraceSections().stream().forEach(causalSectionUI -> logLineDetail.getCausalSections().add(causalSectionUI));
				newValue.getCausalStackTraceSections().addListener((ListChangeListener<CausalSectionUI>) change -> {
					if (change.next()) {
						if (change.wasAdded()) {
							newValue.getCausalStackTraceSections().stream().forEach(causalSectionUI -> logLineDetail.getCausalSections().add(causalSectionUI));
						}
					}
				});
				newValue.releaseLock();

			}
		});


	}

	private void initLogs(LogView logView) {
		DataSourceUI dataSourceUI = logView.getDataSourceUI();
		ObservableList<LogLineUI> logLines = dataSourceUI.getLines();

		dataSourceUI.acquireLock();
		logLines.addListener((ListChangeListener<LogLineUI>) change -> {
			if (change.next()) {
				if (change.wasAdded()) {
					for (LogLineUI added : change.getAddedSubList()) {
						addLine(logView, added);
					}
				} else {
					//assume file was rolled-over
					logs.getItems().clear();
				}
			};
		});
		logLines.stream().forEach(logLineUI -> {
			addLine(logView, logLineUI);
		});
		dataSourceUI.releaseLock();
	}

	private void addLine(LogView logView, LogLineUI logLineUI) {
		LogLine logLine = new LogLine(logLineUI);
		logs.getItems().add(logLine);
		logs.getSelectionModel().setSelectionMode(null);
		logLine.setOnMouseClicked(new SelectableListener(logs.getItems().size() -1 , logLine, logView));
	}

	private class SelectableListener implements EventHandler<MouseEvent> {
		private final int index;
		private final LogLine logLine;
		private final LogView logView;

		private SelectableListener(int index, LogLine logLine, LogView logView) {
			this.index = index;
			this.logLine = logLine;
			this.logView = logView;
		}

		@Override
		public void handle(MouseEvent mouseEvent) {
			logView.setFocusLogLine(logLine.getValue());
			logs.getSelectionModel().clearSelection();
			logs.getSelectionModel().select(index);
//			logs.getFocusModel().focus(index);
		}
	}
}
