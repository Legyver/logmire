package com.legyver.logmire.ui.tabs;

import com.legyver.logmire.ui.bean.DataSourceUI;
import com.legyver.logmire.ui.bean.LogLineUI;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public class LogView extends Control {
	private final DataSourceUI dataSourceUI;
	private final ObjectProperty<LogLineUI> focusLogLine = new SimpleObjectProperty<>();

	public LogView(DataSourceUI dataSourceUI) {
		this.dataSourceUI = dataSourceUI;
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

	public Skin<?> createDefaultSkin() {
		return new LogViewSkin(this);
	}
}
