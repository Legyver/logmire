package com.legyver.logmire.task.indexlog;

import com.legyver.logmire.ui.bean.LogLineUI;
import com.legyver.logmire.ui.search.InMemoryIndex;

import java.util.List;

public class NCharIndexStrategy implements IndexStrategy {
	private final int nCount;

	public NCharIndexStrategy(int nCount) {
		this.nCount = nCount;
	}

	@Override
	public void execute(InMemoryIndex inMemoryIndex, LogLineUI logLineUI, String longMessageLC) {
		switch (nCount) {
			case 5: index(inMemoryIndex, logLineUI, longMessageLC, IndexPattern.FIVE);//index 5 AND
			case 4: index(inMemoryIndex, logLineUI, longMessageLC, IndexPattern.FOUR);//index 4 AND
			case 3: index(inMemoryIndex, logLineUI, longMessageLC, IndexPattern.THREE);//index 3 (the fall-throughs are intentional)
				break;
			default:
				//lets start with above
		}
	}

	private void index(InMemoryIndex inMemoryIndex, LogLineUI logLineUI, String longMessageLC, List<String> indexedStrings) {
		for (String searchPattern: indexedStrings) {
			if (longMessageLC.contains(searchPattern)) {
				inMemoryIndex.index(searchPattern, logLineUI);
			}
		}
	}
}
