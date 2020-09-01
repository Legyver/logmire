package com.legyver.logmire.ui.tabs;

import com.legyver.logmire.ui.bean.LogLineUI;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public class LogLine extends Control {
	private final LogLineUI value;

	public LogLine(LogLineUI logLineUI) {
		this.value = logLineUI;
	}

	public LogLineUI getValue() {
		return value;
	}

	public String getTruncated() {
		return value.getTruncated();
	}

	public Skin<?> createDefaultSkin() {
		return new LogLineSkin(this);
	}
}
