package com.legyver.logmire.ui.tabs;

import com.legyver.logmire.ui.bean.CausalSectionUI;
import com.legyver.logmire.ui.bean.StackTraceElementUI;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;


public class LogLineDetail extends Control {
	private final StringProperty date = new SimpleStringProperty();
	private final StringProperty timestamp = new SimpleStringProperty();
	private final StringProperty severity = new SimpleStringProperty();
	private final StringProperty reporter = new SimpleStringProperty();
	private final StringProperty fullMessage = new SimpleStringProperty();
	private final StringProperty copyableMessage = new SimpleStringProperty();
	private final StringProperty fullInfo = new SimpleStringProperty();
	private final StringProperty executor = new SimpleStringProperty();
	private final StringProperty rootError = new SimpleStringProperty();
	private final StringProperty rootLocation = new SimpleStringProperty();

	private final ObservableList<StackTraceElementUI> stackTraceElements = FXCollections.observableArrayList();
	private final ObservableList<CausalSectionUI> causalSections = FXCollections.observableArrayList();

	public LogLineDetail() {
		getStyleClass().add("logline-detail");
	}

	public String getDate() {
		return date.get();
	}

	public StringProperty dateProperty() {
		return date;
	}

	public void setDate(String date) {
		this.date.set(date);
	}

	public String getTimestamp() {
		return timestamp.get();
	}

	public StringProperty timestampProperty() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp.set(timestamp);
	}

	public String getSeverity() {
		return severity.get();
	}

	public StringProperty severityProperty() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity.set(severity);
	}

	public String getReporter() {
		return reporter.get();
	}

	public StringProperty reporterProperty() {
		return reporter;
	}

	public void setReporter(String reporter) {
		this.reporter.set(reporter);
	}

	public String getFullMessage() {
		return fullMessage.get();
	}

	public StringProperty fullMessageProperty() {
		return fullMessage;
	}

	public void setFullMessage(String fullMessage) {
		this.fullMessage.set(fullMessage);
	}

	public String getCopyableMessage() {
		return copyableMessage.get();
	}

	public StringProperty copyableMessageProperty() {
		return copyableMessage;
	}

	public void setCopyableMessage(String copyableMessage) {
		this.copyableMessage.set(copyableMessage);
	}

	public String getFullInfo() {
		return fullInfo.get();
	}

	public StringProperty fullInfoProperty() {
		return fullInfo;
	}

	public void setFullInfo(String fullInfo) {
		this.fullInfo.set(fullInfo);
	}

	public String getExecutor() {
		return executor.get();
	}

	public StringProperty executorProperty() {
		return executor;
	}

	public void setExecutor(String executor) {
		this.executor.set(executor);
	}

	public String getRootError() {
		return rootError.get();
	}

	public StringProperty rootErrorProperty() {
		return rootError;
	}

	public void setRootError(String rootError) {
		this.rootError.set(rootError);
	}

	public String getRootLocation() {
		return rootLocation.get();
	}

	public StringProperty rootLocationProperty() {
		return rootLocation;
	}

	public void setRootLocation(String rootLocation) {
		this.rootLocation.set(rootLocation);
	}

	public ObservableList<StackTraceElementUI> getStackTraceElements() {
		return stackTraceElements;
	}

	public ObservableList<CausalSectionUI> getCausalSections() {
		return causalSections;
	}

	public Skin<?> createDefaultSkin() {
		return new LogLineDetailSkin(this);
	}
}
