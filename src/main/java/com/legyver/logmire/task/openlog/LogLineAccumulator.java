package com.legyver.logmire.task.openlog;

import com.legyver.logmire.ui.bean.CausalSectionUI;
import com.legyver.logmire.ui.bean.LogLineUI;
import com.legyver.logmire.ui.bean.StackTraceElementUI;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
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
	private static final String MESSAGE_REGEX = "\\[(([a-zA-Z0-9.])+)\\] (\\((([^)])+)\\))?((.)+)";
	/**
	 * group 1: location
	 */
	private static final String LOCATION_LINE_REGEX = "\\((([A-Za-z0-9.])*:(([0-9])*))\\)";
	private static final Pattern LOCATION_LINE = Pattern.compile(LOCATION_LINE_REGEX);

	private static final String TIMESTAMP_REGEX = "\\d{1,2}:\\d{2}:\\d{2},\\d{3}";
	private static final Pattern TIMESTAMP_START = Pattern.compile("^" + TIMESTAMP_REGEX);

	private LogLineUI currentLog;
	private CausalSectionUI causalSectionUI = null;
	private int count = 0;

	public LogLineUI addLine(String line) {
		if (newEntry(line)) {
			currentLog = new LogLineUI(++count);
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
			currentLog = new LogLineUI(++count);
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
		//deliberately overwriting earlier values as each "Caused by" gets closer to the root error
		if (reason.contains(": ")) {
			currentLog.setRootError(getLastSplit(reason));
		} else {
			currentLog.setRootError(reason);
		}
		//replay the previous stack to get the location
		if (currentLog.getCausalStackTraceSections().size() > 0) {
			CausalSectionUI previousStack = currentLog.getCausalStackTraceSections().get(currentLog.getCausalStackTraceSections().size() - 1);
			List<StackTraceElementUI> stackTraceElements = previousStack.getStackTraceElements();
			//find the last non-internal location
			for (int i = stackTraceElements.size() - 1; i > -1; i--) {
				StackTraceElementUI stackTraceElementUI = stackTraceElements.get(i);
				String location = stackTraceElementUI.getLocation();
				if (!startsWithAny(location, "java.", "javax.", "sun.", "com.oracle.", "org.apache.", "org.jboss.", "org.springframework.")) {//TODO: externalize
					currentLog.setRootLocation(location);
					break;
				}
			}
		}
	}

	private boolean startsWithAny(String value, String...prefixes) {
		return Stream.of(prefixes).anyMatch(prefix -> value.startsWith(prefix));
	}

	private static String getLastSplit(String value) {
		String[] parts = value.split(": ");
		for (int i = parts.length - 1; i > -1; i--) {
			String part = parts[i];
			if (!StringUtils.isAllBlank(part)) {
				return part;
			}
		}
		return value;
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
				logLineUI.setFirstLine(value);
				if (value != null && value.contains(": ")) {
					setTrimmedMessage(logLineUI, getLastSplit(value));
					if (logLineUI.getShortMessage() == null) {
						setTrimmedMessage(logLineUI, value);
					}
				} else {
					setTrimmedMessage(logLineUI, value);
				}
			}

			private void setTrimmedMessage(LogLineUI logLineUI, String value) {
				if (value != null) {
					if (value.endsWith(":")) {
						logLineUI.setShortMessage(value.substring(0, value.length() - 1).trim());
					} else {
						logLineUI.setShortMessage(value.trim());
					}
				}
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
