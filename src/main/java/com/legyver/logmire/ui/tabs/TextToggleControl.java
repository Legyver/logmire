package com.legyver.logmire.ui.tabs;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public class TextToggleControl extends Control {
	private final BooleanProperty showLevel = new SimpleBooleanProperty();
	private final StringProperty text;

	public TextToggleControl(String text) {
		this.text = new SimpleStringProperty(text);
		getStyleClass().add("text-toggle");
	}

	public String getText() {
		return text.get();
	}

	public StringProperty textProperty() {
		return text;
	}

	public boolean getShowLevel() {
		return showLevel.get();
	}

	public BooleanProperty showLevelProperty() {
		return showLevel;
	}

	public void setShowLevel(boolean showLevel) {
		this.showLevel.set(showLevel);
	}

	public Skin<?> createDefaultSkin() {
		return new TextToggleControlSkin(this);
	}
}
