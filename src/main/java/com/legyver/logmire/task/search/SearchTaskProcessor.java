package com.legyver.logmire.task.search;

import com.legyver.core.exception.CoreException;
import com.legyver.logmire.ui.bean.LogLineUI;
import com.legyver.tuktukfx.adapter.AbortableTaskStatusAdapter;
import com.legyver.tuktukfx.processor.TaskProcessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.Semaphore;

public class SearchTaskProcessor implements TaskProcessor<AbortableTaskStatusAdapter> {
	private static final Logger logger = LogManager.getLogger();
	private final SearchTaskContext searchTaskContext;

	public SearchTaskProcessor(SearchTaskContext searchTaskContext) {
		this.searchTaskContext = searchTaskContext;
	}

	@Override
	public void process(AbortableTaskStatusAdapter taskStatusAdapter) throws CoreException {
		Semaphore mutex = searchTaskContext.getLock();
		try {
			mutex.acquire();
			List<LogLineUI> searchResult = searchTaskContext.getIndexSearchImpl().apply((x) -> taskStatusAdapter.isAborted());
			searchTaskContext.setSearchResult(searchResult);
		} catch (InterruptedException e) {
			logger.error("Error obtaining lock", e);
		} finally {
			mutex.release();
		}
	}
}
