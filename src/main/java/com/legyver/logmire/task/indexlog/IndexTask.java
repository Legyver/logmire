package com.legyver.logmire.task.indexlog;

import com.legyver.core.exception.CoreException;
import com.legyver.logmire.ui.bean.LogLineUI;
import com.legyver.logmire.ui.search.FilterableLogContext;
import com.legyver.logmire.ui.search.InMemoryIndex;
import com.legyver.tuktukfx.adapter.TaskStatusAdapter;
import com.legyver.tuktukfx.processor.TaskProcessor;
import com.legyver.tuktukfx.status.TaskTimingData;
import com.legyver.tuktukfx.task.AbstractObservableTask;

import java.util.List;

public class IndexTask extends AbstractObservableTask {
	private final InMemoryIndex inMemoryIndex;
	private final List<LogLineUI> linesToIndex;

	public IndexTask(FilterableLogContext filterableLogContext, List<LogLineUI> linesToIndex) {
		super(new TaskTimingData(100));

		this.inMemoryIndex = filterableLogContext.getInMemoryIndex();
		this.linesToIndex = linesToIndex;
	}

	@Override
	public Object execute(TaskStatusAdapter taskStatusAdapter) throws CoreException {
		process(taskStatusAdapter, 100.0);
		return null;
	}

	@Override
	public TaskProcessor getTaskProcessor() {
		return new IndexTaskProcessor(this);
	}

	public InMemoryIndex getInMemoryIndex() {
		return inMemoryIndex;
	}

	public List<LogLineUI> getLinesToIndex() {
		return linesToIndex;
	}
}
