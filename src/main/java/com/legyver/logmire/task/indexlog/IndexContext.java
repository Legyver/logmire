package com.legyver.logmire.task.indexlog;

import com.legyver.logmire.ui.bean.LogLineUI;
import com.legyver.logmire.ui.search.InMemoryIndex;

import java.util.Locale;

public class IndexContext {
	private final String longMessageLC;
	private final LogLineUI logLineUI;
	private IndexStrategy indexStrategy;

	public IndexContext(LogLineUI logLineUI) {
		this.logLineUI = logLineUI;
		this.longMessageLC = logLineUI.getLongMessage().toLowerCase(Locale.getDefault());
	}

	public void executeStrategy(InMemoryIndex inMemoryIndex) {
		indexStrategy.execute(inMemoryIndex, logLineUI, longMessageLC);
	}

	public void setIndexStrategy(IndexStrategy indexStrategy) {
		this.indexStrategy = indexStrategy;
	}
}
