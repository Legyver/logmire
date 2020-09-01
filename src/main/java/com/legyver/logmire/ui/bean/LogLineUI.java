package com.legyver.logmire.ui.bean;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.StringJoiner;

public class LogLineUI extends BaseLogEntry {
	/**
	 * Full text.  Accumulated line-by-line
	 */
	private final StringJoiner current = new StringJoiner(System.lineSeparator());
	/**
	 * First line of message including date/time stamp, level, reporter, executor, etc.
	 * Generally how it would be displayed in a raw log file.
	 */
	private String truncated;
	/**
	 * Log severity extracted from the first line
	 */
	private String severity;
	/**
	 * Timestamp of event extracted from the first line
	 */
	private String timestamp;
	/**
	 * Date of event (if available) extracted from the first line
	 */
	private String date;
	/**
	 * Class that logged the event, extracted from the first line
	 */
	private String reporter;
	/**
	 * Usually a user or thread, extracted from the first line
	 */
	private String executor;

	private final ObservableList<CausalSectionUI> causalStackTraceSections = FXCollections.observableArrayList();

	public String getPlainText() {
		return current.toString();
	}

	public String getLongMessage() {
		String fullText = current.toString();
		int firstLineEnding = fullText.indexOf(System.lineSeparator());
		if (firstLineEnding > 1) {
			return getShortMessage() + fullText.substring(firstLineEnding);
		}
		return getShortMessage();
	}

	public String getTruncated() {
		return truncated;
	}

	public void setTruncated(String truncated) {
		this.truncated = truncated;
	}

	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
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

	public String getExecutor() {
		return executor;
	}

	public void setExecutor(String executor) {
		this.executor = executor;
	}

	public void accumulate(String line) {
		current.add(line);
	}

	public ObservableList<CausalSectionUI> getCausalStackTraceSections() {
		return causalStackTraceSections;
	}

	public void addCausalSection(CausalSectionUI causalSectionUI) {
		this.causalStackTraceSections.add(causalSectionUI);
	}

}
