package com.legyver.logmire.task.indexlog;

import com.legyver.logmire.ui.bean.LogLineUI;
import com.legyver.logmire.ui.search.InMemoryIndex;

public interface IndexStrategy {
	void execute(InMemoryIndex inMemoryIndex, LogLineUI logLineUI, String longMessageLC);
}
