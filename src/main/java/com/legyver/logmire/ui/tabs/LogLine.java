package com.legyver.logmire.ui.tabs;

import com.legyver.logmire.ui.bean.LogLineUI;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import org.apache.commons.lang3.StringUtils;

public class LogLine extends Control {
	private final LogLineUI value;

	public LogLine(LogLineUI logLineUI) {
		this.value = logLineUI;
	}

	public String getTruncated() {
		return value.getTruncated();
	}

	public Skin<?> createDefaultSkin() {
		return new LogLineSkin(this);
	}
}
