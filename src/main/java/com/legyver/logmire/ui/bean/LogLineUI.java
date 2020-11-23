package com.legyver.logmire.ui.bean;

import com.legyver.logmire.ui.tabs.LogLineView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.StringJoiner;

public class LogLineUI extends BaseLogEntry implements Comparable {
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

	/**
	 * Actual message extracted from the first line.
	 * Unlike shortMessage this contains all the message regardless if there are colons or not.
	 */
	private String firstLine;

	/**
	 * Root cause message extracted from stacktrace
	 */
	private String rootError;

	/**
	 * Root cause location extracted from stacktrace
	 */
	private String rootLocation;

	/**
	 * entry number in logfile after rolling up stacktrace
	 */
	private final Integer entryNumber;

	/**
	 * Reference to the UI view
	 */
	private LogLineView logLineView;

	private final ObservableList<CausalSectionUI> causalStackTraceSections = FXCollections.observableArrayList();

	public LogLineUI(int entryNumber) {
		this.entryNumber = entryNumber;
	}

	public String getPlainText() {
		return current.toString();
	}

	public String getLongMessage() {
		String fullText = current.toString();
		int firstLineEnding = fullText.indexOf(System.lineSeparator());
		if (firstLineEnding > 1) {
			return getFirstLine() + fullText.substring(firstLineEnding);
		}
		return getShortMessage();
	}

	public String getFirstLine() {
		return firstLine;
	}

	public void setFirstLine(String firstLine) {
		this.firstLine = firstLine;
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

	public String getRootError() {
		return rootError;
	}

	public void setRootError(String rootError) {
		this.rootError = rootError;
	}

	public String getRootLocation() {
		return rootLocation;
	}

	public void setRootLocation(String rootLocation) {
		this.rootLocation = rootLocation;
	}

	public void accumulate(String line) {
		current.add(line);
	}

	public ObservableList<CausalSectionUI> getCausalStackTraceSections() {
		return causalStackTraceSections;
	}

	public void addCausalSection(CausalSectionUI causalSectionUI) {
		acquireLock();
		this.causalStackTraceSections.add(causalSectionUI);
		releaseLock();
	}

	public Integer getEntryNumber() {
		return entryNumber;
	}

	public LogLineView getLogLineView() {
		return logLineView;
	}

	public void setLogLineView(LogLineView logLineView) {
		this.logLineView = logLineView;
	}

	//used for displaying search result lines in order
	@Override
	public int compareTo(Object o) {
		return this.entryNumber.compareTo(((LogLineUI) o).entryNumber);
	}

	//used for indexing
	@Override
	public int hashCode() {
		return entryNumber.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		//also counts as a null check
		if (obj instanceof LogLineUI) {
			return new EqualsBuilder()
					.append(entryNumber, ((LogLineUI) obj).entryNumber).isEquals();
		}
		return false;
	}
}
