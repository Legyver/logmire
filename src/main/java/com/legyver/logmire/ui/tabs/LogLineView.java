package com.legyver.logmire.ui.tabs;

import com.legyver.logmire.ui.bean.LogLineUI;
import javafx.event.EventHandler;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.input.MouseEvent;

public class LogLineView extends Control {
	private final LogLineUI value;

	public LogLineView(LogLineUI logLineUI) {
		this.value = logLineUI;
		logLineUI.setLogLineView(this);
	}

	public LogLineUI getValue() {
		return value;
	}

	public String getSeverity() {
		return value.getSeverity();
	}

	public String getReporter() {
		return value.getReporter();
	}

	public String getExecutor() {
		return value.getExecutor();
	}

	public String getTruncated() {
		return value.getTruncated();
	}

	public Skin<?> createDefaultSkin() {
		return new LogLineViewSkin(this);
	}
}
