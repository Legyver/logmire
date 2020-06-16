package com.legyver.logmire.ui.tabs;

import com.legyver.logmire.ui.bean.DataSourceUI;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public class LogView extends Control {
	private final DataSourceUI dataSourceUI;

	public LogView(DataSourceUI dataSourceUI) {
		this.dataSourceUI = dataSourceUI;
	}

	public DataSourceUI getDataSourceUI() {
		return dataSourceUI;
	}

	public Skin<?> createDefaultSkin() {
		return new LogViewSkin(this);
	}
}
