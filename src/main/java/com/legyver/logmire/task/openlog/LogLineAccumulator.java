package com.legyver.logmire.task.openlog;

import com.legyver.logmire.ui.bean.DataSourceUI;

import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogLineAccumulator {
	private final static Pattern TIME_START = Pattern.compile("^\\d{1,2}:\\d{2}:\\d{2},\\d{3}");
	private final DataSourceUI dataSource;
	private StringJoiner current;

	public LogLineAccumulator(DataSourceUI dataSource) {
		this.dataSource = dataSource;
	}

	public void addLine(String line) {
		if (newLine(line)) {
			if (current != null) {
				dataSource.addLine(current.toString());
			}
			current = new StringJoiner(System.lineSeparator());
		}
		if (current == null) {
			current = new StringJoiner(System.lineSeparator());
		}
		current.add(line);
	}

	private boolean newLine(String line) {
		Matcher matcher = TIME_START.matcher(line);
		return matcher.find();
	}
}
