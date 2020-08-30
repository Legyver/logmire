package com.legyver.logmire.ui.tabs;

import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import org.apache.commons.lang3.StringUtils;

public class LogLine extends Control {

	private final String plainText;
	private final String truncated;

	public LogLine(String plainText) {
		this.plainText = plainText;
		this.truncated = StringUtils.isBlank(plainText) || plainText.length() < 200 ? plainText : plainText.substring(0, 200);
	}

	public String getPlainText() {
		return plainText;
	}

	public String getTruncated() {
		return truncated;
	}

	public Skin<?> createDefaultSkin() {
		return new LogLineSkin(this);
	}
}
