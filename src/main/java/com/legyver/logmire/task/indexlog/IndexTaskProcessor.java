package com.legyver.logmire.task.indexlog;

import com.legyver.core.exception.CoreException;
import com.legyver.logmire.ui.bean.LogLineUI;
import com.legyver.logmire.ui.search.InMemoryIndex;
import com.legyver.tuktukfx.adapter.TaskStatusAdapter;
import com.legyver.tuktukfx.processor.TaskProcessor;

import java.util.List;

public class IndexTaskProcessor implements TaskProcessor {
	private final InMemoryIndex inMemoryIndex;
	private final List<LogLineUI> linesToIndex;

	public IndexTaskProcessor(IndexTask indexTask) {
		this.inMemoryIndex = indexTask.getInMemoryIndex();
		this.linesToIndex = indexTask.getLinesToIndex();
	}

	@Override
	public void process(TaskStatusAdapter taskStatusAdapter) throws CoreException {
		for (LogLineUI logLineUI : linesToIndex) {
			IndexContext indexContext = new IndexContext(logLineUI);

//			indexContext.setIndexStrategy(new NCharIndexStrategy(3));
//			indexContext.executeStrategy(inMemoryIndex);

			indexContext.setIndexStrategy(new WordSplitStrategy());
			indexContext.executeStrategy(inMemoryIndex);
		}
	}
}
