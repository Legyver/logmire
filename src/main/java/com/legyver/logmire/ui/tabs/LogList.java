package com.legyver.logmire.ui.tabs;

import com.legyver.logmire.ui.search.FilterableLogContext;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public class LogList extends Control {
	private final LogView logView;
	private final FilterableLogContext filterableLogContext;

	public LogList(LogView logView, FilterableLogContext filterableLogContext) {
		this.logView = logView;
		this.filterableLogContext = filterableLogContext;
	}

	public FilterableLogContext getFilterableLogContext() {
		return filterableLogContext;
	}

	public LogView getLogView() {
		return logView;
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new LogListSkin(this);
	}
}
