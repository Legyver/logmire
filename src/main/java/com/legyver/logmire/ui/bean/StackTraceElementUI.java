package com.legyver.logmire.ui.bean;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class StackTraceElementUI {
	private final String stackTraceElementLine;
	private final String location;
	private final String copyableClassRef;

	public StackTraceElementUI(String stackTraceElementLine, String location, String copyableClassRef) {
		this.stackTraceElementLine = stackTraceElementLine;
		this.location = location;
		this.copyableClassRef = copyableClassRef;
	}

	public String getStackTraceElementLine() {
		return stackTraceElementLine;
	}

	public String getLocation() {
		return location;
	}

	public String getCopyableClassRef() {
		return copyableClassRef;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		StackTraceElementUI other = (StackTraceElementUI) obj;
		if ((stackTraceElementLine == null && other.stackTraceElementLine != null)
			|| (location == null && other.location != null)
			|| (copyableClassRef == null && other.copyableClassRef != null)) {
			return false;
		}

		return stackTraceElementLine.equals(other.getStackTraceElementLine())
				&& location.equals(other.getLocation())
				&& copyableClassRef.equals(other.getCopyableClassRef());
	}

	@Override
	public String toString() {
		return stackTraceElementLine;
	}
}
