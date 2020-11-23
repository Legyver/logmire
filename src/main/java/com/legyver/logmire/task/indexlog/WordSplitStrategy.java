package com.legyver.logmire.task.indexlog;

import com.legyver.logmire.ui.bean.LogLineUI;
import com.legyver.logmire.ui.search.InMemoryIndex;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class WordSplitStrategy implements IndexStrategy {
	public static final String SPLIT_PATTERN = " \t\n\r\f.:()[]";

	@Override
	public void execute(InMemoryIndex inMemoryIndex, LogLineUI logLineUI, String longMessageLC) {
		StringTokenizer stringTokenizer = new StringTokenizer(longMessageLC, SPLIT_PATTERN);
		while (stringTokenizer.hasMoreTokens()) {
			String next = stringTokenizer.nextToken();
			inMemoryIndex.index(next, logLineUI);
		}
	}
}
