package com.legyver.logmire.ui.bean;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.concurrent.Semaphore;

public abstract class BaseLogEntry {

	/**
	 * lines of stacktrace, extracted from subsequent lines
	 * ie: at com.example.Class.method(Class:123)
	 * since we eagerly add lines, this needs to be observable so entries can be added as they come in
	 */
	private final ObservableList<StackTraceElementUI> stackTraceElements = FXCollections.observableArrayList();
	/**
	 * Actual message extracted from the first line.
	 * If the line contains a colon, this will be whatever follows it, as long as it's not blank.
	 */
	private String shortMessage;

	private final Semaphore mutex = new Semaphore(1);

	public boolean acquireLock() {
		try {
			mutex.acquire();
			return true;
		} catch (InterruptedException e) {
//			logger.error("Error acquiring lock", e);
			return false;
		}
	}

	public void releaseLock() {
		mutex.release();
	}

	public String getShortMessage() {
		return shortMessage;
	}

	public void setShortMessage(String shortMessage) {
		this.shortMessage = shortMessage;
	}

	public ObservableList<StackTraceElementUI> getStackTraceElements() {
		return stackTraceElements;
	}

	public void addStackTraceElement(String stackTraceElementLine, String location, String copyableClassRef) {
		acquireLock();
		this.stackTraceElements.add(new StackTraceElementUI(stackTraceElementLine, location, copyableClassRef));
		releaseLock();
	}
}
