package com.legyver.logmire.ui.tabs;

import com.legyver.fenxlib.core.context.ApplicationContext;
import com.legyver.logmire.ui.ApplicationUIModel;
import com.legyver.logmire.ui.bean.DataSourceUI;
import com.legyver.logmire.ui.bean.LogLineUI;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableMap;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public class LogView extends Control {
	private final DataSourceUI dataSourceUI;
	private final ObjectProperty<LogLineUI> focusLogLine = new SimpleObjectProperty<>();
	//split out the severity properties into individual properties
	private final BooleanProperty showTrace = new SimpleBooleanProperty();
	private final BooleanProperty showDebug = new SimpleBooleanProperty();
	private final BooleanProperty showInfo = new SimpleBooleanProperty();
	private final BooleanProperty showWarn = new SimpleBooleanProperty();
	private final BooleanProperty showError = new SimpleBooleanProperty();
	private final BooleanProperty showFatal = new SimpleBooleanProperty();

	public LogView(DataSourceUI dataSourceUI) {
		this.dataSourceUI = dataSourceUI;
		ApplicationUIModel uiModel = (ApplicationUIModel) ApplicationContext.getUiModel();
		initSeverityFilters(uiModel);
	}

	private void initSeverityFilters(ApplicationUIModel uiModel) {
		ObservableMap<String, BooleanProperty> severityFilters = uiModel.getSeverityFilters();
		uiModel.getSeverityFilters().keySet().stream().forEach(severity -> {
			BooleanProperty applicationProperty = severityFilters.get(severity);
			boolean currentValue = applicationProperty.getValue();
			BooleanProperty controlProperty;
			switch (severity) {
				case "TRACE": controlProperty = showTrace; break;
				case "DEBUG": controlProperty = showDebug; break;
				case "INFO": controlProperty = showInfo; break;
				case "WARN": controlProperty = showWarn; break;
				case "ERROR": controlProperty = showError; break;
				case "FATAL": controlProperty = showFatal; break;
				default: controlProperty = null;
			}
			if (controlProperty != null) {
				applicationProperty.bind(controlProperty);
				controlProperty.set(currentValue);//we want the config-loaded value to cascade to the UI, but then the flow should be from UI -> app -> config
			}
		});
	}

	public DataSourceUI getDataSourceUI() {
		return dataSourceUI;
	}

	public LogLineUI getFocusLogLine() {
		return focusLogLine.get();
	}

	public ObjectProperty<LogLineUI> focusLogLineProperty() {
		return focusLogLine;
	}

	public void setFocusLogLine(LogLineUI focusLogLine) {
		this.focusLogLine.set(focusLogLine);
	}

	public boolean isShowTrace() {
		return showTrace.get();
	}

	public BooleanProperty showTraceProperty() {
		return showTrace;
	}

	public void setShowTrace(boolean showTrace) {
		this.showTrace.set(showTrace);
	}

	public boolean isShowDebug() {
		return showDebug.get();
	}

	public BooleanProperty showDebugProperty() {
		return showDebug;
	}

	public void setShowDebug(boolean showDebug) {
		this.showDebug.set(showDebug);
	}

	public boolean isShowInfo() {
		return showInfo.get();
	}

	public BooleanProperty showInfoProperty() {
		return showInfo;
	}

	public void setShowInfo(boolean showInfo) {
		this.showInfo.set(showInfo);
	}

	public boolean isShowWarn() {
		return showWarn.get();
	}

	public BooleanProperty showWarnProperty() {
		return showWarn;
	}

	public void setShowWarn(boolean showWarn) {
		this.showWarn.set(showWarn);
	}

	public boolean isShowError() {
		return showError.get();
	}

	public BooleanProperty showErrorProperty() {
		return showError;
	}

	public void setShowError(boolean showError) {
		this.showError.set(showError);
	}

	public boolean isShowFatal() {
		return showFatal.get();
	}

	public BooleanProperty showFatalProperty() {
		return showFatal;
	}

	public void setShowFatal(boolean showFatal) {
		this.showFatal.set(showFatal);
	}

	public Skin<?> createDefaultSkin() {
		return new LogViewSkin(this);
	}
}
