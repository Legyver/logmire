package com.legyver.logmire.ui.filter;

import com.legyver.fenxlib.core.impl.context.ApplicationContext;
import com.legyver.logmire.ui.ApplicationUIModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableMap;

public class SeverityFilterData {
	public static final int TRACE = 1;
	public static final int DEBUG = 2;
	public static final int INFO = 4;
	public static final int WARN = 8;
	public static final int ERROR = 16;
	public static final int FATAL = 32;

	private final BooleanProperty showTrace = new SimpleBooleanProperty();
	private final BooleanProperty showDebug = new SimpleBooleanProperty();
	private final BooleanProperty showInfo = new SimpleBooleanProperty();
	private final BooleanProperty showWarn = new SimpleBooleanProperty();
	private final BooleanProperty showError = new SimpleBooleanProperty();
	private final BooleanProperty showFatal = new SimpleBooleanProperty();

	public SeverityFilterData() {
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

	@Override
	public int hashCode() {
		return hashBoolean(showTrace) * TRACE
				+ hashBoolean(showDebug) * DEBUG
				+ hashBoolean(showInfo)  * INFO
				+ hashBoolean(showWarn)  * WARN
				+ hashBoolean(showError) * ERROR
				+ hashBoolean(showFatal) * FATAL;
	}

	private int hashBoolean(BooleanProperty property) {
		return property.get() ? 1 : 0;
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
}
