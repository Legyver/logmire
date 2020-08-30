package com.legyver.logmire.task.openlog;

import com.legyver.logmire.ui.bean.LogLineUI;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class LogLineAccumulator {
	private static final String DATE_REGEX = "\\d{4}-\\d{2}-\\d{2}";
	private static final Pattern DATE_START = Pattern.compile("^" + DATE_REGEX);

	private static final String LEVEL_REGEX = "\\b(INFO|WARN|ERROR|TRACE|DEBUG)\\b";
	private static final String MESSAGE_REGEX = "\\[(([a-zA-Z0-9.])*)\\] (\\(Thread-\\d{1,2}\\) )*((.)*)";//group 1: reporter, group 3: (Optional) Thread, group 4: message
	private static final String REPORTER_REGEX = "\\[(([a-zA-Z0-9.])*)\\]";

	private static final String TIMESTAMP_REGEX = "\\d{1,2}:\\d{2}:\\d{2},\\d{3}";
	private static final Pattern TIMESTAMP_START = Pattern.compile("^" + TIMESTAMP_REGEX);

	private LogLineUI currentLog;

	public LogLineUI addLine(String line) {
		if (newEntry(line)) {
			currentLog = new LogLineUI();
			currentLog.setTruncated(line.replaceAll("\r\n", "").replaceAll("\n", ""));
		}
		//catch if the logfile starts with something that does not match new line criteria
		if (currentLog == null) {
			currentLog = new LogLineUI();
		}
		currentLog.accumulate(line);
		Stream.of(LogData.values()).forEach(logData -> {
			if (logData.isNotSet(currentLog)) {
				logData.setValue(currentLog, logData.getValue(line));
			}
		});

		return currentLog;
	}

	private boolean newEntry(String line) {
		Matcher timeAtStart = TIMESTAMP_START.matcher(line);
		if (timeAtStart.find()) {
			return true;
		}
		Matcher dateAtStart = DATE_START.matcher(line);
		return dateAtStart.find();
	}

	private enum LogData {
		DATE(DATE_REGEX, 0) {
			@Override
			boolean isNotSet(LogLineUI logLineUI) {
				return logLineUI.getDate() == null;
			}

			@Override
			void setValue(LogLineUI logLineUI, String value) {
				logLineUI.setDate(value);
			}
		}, TIMESTAMP(TIMESTAMP_REGEX, 0) {
			@Override
			boolean isNotSet(LogLineUI logLineUI) {
				return logLineUI.getTimestamp() == null;
			}

			@Override
			void setValue(LogLineUI logLineUI, String value) {
				logLineUI.setTimestamp(value);
			}
		}, LEVEL(LEVEL_REGEX, 0) {
			@Override
			boolean isNotSet(LogLineUI logLineUI) {
				return logLineUI.getLevel() == null;
			}

			@Override
			void setValue(LogLineUI logLineUI, String value) {
				logLineUI.setLevel(value);
			}
		}, MESSAGE(MESSAGE_REGEX, 4) {
			@Override
			boolean isNotSet(LogLineUI logLineUI) {
				return logLineUI.getMessage() == null;
			}

			@Override
			void setValue(LogLineUI logLineUI, String value) {
				logLineUI.setMessage(value);
			}
		}, REPORTER(REPORTER_REGEX, 1) {
			@Override
			boolean isNotSet(LogLineUI logLineUI) {
				return logLineUI.getReporter() == null;
			}

			@Override
			void setValue(LogLineUI logLineUI, String value) {
				logLineUI.setReporter(value);
			}
		};

		final Pattern pattern;
		final int groupIndex;

		LogData(String regex, int groupIndex) {
			this.pattern = Pattern.compile(regex);
			this.groupIndex = groupIndex;
		}

		String getValue(String line) {
			Matcher matcher = pattern.matcher(line);
			if (matcher.find()) {
				return matcher.group(groupIndex);
			}
			return null;
		}

		abstract boolean isNotSet(LogLineUI logLineUI);
		abstract void setValue(LogLineUI logLineUI, String value);
	}
}
