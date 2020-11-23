package com.legyver.logmire.ui.tabs;

import com.legyver.logmire.event.ResetType;
import com.legyver.logmire.ui.bean.DataSourceUI;
import com.legyver.logmire.ui.bean.LogLineUI;
import com.legyver.logmire.ui.filter.PackageFilterData;
import com.legyver.logmire.ui.filter.SeverityFilterData;
import com.legyver.logmire.ui.search.FilterableLogContext;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public class LogView extends Control {
	private final DataSourceUI dataSourceUI;
	private final ObjectProperty<LogLineUI> focusLogLine;
	private final SeverityFilterData severityFilterData;
	private final PackageFilterData packageFilterData;

	public LogView(DataSourceUI dataSourceUI) {
		this.dataSourceUI = dataSourceUI;
		this.severityFilterData = dataSourceUI.getFilterableLogContext().getSeverityFilterData();
		this.packageFilterData = dataSourceUI.getFilterableLogContext().getPackageFilterData();
		this.focusLogLine = dataSourceUI.getFilterableLogContext().focusLogLineProperty();
	}

	public FilterableLogContext getFilterableLogContext() {
		return dataSourceUI.getFilterableLogContext();
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

	//generated delegate methods
	public boolean isFilterEnabled() {
		return packageFilterData.isFilterEnabled();
	}

	public BooleanProperty filterEnabledProperty() {
		return packageFilterData.filterEnabledProperty();
	}

	public void setFilterEnabled(boolean filterEnabled) {
		packageFilterData.setFilterEnabled(filterEnabled);
	}

	public boolean isShowTrace() {
		return severityFilterData.isShowTrace();
	}

	public BooleanProperty showTraceProperty() {
		return severityFilterData.showTraceProperty();
	}

	public void setShowTrace(boolean showTrace) {
		severityFilterData.setShowTrace(showTrace);
	}

	public boolean isShowDebug() {
		return severityFilterData.isShowDebug();
	}

	public BooleanProperty showDebugProperty() {
		return severityFilterData.showDebugProperty();
	}

	public void setShowDebug(boolean showDebug) {
		severityFilterData.setShowDebug(showDebug);
	}

	public boolean isShowInfo() {
		return severityFilterData.isShowInfo();
	}

	public BooleanProperty showInfoProperty() {
		return severityFilterData.showInfoProperty();
	}

	public void setShowInfo(boolean showInfo) {
		severityFilterData.setShowInfo(showInfo);
	}

	public boolean isShowWarn() {
		return severityFilterData.isShowWarn();
	}

	public BooleanProperty showWarnProperty() {
		return severityFilterData.showWarnProperty();
	}

	public void setShowWarn(boolean showWarn) {
		severityFilterData.setShowWarn(showWarn);
	}

	public boolean isShowError() {
		return severityFilterData.isShowError();
	}

	public BooleanProperty showErrorProperty() {
		return severityFilterData.showErrorProperty();
	}

	public void setShowError(boolean showError) {
		severityFilterData.setShowError(showError);
	}

	public boolean isShowFatal() {
		return severityFilterData.isShowFatal();
	}

	public BooleanProperty showFatalProperty() {
		return severityFilterData.showFatalProperty();
	}

	public void setShowFatal(boolean showFatal) {
		severityFilterData.setShowFatal(showFatal);
	}

	public Skin<?> createDefaultSkin() {
		return new LogViewSkin(this);
	}

	public void reset(ResetType resetType) {
		dataSourceUI.getFilterableLogContext().reset(resetType);
	}
}
