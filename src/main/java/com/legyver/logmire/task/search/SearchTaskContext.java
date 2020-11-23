package com.legyver.logmire.task.search;

import com.legyver.logmire.ui.bean.LogLineUI;
import com.legyver.tuktukfx.status.TaskTimingData;

import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.function.Function;
import java.util.function.Predicate;

public class SearchTaskContext extends TaskTimingData {
	private final Function<Predicate, List<LogLineUI>> indexSearchImpl;
	private List<LogLineUI> searchResult;
	private Semaphore lock;

	public SearchTaskContext(double sizeGuess, Function<Predicate, List<LogLineUI>> indexSearchImpl) {
		super(sizeGuess);
		this.indexSearchImpl = indexSearchImpl;
	}

	public void setSearchResult(List<LogLineUI> searchResult) {
		this.searchResult = searchResult;
	}

	public List<LogLineUI> getSearchResult() {
		return searchResult;
	}

	public void setLock(Semaphore lock) {
		this.lock = lock;
	}

	public Function<Predicate, List<LogLineUI>> getIndexSearchImpl() {
		return indexSearchImpl;
	}

	public Semaphore getLock() {
		return lock;
	}
}
