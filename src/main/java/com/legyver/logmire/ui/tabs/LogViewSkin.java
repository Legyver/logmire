package com.legyver.logmire.ui.tabs;

import com.legyver.fenxlib.core.context.ApplicationContext;
import com.legyver.fenxlib.extensions.tuktukfx.task.adapter.JavaFxAdapter;
import com.legyver.fenxlib.extensions.tuktukfx.task.exec.TaskExecutor;
import com.legyver.logmire.event.ResetType;
import com.legyver.logmire.task.indexlog.IndexTask;
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
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

public class LogViewSkin extends SkinBase<LogView> {
	private static final Logger logger  = LogManager.getLogger(LogViewSkin.class);

	private final SplitPane mainSplitPane;
	private final AnchorPane detailPane;
	private final LogList logList;
	private final LogLineDetail logLineDetail;

	public LogViewSkin(LogView logView) {
		super(logView);
		logList = new LogList(logView, logView.getFilterableLogContext());

		BorderPane logPane = new BorderPane();
		logPane.setTop(filterControl(logView));
		logPane.setCenter(logList);

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

		DataSourceUI dataSourceUI = logView.getDataSourceUI();
		dataSourceUI.acquireLock();
		ObservableList<LogLineUI> observableList = dataSourceUI.getLines();
		//we don't want to lock for the duration of the async task as that defeats the purpose
		//so create a snapshot
		List<LogLineUI> listSnapshot = observableList.stream().collect(Collectors.toList());
		IndexTask indexTask = new IndexTask(logList.getFilterableLogContext(), listSnapshot);

		Semaphore initialIndexLock = new Semaphore(0);
		JavaFxAdapter mainIndexTaskAdapter = new JavaFxAdapter(indexTask);
		mainIndexTaskAdapter.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				initialIndexLock.release();
			}
		});
		TaskExecutor.INSTANCE.submitTask(mainIndexTaskAdapter);

		observableList.addListener((ListChangeListener<LogLineUI>) c -> {
			c.next();
			if (c.wasAdded()) {
				List<LogLineUI> added = (List<LogLineUI>) c.getAddedSubList();
				JavaFxAdapter newLineIndexTask = new JavaFxAdapter(new IndexTask(logList.getFilterableLogContext(), added));

				try {
					initialIndexLock.acquire();
					newLineIndexTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
						@Override
						public void handle(WorkerStateEvent event) {
							initialIndexLock.release();
						}
					});
					TaskExecutor.INSTANCE.submitTask(mainIndexTaskAdapter);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else if (c.wasRemoved()) {
				//assume that the log file rolled over
				logView.reset(ResetType.LOGFILE_ROLLOVER);
			}
		});
		getChildren().add(mainSplitPane);
	}

	private Node filterControl(LogView logView) {
		SearchControl searchControl = new SearchControl(logView.getFilterableLogContext());
		HBox.setHgrow(searchControl, Priority.SOMETIMES);

		PackageFilter packageFilter = new PackageFilter(logView.getFilterableLogContext());
		packageFilter.setTooltip(new Tooltip("Hide internal packages"));
		logView.filterEnabledProperty().bind(packageFilter.hidePackageProperty());

		ApplicationUIModel applicationUIModel = (ApplicationUIModel) ApplicationContext.getUiModel();
		ObservableMap<String,  BooleanProperty> packageFilters = applicationUIModel.getPackageFilters();
		packageFilters.entrySet().stream().forEach(entry -> entry.getValue().addListener((observableValue, aBoolean, t1) -> logView.reset(ResetType.PACKAGE_TOGGLE)));
		packageFilters.addListener((MapChangeListener<String, BooleanProperty>) change -> logView.reset(ResetType.REPORTER_TOGGLE));

		TextToggleControl toggleTrace = filter(makeToggleControl("TRACE", logView.showTraceProperty()));
		TextToggleControl debugTrace = filter(makeToggleControl("DEBUG", logView.showDebugProperty()));
		TextToggleControl infoTrace = filter(makeToggleControl("INFO", logView.showInfoProperty()));
		TextToggleControl warningTrace = filter(makeToggleControl("WARN", logView.showWarnProperty()));
		TextToggleControl errorTrace = filter(makeToggleControl("ERROR", logView.showErrorProperty()));
		TextToggleControl fatalTrace = filter(makeToggleControl("FATAL", logView.showFatalProperty()));

		Label hideLabel = new Label("Hide");
		hideLabel.getStyleClass().add("log-menu-bar");
		VBox hideOptions = new VBox(hideLabel, packageFilter);

		Label searchLabel = new Label("Search");
		searchLabel.getStyleClass().add("log-menu-bar");
		VBox searchOptions = new VBox(searchLabel, searchControl);

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

	private TextToggleControl makeToggleControl(String text, BooleanProperty booleanProperty) {
		TextToggleControl toggleControl = new TextToggleControl(text);
		toggleControl.setPadding(new Insets(1,5,1,5));
		boolean currentValue = booleanProperty.getValue();
		booleanProperty.bind(toggleControl.showLevelProperty());
		toggleControl.setShowLevel(currentValue);
		return toggleControl;
	}

	private TextToggleControl filter(TextToggleControl textToggleControl) {
		textToggleControl.showLevelProperty().addListener((observableValue, oldValue, newValue) -> getSkinnable().reset(ResetType.SEVERITY_TOGGLE));
		return textToggleControl;
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

}
