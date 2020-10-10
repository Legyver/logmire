package com.legyver.logmire.ui.tabs;

import com.legyver.fenxlib.core.context.ApplicationContext;
import com.legyver.logmire.config.IconConstants;
import com.legyver.logmire.ui.ApplicationUIModel;
import com.legyver.logmire.ui.bean.CausalSectionUI;
import com.legyver.logmire.ui.bean.DataSourceUI;
import com.legyver.logmire.ui.bean.LogLineUI;
import com.legyver.logmire.ui.bean.StackTraceElementUI;
import javafx.beans.property.BooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class LogViewSkin extends SkinBase<LogView> {
	private static final Logger logger  = LogManager.getLogger(LogViewSkin.class);

	private final SplitPane mainSplitPane;
	private final ListView<LogLine> logs;
	private final ObservableList<LogLine> internalList;
	private final AnchorPane detailPane;
	private final LogLineDetail logLineDetail;

	public LogViewSkin(LogView logView) {
		super(logView);
		logs = new ListView<>();
		internalList = logs.getItems();

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
		initLogs(logView, true);

		getChildren().add(mainSplitPane);
	}

	private Node filterControl(LogView logView) {
		PackageFilter packageFilter = new PackageFilter();
		packageFilter.setTooltip(new Tooltip("Hide internal packages"));
		packageFilter.hidePackageProperty().addListener((observableValue, oldValue, newValue) -> initLogs(logView, false));
		ApplicationUIModel applicationUIModel = (ApplicationUIModel) ApplicationContext.getUiModel();
		ObservableMap<String, BooleanProperty> packageFilters = applicationUIModel.getPackageFilters();
		packageFilters.entrySet().stream().forEach(entry -> {
			entry.getValue().addListener((observableValue, aBoolean, t1) -> {
				if (packageFilter.isHidePackage()) {
					initLogs(logView, false);
				}
			});
		});
		packageFilters.addListener((MapChangeListener<String, BooleanProperty>) change -> {
			String packageName = null;
			BooleanProperty enabled = null;
			if (change.wasAdded()) {
				packageName = change.getKey();
				enabled = change.getValueAdded();
			} else if (change.wasRemoved()) {
				packageName = change.getKey();
				enabled = change.getValueAdded();
			}
			initLogs(logView, false);
		});
		TextField searchField = new TextField();
		searchField.setPrefWidth(300);
		searchField.setMaxWidth(600);
		SVGControl searchControl = new SVGControl();
		searchControl.setSvgIconLibraryPrefix(IconConstants.FONTAWESOME_FREE_SOLID);
		searchControl.setSvgIcon("search");
		searchControl.setSvgIconPaint(Paint.valueOf("#68b1e3"));
//		searchControl.setSvgIconPaint(Paint.valueOf("#36423d"));
		searchControl.setSvgIconSize(15);
		searchControl.setTooltip(new Tooltip("Search"));

		ToggleControl toggleTrace = makeToggle("TRACE", logView.showTraceProperty());
		ToggleControl debugTrace = makeToggle("DEBUG", logView.showDebugProperty());
		ToggleControl infoTrace = makeToggle("INFO", logView.showInfoProperty());
		ToggleControl warningTrace = makeToggle("WARN", logView.showWarnProperty());
		ToggleControl errorTrace = makeToggle("ERROR", logView.showErrorProperty());
		ToggleControl fatalTrace = makeToggle("FATAL", logView.showFatalProperty());

		Label hideLabel = new Label("Hide");
		hideLabel.getStyleClass().add("log-menu-bar");
		VBox hideOptions = new VBox(hideLabel, packageFilter);

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

	private ToggleControl makeToggle(String text, BooleanProperty booleanProperty) {
		ToggleControl toggleControl = new ToggleControl(text);
		boolean currentValue = booleanProperty.getValue();
		booleanProperty.bind(toggleControl.engagedProperty());
		toggleControl.setEngaged(currentValue);
		toggleControl.engagedProperty().addListener((observableValue, oldValue, newValue) -> initLogs(getSkinnable(), false));
		return toggleControl;
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
				logLineDetail.getStackTraceElements().clear();
				logLineDetail.getCausalSections().clear();

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

	private void initLogs(LogView logView, boolean init) {
		DataSourceUI dataSourceUI = logView.getDataSourceUI();
		ObservableList<LogLineUI> logLines = dataSourceUI.getLines();

		dataSourceUI.acquireLock();
		if (init) {
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
				}
				;
			});
		} else {
			//rather than filtering the items, we actually re-add items every time, so we need to clear existing first
			logs.getItems().clear();
		}
		logLines.stream().forEach(logLineUI -> {
			addLine(logView, logLineUI);
		});
		dataSourceUI.releaseLock();
	}

	private void addLine(LogView logView, LogLineUI logLineUI) {
		if (severityShown(logView, logLineUI.getSeverity())
			&& !isInternalMessage(logView, logLineUI.getReporter())
		) {
			LogLine logLine = new LogLine(logLineUI);
			logs.getItems().add(logLine);
			logLine.setOnMouseClicked(new SelectableListener(logs.getItems().size() -1 , logLine, logView));
		}

	}

	private boolean isInternalMessage(LogView logView, String reporter) {
		ApplicationUIModel applicationUIModel = (ApplicationUIModel) ApplicationContext.getUiModel();
		ObservableMap<String, BooleanProperty> packageFilters = applicationUIModel.getPackageFilters();
		Optional<String> filterPackage = packageFilters.keySet().stream()
				.filter(s -> reporter != null && reporter.startsWith(s))
				.findFirst();
		return filterPackage.isPresent() ? packageFilters.get(filterPackage.get()).get(): false;
	}

	private boolean severityShown(LogView logView, String severity) {
		if (severity == null) {
			return true;
		} else {
			switch (severity) {
				case "TRACE":
					return logView.isShowTrace();
				case "DEBUG":
					return logView.isShowDebug();
				case "INFO":
					return logView.isShowInfo();
				case "WARN":
					return logView.isShowWarn();
				case "ERROR":
					return logView.isShowError();
				case "FATAL":
					return logView.isShowFatal();
				default:
					return true;
			}
		}
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
