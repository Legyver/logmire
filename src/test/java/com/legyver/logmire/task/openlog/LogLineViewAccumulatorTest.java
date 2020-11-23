package com.legyver.logmire.task.openlog;

import com.legyver.logmire.ui.bean.CausalSectionUI;
import com.legyver.logmire.ui.bean.LogLineUI;
import com.legyver.logmire.ui.bean.StackTraceElementUI;
import org.hamcrest.*;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class LogLineViewAccumulatorTest {
	@Test
	public void parseSingleLineDateTimestampThread() throws Exception {
		String line = "2020-06-12 11:26:39,428 INFO  [org.apache.coyote.http11.Http11AprProtocol] (Thread-2) Starting Coyote HTTP/1.1 on http-0.0.0.0-8090";
		LogLineAccumulator logLineAccumulator = new LogLineAccumulator();
		LogLineUI logLineUI = logLineAccumulator.addLine(line);
		assertEquals(line, logLineUI.getPlainText());
		assertEquals("2020-06-12", logLineUI.getDate());
		assertEquals("11:26:39,428", logLineUI.getTimestamp());
		assertEquals("INFO", logLineUI.getSeverity());
		assertEquals("org.apache.coyote.http11.Http11AprProtocol", logLineUI.getReporter());
		assertEquals("Starting Coyote HTTP/1.1 on http-0.0.0.0-8090", logLineUI.getShortMessage());
	}

	@Test
	public void parseSingleLineTimestamp() throws Exception {
		String line = "11:23:40,439 INFO  [AbstractServer] Starting: JBossAS [6.1.0.Final \"Neo\"]";
		LogLineAccumulator logLineAccumulator = new LogLineAccumulator();
		LogLineUI logLineUI = logLineAccumulator.addLine(line);
		assertEquals(line, logLineUI.getPlainText());
		assertNull(logLineUI.getDate());
		assertEquals("11:23:40,439", logLineUI.getTimestamp());
		assertEquals("INFO", logLineUI.getSeverity());
		assertEquals("AbstractServer", logLineUI.getReporter());
		assertEquals("Starting: JBossAS [6.1.0.Final \"Neo\"]", logLineUI.getFirstLine());
		assertEquals("JBossAS [6.1.0.Final \"Neo\"]", logLineUI.getShortMessage());
	}

	@Test
	public void parseSingleLineDateTimestampExecutor() throws Exception {
		String line = "2020-06-12 11:26:39,393 ERROR [ProfileServiceBootstrap] (Thread-2) Failed to load profile:: org.jboss.deployers.client.spi.IncompleteDeploymentException: Summary of incomplete deployments (SEE PREVIOUS ERRORS FOR DETAILS):\n";
		LogLineAccumulator logLineAccumulator = new LogLineAccumulator();
		LogLineUI logLineUI = logLineAccumulator.addLine(line);
		assertEquals(line, logLineUI.getPlainText());
		assertEquals("2020-06-12", logLineUI.getDate());
		assertEquals("11:26:39,393", logLineUI.getTimestamp());
		assertEquals("ERROR", logLineUI.getSeverity());
		assertEquals("ProfileServiceBootstrap", logLineUI.getReporter());
		assertEquals("Thread-2", logLineUI.getExecutor());
		assertEquals("Summary of incomplete deployments (SEE PREVIOUS ERRORS FOR DETAILS)", logLineUI.getShortMessage());
	}

	@Test
	public void parseSingleLineDateTimestampExecutorDoubleColons() throws Exception {
		String line = "2020-06-12 11:44:10,755 ERROR [com.example.package.proxy.SomeProxy] (user.name@sometenant.onmicrosoft.com@127.0.0.1) InvocationTargetException occurred, root cause: Error updating object [com.example.package.MyBean@56930e80]: Unable to add object of class [{com::example::package::MyBean}@{metaloader}]: com.example.package.exception.ExampleException: Error updating object [com.example.package.MyBean@56930e80]: Unable to add object of class [{com::example::package::MyBean}@{persistenceservice}]\n";
		LogLineAccumulator logLineAccumulator = new LogLineAccumulator();
		LogLineUI logLineUI = logLineAccumulator.addLine(line);
		assertEquals(line, logLineUI.getPlainText());
		assertEquals("2020-06-12", logLineUI.getDate());
		assertEquals("11:44:10,755", logLineUI.getTimestamp());
		assertEquals("ERROR", logLineUI.getSeverity());
		assertEquals("com.example.package.proxy.SomeProxy", logLineUI.getReporter());
		assertEquals("user.name@sometenant.onmicrosoft.com@127.0.0.1", logLineUI.getExecutor());
		assertEquals("Unable to add object of class [{com::example::package::MyBean}@{persistenceservice}]", logLineUI.getShortMessage());
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
		assertEquals("INFO", logLineUI.getSeverity());
		assertEquals("AbstractJBossASServerBase", logLineUI.getReporter());
		assertEquals("Server Configuration", logLineUI.getShortMessage());
		assertEquals("Server Configuration:", logLineUI.getFirstLine());
	}

	@Test
	public void locationElementAnalysis() throws Exception {
		String line = "2020-06-12 11:44:10,707 ERROR [com.example.package.proxy.SomeProxy] (user.name@sometenant.onmicrosoft.com@121.0.0.1) InvocationTargetException occurred, root cause: Error updating object [com.example.package.MyBean@56930e80]: Unable to add object of class [com.example.package.MyBean]: com.example.package.exception.ExampleException: Error updating object [com.example.package.MyBean@56930e80]: Unable to add object of class [com.example.package.MyBean]\n" +
				"\tat com.example.package.util.PersistenceUtil.createObject(PersistenceUtil.java:277) [:]\n" +
				"\tat com.example.package.session.DataCreator.createObject(DataCreator.java:212) [:]\n" +
				"\tat sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) [:1.8.0_232]\n" +
				"\tat sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) [:1.8.0_232]\n" +
				"\tat sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) [:1.8.0_232]\n" +
				"\tat java.lang.reflect.Method.invoke(Method.java:498) [:1.8.0_232]\n" +
				"\tat com.example.package.proxy.SomeProxy.invoke(SomeProxy.java:56) [:]\n" +
				"\tat com.sun.proxy.$Proxy257.createObject(Unknown Source)\n" +
				"\tat com.example.package.session.MyServiceImpl.create(MyServiceImpl.java:44) [:]\n" +
				"\tat sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) [:1.8.0_232]\n" +
				"\tat sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) [:1.8.0_232]\n" +
				"\tat sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) [:1.8.0_232]\n" +
				"\tat java.lang.reflect.Method.invoke(Method.java:498) [:1.8.0_232]\n" +
				"\tat com.example.package.proxy.SomeProxy.invoke(SomeProxy.java:56) [:]\n" +
				"\tat com.sun.proxy.$Proxy340.create(Unknown Source)\n" +
				"\tat com.example.package.session.MyServiceImpl.updateMyBean(MyServiceImpl.java:132) [:]\n" +
				"\tat com.example.package.session.MyServiceImpl.updateMyBean(MyServiceImpl.java:113) [:]\n" +
				"\tat sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) [:1.8.0_232]\n" +
				"\tat sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) [:1.8.0_232]\n" +
				"\tat sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) [:1.8.0_232]\n" +
				"\tat java.lang.reflect.Method.invoke(Method.java:498) [:1.8.0_232]\n" +
				"\tat com.example.package.proxy.SomeProxy.invoke(SomeProxy.java:56) [:]\n" +
				"\tat com.sun.proxy.$Proxy340.updateMyBean(Unknown Source)\n" +
				"\tat com.example.web.GenericUIAction.saveJsonSettings(GenericUIAction.java:81) [:]\n" +
				"\tat sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) [:1.8.0_232]\n" +
				"\tat sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) [:1.8.0_232]\n" +
				"\tat sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) [:1.8.0_232]\n" +
				"\tat java.lang.reflect.Method.invoke(Method.java:498) [:1.8.0_232]\n" +
				"\tat org.apache.struts.action.RequestProcessor.processActionPerform(RequestProcessor.java:425) [:1.3.10]\n" +
				"\tat org.apache.struts.action.RequestProcessor.process(RequestProcessor.java:228) [:1.3.10]\n" +
				"\tat org.apache.struts.action.ActionServlet.process(ActionServlet.java:1913) [:1.3.10]\n" +
				"\tat org.apache.struts.action.ActionServlet.doPost(ActionServlet.java:462) [:1.3.10]\n" +
				"\tat javax.servlet.http.HttpServlet.service(HttpServlet.java:754) [:1.0.0.Final]\n" +
				"\tat javax.servlet.http.HttpServlet.service(HttpServlet.java:847) [:1.0.0.Final]\n" +
				"\tat org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:324) [:6.1.0.Final]\n" +
				"\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:242) [:6.1.0.Final]\n" +
				"\tat com.example.package.web.SecurityHeaderFilter.doFilter(SecurityHeaderFilter.java:62) [:]\n" +
				"\tat org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:274) [:6.1.0.Final]\n" +
				"\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:242) [:6.1.0.Final]\n" +
				"\tat com.example.package.web.WebFilter.doFilter(WebFilter.java:304) [:1.0.0.0.-SNAPSHOT]\n" +
				"\tat org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:274) [:6.1.0.Final]\n" +
				"\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:242) [:6.1.0.Final]\n" +
				"\tat org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:274) [:6.1.0.Final]\n" +
				"\tat org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:242) [:6.1.0.Final]\n" +
				"\tat org.jboss.web.tomcat.security.SecurityAssociationValve.invoke(SecurityAssociationValve.java:181) [:6.1.0.Final]\n" +
				"\tat org.jboss.modcluster.catalina.CatalinaContext$RequestListenerValve.event(CatalinaContext.java:285) [:1.1.0.Final]\n" +
				"\tat org.jboss.modcluster.catalina.CatalinaContext$RequestListenerValve.invoke(CatalinaContext.java:261) [:1.1.0.Final]\n" +
				"\tat org.jboss.web.tomcat.security.JaccContextValve.invoke(JaccContextValve.java:88) [:6.1.0.Final]\n" +
				"\tat org.jboss.web.tomcat.security.SecurityContextEstablishmentValve.invoke(SecurityContextEstablishmentValve.java:100) [:6.1.0.Final]\n" +
				"\tat org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:159) [:6.1.0.Final]\n" +
				"\tat org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:102) [:6.1.0.Final]\n" +
				"\tat org.jboss.web.tomcat.service.jca.CachedConnectionValve.invoke(CachedConnectionValve.java:158) [:6.1.0.Final]\n" +
				"\tat org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:109) [:6.1.0.Final]\n" +
				"\tat org.jboss.web.tomcat.service.request.ActiveRequestResponseCacheValve.invoke(ActiveRequestResponseCacheValve.java:53) [:6.1.0.Final]\n" +
				"\tat org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:362) [:6.1.0.Final]\n" +
				"\tat org.apache.coyote.http11.Http11AprProcessor.process(Http11AprProcessor.java:893) [:6.1.0.Final]\n" +
				"\tat org.apache.coyote.http11.Http11AprProtocol$Http11ConnectionHandler.process(Http11AprProtocol.java:600) [:6.1.0.Final]\n" +
				"\tat org.apache.tomcat.util.net.AprEndpoint$Worker.run(AprEndpoint.java:2019) [:6.1.0.Final]\n" +
				"\tat java.lang.Thread.run(Thread.java:748) [:1.8.0_232]\n" +
				"Caused by: com.example.package.exception.SomeException: Unable to add object of class [com.example.package.MyBean]\n" +
				"\tat com.example.package.db.DaoService.createObjectInternal(DaoService.java:1201) [:1.0.0.0.-SNAPSHOT]\n" +
				"\tat com.example.package.db.DaoService.createObject(DaoService.java:1170) [:1.0.0.0.-SNAPSHOT]\n" +
				"\tat com.example.package.util.PersistenceUtil.createObject(PersistenceUtil.java:275) [:]\n" +
				"\t... 80 more\n" +
				"Caused by: java.sql.SQLIntegrityConstraintViolationException: ORA-00001: unique constraint (SCHEMA.AK0MY_UNIQUE_CONSTRAINT) violated\n" +
				"\n" +
				"\tat oracle.jdbc.driver.T4CTTIoer11.processError(T4CTTIoer11.java:494) [:12.2.0.1.0]\n" +
				"\tat oracle.jdbc.driver.T4CTTIoer11.processError(T4CTTIoer11.java:446) [:12.2.0.1.0]\n" +

				"\tat oracle.jdbc.driver.OraclePreparedStatementWrapper.executeUpdate(OraclePreparedStatementWrapper.java:1061) [:12.2.0.1.0]\n" +
				"\tat org.jboss.resource.adapter.jdbc.WrappedPreparedStatement.executeUpdate(WrappedPreparedStatement.java:365) [:6.1.0.Final]\n" +
				"\tat com.example.package.db.DaoService.createObjectInternal(DaoService.java:1189) [:1.0.0.0.-SNAPSHOT]\n" +
				"\t... 85 more\n" +
				"Caused by: Error : 1, Position : 0, Sql = INSERT INTO ISM.MY_TABLE (ID,USER_ID,SOME_PATH,SOME_KEY,SOME_VALUE,SOME_BIG_VALUE,CREATION_DATE,CREATOR_ID) VALUES (:1 ,:2 ,:3 ,:4 ,:5 ,:6 ,:7 ,:8 ,:9 ,:10 ), OriginalSql = INSERT INTO ISM.MY_TABLE (ID,USER_ID,SOME_PATH,SOME_KEY,SOME_VALUE,SOME_BIG_VALUE,CREATION_DATE,CREATOR_ID) VALUES (?,?,?,?,?,?,?,?), Error Msg = ORA-00001: unique constraint (SCHEMA.AK0MY_UNIQUE_CONSTRAINT) violated\n" +
				"\n" +
				"\tat oracle.jdbc.driver.T4CTTIoer11.processError(T4CTTIoer11.java:498) [:12.2.0.1.0]\n" +
				"\t... 101 more";

		String[] lines = line.split("\n");
		LogLineAccumulator logLineAccumulator = new LogLineAccumulator();
		LogLineUI logLineUI = null;
		for (String s : lines) {
			logLineUI = logLineAccumulator.addLine(s);
		}
		assertEquals("InvocationTargetException occurred, root cause: Error updating object [com.example.package.MyBean@56930e80]: Unable to add object of class [com.example.package.MyBean]: com.example.package.exception.ExampleException: Error updating object [com.example.package.MyBean@56930e80]: Unable to add object of class [com.example.package.MyBean]", logLineUI.getFirstLine());
		assertEquals("Unable to add object of class [com.example.package.MyBean]", logLineUI.getShortMessage());
		assertEquals("unique constraint (SCHEMA.AK0MY_UNIQUE_CONSTRAINT) violated", logLineUI.getRootError());
		assertEquals("DaoService.java:1189", logLineUI.getRootLocation());

		assertThat(logLineUI.getStackTraceElements(), new NonExhaustiveListMatcher(
				expectedStackTraceElement(
				"at com.example.package.util.PersistenceUtil.createObject(PersistenceUtil.java:277) [:]",
						"PersistenceUtil.java:277",
						"com.example.package.util.PersistenceUtil"
						),
				expectedStackTraceElement(
				"at com.example.package.session.DataCreator.createObject(DataCreator.java:212) [:]",
						"DataCreator.java:212",
						"com.example.package.session.DataCreator"
						),
				expectedStackTraceElement(
				"at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) [:1.8.0_232]",
						"Native Method",
						"sun.reflect.NativeMethodAccessorImpl"
						),
				expectedStackTraceElement(
				"at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) [:1.8.0_232]",
						"NativeMethodAccessorImpl.java:62",
						"sun.reflect.NativeMethodAccessorImpl"
						),
				expectedStackTraceElement(
				"at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) [:1.8.0_232]",
						"DelegatingMethodAccessorImpl.java:43",
						"sun.reflect.DelegatingMethodAccessorImpl"
						),
				expectedStackTraceElement(
				"at java.lang.reflect.Method.invoke(Method.java:498) [:1.8.0_232]",
						"Method.java:498",
						"java.lang.reflect.Method"
						)
				));

		List<CausalSectionUI> causalSections = logLineUI.getCausalStackTraceSections();
		assertThat(causalSections.size(), org.hamcrest.CoreMatchers.is(3));

		CausalSectionUI firstSection = causalSections.get(0);
		assertEquals("com.example.package.exception.SomeException: Unable to add object of class [com.example.package.MyBean]", firstSection.getShortMessage());
		assertThat(firstSection.getStackTraceElements(), new NonExhaustiveListMatcher(
				expectedStackTraceElement(
						"at com.example.package.db.DaoService.createObjectInternal(DaoService.java:1201) [:1.0.0.0.-SNAPSHOT]",
						"DaoService.java:1201",
						"com.example.package.db.DaoService"
				),
				expectedStackTraceElement(
						"at com.example.package.db.DaoService.createObject(DaoService.java:1170) [:1.0.0.0.-SNAPSHOT]",
						"DaoService.java:1170",
						"com.example.package.db.DaoService"
				),
				expectedStackTraceElement(
						"at com.example.package.util.PersistenceUtil.createObject(PersistenceUtil.java:275) [:]",
						"PersistenceUtil.java:275",
						"com.example.package.util.PersistenceUtil"
				)
		));

		CausalSectionUI secondSection = causalSections.get(1);
		assertEquals("java.sql.SQLIntegrityConstraintViolationException: ORA-00001: unique constraint (SCHEMA.AK0MY_UNIQUE_CONSTRAINT) violated", secondSection.getShortMessage());
		assertThat(secondSection.getStackTraceElements(), new NonExhaustiveListMatcher(
				expectedStackTraceElement(
						"at oracle.jdbc.driver.T4CTTIoer11.processError(T4CTTIoer11.java:494) [:12.2.0.1.0]",
						"T4CTTIoer11.java:494",
						"oracle.jdbc.driver.T4CTTIoer11"
				),
				expectedStackTraceElement(
						"at oracle.jdbc.driver.T4CTTIoer11.processError(T4CTTIoer11.java:446) [:12.2.0.1.0]",
						"T4CTTIoer11.java:446",
						"oracle.jdbc.driver.T4CTTIoer11"
				),
				expectedStackTraceElement(
						"at oracle.jdbc.driver.OraclePreparedStatementWrapper.executeUpdate(OraclePreparedStatementWrapper.java:1061) [:12.2.0.1.0]",
						"OraclePreparedStatementWrapper.java:1061",
						"oracle.jdbc.driver.OraclePreparedStatementWrapper"
				),
				expectedStackTraceElement(
						"at org.jboss.resource.adapter.jdbc.WrappedPreparedStatement.executeUpdate(WrappedPreparedStatement.java:365) [:6.1.0.Final]",
						"WrappedPreparedStatement.java:365",
						"org.jboss.resource.adapter.jdbc.WrappedPreparedStatement"
				),
				expectedStackTraceElement(
						"at com.example.package.db.DaoService.createObjectInternal(DaoService.java:1189) [:1.0.0.0.-SNAPSHOT]",
						"DaoService.java:1189",
						"com.example.package.db.DaoService"
				)
		));

		CausalSectionUI thirdSection = causalSections.get(2);
		assertEquals("Error : 1, Position : 0, Sql = INSERT INTO ISM.MY_TABLE (ID,USER_ID,SOME_PATH,SOME_KEY,SOME_VALUE,SOME_BIG_VALUE,CREATION_DATE,CREATOR_ID) VALUES (:1 ,:2 ,:3 ,:4 ,:5 ,:6 ,:7 ,:8 ,:9 ,:10 ), OriginalSql = INSERT INTO ISM.MY_TABLE (ID,USER_ID,SOME_PATH,SOME_KEY,SOME_VALUE,SOME_BIG_VALUE,CREATION_DATE,CREATOR_ID) VALUES (?,?,?,?,?,?,?,?), Error Msg = ORA-00001: unique constraint (SCHEMA.AK0MY_UNIQUE_CONSTRAINT) violated", thirdSection.getShortMessage());
		assertThat(thirdSection.getStackTraceElements(), new NonExhaustiveListMatcher(
				expectedStackTraceElement(
						"at oracle.jdbc.driver.T4CTTIoer11.processError(T4CTTIoer11.java:498) [:12.2.0.1.0]",
						"T4CTTIoer11.java:498",
						"oracle.jdbc.driver.T4CTTIoer11"
				)
		));
	}

	private Matcher<StackTraceElementUI> expectedStackTraceElement(String text, String location, String copyableClassRef) {
		return new StackTraceElementUIMatcher(new StackTraceElementUI(text, location, copyableClassRef));
	}

	private static class StackTraceElementUIMatcher extends BaseMatcher<StackTraceElementUI> {
		private StackTraceElementUI stackTraceElementUI;

		private boolean textMatched;
		private boolean locationMatched;
		private boolean copyableClassRefMatched;

		private StackTraceElementUIMatcher(StackTraceElementUI stackTraceElementUI) {
			this.stackTraceElementUI = stackTraceElementUI;
		}

		@Override
		public boolean matches(Object item) {
			StackTraceElementUI other = (StackTraceElementUI) item;
			textMatched = stackTraceElementUI.getStackTraceElementLine().equals(other.getStackTraceElementLine());
			locationMatched = stackTraceElementUI.getLocation().equals(other.getLocation());
			copyableClassRefMatched = stackTraceElementUI.getCopyableClassRef().equals(other.getCopyableClassRef());
			return textMatched && locationMatched && copyableClassRefMatched;
		}

		@Override
		public void describeTo(Description description) {
			description.appendValue(stackTraceElementUI.toString());
		}
	}

	private class NonExhaustiveListMatcher extends BaseMatcher<List<StackTraceElementUI>> {
		private Matcher<StackTraceElementUI>[] stackTraceElementUIMatchers;

		public NonExhaustiveListMatcher(Matcher<StackTraceElementUI>...stackTraceElementUIMatchers) {
			this.stackTraceElementUIMatchers = stackTraceElementUIMatchers;
		}

		@Override
		public boolean matches(Object item) {
			List<StackTraceElementUI> actual = (List<StackTraceElementUI>) item;
			for (int i = 0; i < stackTraceElementUIMatchers.length; i++) {
				Matcher matcher = stackTraceElementUIMatchers[i];
				MatcherAssert.assertThat(actual.get(i), matcher);
			}
			return true;
		}

		@Override
		public void describeTo(Description description) {

		}
	}
}
