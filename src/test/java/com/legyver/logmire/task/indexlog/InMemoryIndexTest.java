package com.legyver.logmire.task.indexlog;

import com.legyver.logmire.task.openlog.LogLineAccumulator;
import com.legyver.logmire.task.search.SearchTaskContext;
import com.legyver.logmire.ui.bean.LogLineUI;
import com.legyver.logmire.ui.search.InMemoryIndex;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class InMemoryIndexTest {

	@Test
	public void simpleIndex() {
		String line = "2020-06-12 11:26:39,428 INFO  [org.apache.coyote.http11.Http11AprProtocol] (Thread-2) Starting Coyote HTTP/1.1 on http-0.0.0.0-8090";
		LogLineUI logLineUI = getLogLine(line);

		InMemoryIndex inMemoryIndex = new InMemoryIndex();
		index(inMemoryIndex, logLineUI);

		{
			List<LogLineUI> indexed = search(inMemoryIndex, "Starting");
			assertThat(indexed).contains(logLineUI);
		}

		{
			List<LogLineUI> indexed = search(inMemoryIndex, "Sta");
			assertThat(indexed).contains(logLineUI);
		}

		{
			List<LogLineUI> indexed = search(inMemoryIndex, "8090");
			assertThat(indexed).contains(logLineUI);
		}

		{
			List<LogLineUI> indexed = search(inMemoryIndex, "Starting Coyote");
			assertThat(indexed).contains(logLineUI);
		}

	}

	private List<LogLineUI> search(InMemoryIndex inMemoryIndex, String search) {
		SearchTaskContext searchTaskContext = inMemoryIndex.searchTaskContext(search);
		List<LogLineUI> indexed = searchTaskContext.getIndexSearchImpl().apply((x) -> false);
		return indexed;
	}

	private void index(InMemoryIndex inMemoryIndex, LogLineUI logLineUI) {
		IndexContext indexContext = new IndexContext(logLineUI);

		indexContext.setIndexStrategy(new NCharIndexStrategy(3));
		indexContext.executeStrategy(inMemoryIndex);

		indexContext.setIndexStrategy(new WordSplitStrategy());
		indexContext.executeStrategy(inMemoryIndex);
	}

	private LogLineUI getLogLine(String line) {
		LogLineAccumulator logLineAccumulator = new LogLineAccumulator();
		return logLineAccumulator.addLine(line);
	}

}
