package com.legyver.logmire.ui.tabs;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public class PackageFilter extends Control {
	private final BooleanProperty hidePackage = new SimpleBooleanProperty();

	public PackageFilter() {
		getStyleClass().add("package-filter");
	}

	public boolean isHidePackage() {
		return hidePackage.get();
	}

	public BooleanProperty hidePackageProperty() {
		return hidePackage;
	}

	public void setHidePackage(boolean hidePackage) {
		this.hidePackage.set(hidePackage);
	}

	public Skin<?> createDefaultSkin() {
		return new PackageFilterSkin(this);
	}
}
