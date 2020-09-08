package com.legyver.logmire.ui.tabs;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public class ToggleControl extends Control {
	private final BooleanProperty engaged = new SimpleBooleanProperty(false);
	private final StringProperty text = new SimpleStringProperty();

	public ToggleControl(String trace) {
		text.set(trace);
	}

	public String getText() {
		return text.get();
	}

	public StringProperty textProperty() {
		return text;
	}

	public void setText(String text) {
		this.text.set(text);
	}

	public boolean isEngaged() {
		return engaged.get();
	}

	public BooleanProperty engagedProperty() {
		return engaged;
	}

	public void setEngaged(boolean engaged) {
		this.engaged.set(engaged);
	}

	public Skin<?> createDefaultSkin() {
		return new ToggleControlSkin(this);
	}

}
