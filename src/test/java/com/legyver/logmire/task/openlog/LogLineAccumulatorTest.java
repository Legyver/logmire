package com.legyver.logmire.task.openlog;

import com.legyver.logmire.ui.bean.LogLineUI;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class LogLineAccumulatorTest {
	@Test
	public void parseSingleLineDateTimestampThread() throws Exception {
		String line = "2020-06-12 11:26:39,428 INFO  [org.apache.coyote.http11.Http11AprProtocol] (Thread-2) Starting Coyote HTTP/1.1 on http-0.0.0.0-8090";
		LogLineAccumulator logLineAccumulator = new LogLineAccumulator();
		LogLineUI logLineUI = logLineAccumulator.addLine(line);
		assertEquals(line, logLineUI.getPlainText());
		assertEquals("2020-06-12", logLineUI.getDate());
		assertEquals("11:26:39,428", logLineUI.getTimestamp());
		assertEquals("INFO", logLineUI.getLevel());
		assertEquals("org.apache.coyote.http11.Http11AprProtocol", logLineUI.getReporter());
		assertEquals("Starting Coyote HTTP/1.1 on http-0.0.0.0-8090", logLineUI.getMessage());
	}

	@Test
	public void parseSingleLineTimestamp() throws Exception {
		String line = "11:23:40,439 INFO  [AbstractServer] Starting: JBossAS [6.1.0.Final \"Neo\"]";
		LogLineAccumulator logLineAccumulator = new LogLineAccumulator();
		LogLineUI logLineUI = logLineAccumulator.addLine(line);
		assertEquals(line, logLineUI.getPlainText());
		assertNull(logLineUI.getDate());
		assertEquals("11:23:40,439", logLineUI.getTimestamp());
		assertEquals("INFO", logLineUI.getLevel());
		assertEquals("AbstractServer", logLineUI.getReporter());
		assertEquals("Starting: JBossAS [6.1.0.Final \"Neo\"]", logLineUI.getMessage());
	}

	@Test
	public void parseMultiLineTimestamp() throws Exception {
		String line = "11:23:40,431 INFO  [AbstractJBossASServerBase] Server Configuration:\r\n"
				+ "\r\n"
				+ "\tJBOSS_HOME URL: file:/C:/JBoss/\r\n"
				+ "\tBootstrap: $JBOSS_HOME\\server/all/conf/bootstrap.xml\r\n"
				+ "\tCommon Base: $JBOSS_HOME\\common/\r\n"
				+ "\tCommon Library: $JBOSS_HOME\\common/lib/\r\n"
				+ "\tServer Name: all\r\n"
				+ "\tServer Base: $JBOSS_HOME\\server/\r\n"
				+ "\tServer Library: $JBOSS_HOME\\server/all/lib/\r\n"
				+ "\tServer Config: $JBOSS_HOME\\server/all/conf/\r\n"
				+ "\tServer Home: $JBOSS_HOME\\server/all/\r\n"
				+ "\tServer Data: $JBOSS_HOME\\server/all/data/\r\n"
				+ "\tServer Log: $JBOSS_HOME\\server/all/log/\r\n"
				+ "\tServer Temp: $JBOSS_HOME\\server/all/tmp/";
		String[] lines = line.split("\r\n");
		LogLineAccumulator logLineAccumulator = new LogLineAccumulator();
		LogLineUI logLineUI = null;
		for (String s : lines) {
			logLineUI = logLineAccumulator.addLine(s);
		}
		assertEquals(line, logLineUI.getPlainText());
		assertNull(logLineUI.getDate());
		assertEquals("11:23:40,431", logLineUI.getTimestamp());
		assertEquals("INFO", logLineUI.getLevel());
		assertEquals("AbstractJBossASServerBase", logLineUI.getReporter());
		assertEquals("Server Configuration:", logLineUI.getMessage());
	}
}
