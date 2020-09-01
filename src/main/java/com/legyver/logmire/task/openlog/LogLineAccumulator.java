package com.legyver.logmire.task.openlog;

import com.legyver.logmire.ui.bean.CausalSectionUI;
import com.legyver.logmire.ui.bean.LogLineUI;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class LogLineAccumulator {
	private static final String DATE_REGEX = "\\d{4}-\\d{2}-\\d{2}";
	private static final Pattern DATE_START = Pattern.compile("^" + DATE_REGEX);

	private static final String LEVEL_REGEX = "\\b(INFO|WARN|ERROR|TRACE|DEBUG)\\b";
	/**
	 * group 1: reporter
	 * group 4: (Optional) executor
	 * group 6: message
	 */
	private static final String MESSAGE_REGEX = "\\[(([a-zA-Z0-9.])+)\\] (\\(((.)*)\\))?((.)+)";
	/**
	 * group 1: location
	 */
	private static final String LOCATION_LINE_REGEX = "\\((([A-Za-z0-9.])*:(([0-9])*))\\)";
	private static final Pattern LOCATION_LINE = Pattern.compile(LOCATION_LINE_REGEX);

	private static final String TIMESTAMP_REGEX = "\\d{1,2}:\\d{2}:\\d{2},\\d{3}";
	private static final Pattern TIMESTAMP_START = Pattern.compile("^" + TIMESTAMP_REGEX);

	private LogLineUI currentLog;
	private CausalSectionUI causalSectionUI = null;

	public LogLineUI addLine(String line) {
		if (newEntry(line)) {
			currentLog = new LogLineUI();
			currentLog.setTruncated(line.replaceAll("\r\n", "").replaceAll("\n", ""));
			causalSectionUI = null;
		} else if (causedBy(line)) {
			causalSectionUI = new CausalSectionUI();
			processCausedBy(line);
			currentLog.addCausalSection(causalSectionUI);
		} else if (stackTraceElement(line)) {
			processStackTraceElement(line);
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

	private void processCausedBy(String line) {
		String reason = line.substring(11);//"Caused by: "
		causalSectionUI.setShortMessage(reason);
	}

	private void processStackTraceElement(String line) {
		String trimmed = line.trim();

		String locationLine = getLocationLine(trimmed);
		String locationName = getLocationName(trimmed);

		if (locationName != null) {
			if (causalSectionUI != null) {
				causalSectionUI.addStackTraceElement(trimmed, locationLine, locationName);
			} else {
				currentLog.addStackTraceElement(trimmed, locationLine, locationName);
			}
		}
	}

	private String getLocationName(String trimmed) {
		String locationName;
		//doing all this mess because .NET convention differs from Java convention
		int locationLineStart = trimmed.indexOf('(');
		int postAt = 3;
		locationName = trimmed.substring(postAt, locationLineStart - 1);

		//lop off the last part and hope it was the method
		int preMethod = locationName.lastIndexOf('.');
		locationName = locationName.substring(0, preMethod);
		return locationName;
	}

	private String getLocationLine(String trimmed) {
		Matcher locationLineMatcher = LOCATION_LINE.matcher(trimmed);
		String locationLine = null;
		if (locationLineMatcher.find()) {
			locationLine = locationLineMatcher.group(1);
		} else if (trimmed.contains("(Native Method)")) {
			locationLine = "Native Method";
		}
		return locationLine;
	}

	private boolean newEntry(String line) {
		Matcher timeAtStart = TIMESTAMP_START.matcher(line);
		if (timeAtStart.find()) {
			return true;
		}
		Matcher dateAtStart = DATE_START.matcher(line);
		return dateAtStart.find();
	}

	private boolean causedBy(String line) {
		return line.trim().startsWith("Caused by:");
	}

	private boolean stackTraceElement(String line) {
		return line.trim().startsWith("at ");
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
				return logLineUI.getSeverity() == null;
			}

			@Override
			void setValue(LogLineUI logLineUI, String value) {
				logLineUI.setSeverity(value);
			}
		}, MESSAGE(MESSAGE_REGEX, 6) {
			@Override
			boolean isNotSet(LogLineUI logLineUI) {
				return logLineUI.getShortMessage() == null;
			}

			@Override
			void setValue(LogLineUI logLineUI, String value) {
				logLineUI.setShortMessage(value);
			}
		}, REPORTER(MESSAGE_REGEX, 1) {
			@Override
			boolean isNotSet(LogLineUI logLineUI) {
				return logLineUI.getReporter() == null;
			}

			@Override
			void setValue(LogLineUI logLineUI, String value) {
				logLineUI.setReporter(value);
			}
		}, EXECUTOR(MESSAGE_REGEX, 4) {
			@Override
			boolean isNotSet(LogLineUI logLineUI) {
				return logLineUI.getExecutor() == null;
			}

			@Override
			void setValue(LogLineUI logLineUI, String value) {
				logLineUI.setExecutor(value);
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
				String raw = matcher.group(groupIndex);
				return raw == null ? raw : raw.trim();
			}
			return null;
		}

		abstract boolean isNotSet(LogLineUI logLineUI);
		abstract void setValue(LogLineUI logLineUI, String value);
	}
}
