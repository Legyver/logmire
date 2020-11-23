package com.legyver.logmire.task.search;

import com.legyver.tuktukfx.processor.TaskProcessor;
import com.legyver.tuktukfx.task.AbstractAbortableTask;

public class SearchTask extends AbstractAbortableTask<Void, SearchTaskContext>{

	public SearchTask(SearchTaskContext searchTaskContext) {
		super(searchTaskContext);
	}

	@Override
	public TaskProcessor getTaskProcessor() {
		return new SearchTaskProcessor(context);
	}

}
