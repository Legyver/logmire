package com.legyver.logmire.ui.bean;

import java.util.StringJoiner;

public class LogLineUI {
	private final StringJoiner current = new StringJoiner(System.lineSeparator());
	private String truncated;
	private String message;
	private String level;
	private String timestamp;
	private String date;
	private String reporter;

	public String getPlainText() {
		return current.toString();
	}

	public String getTruncated() {
		return truncated;
	}

	public void setTruncated(String truncated) {
		this.truncated = truncated;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getReporter() {
		return reporter;
	}

	public void setReporter(String reporter) {
		this.reporter = reporter;
	}

	public void accumulate(String line) {
		current.add(line);
	}

	public void deconstruct() {

	}
}
