package com.legyver.logmire.ui.tabs;

import com.legyver.logmire.ui.search.FilterableLogContext;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public class PackageFilter extends Control {
	private final BooleanProperty hidePackage = new SimpleBooleanProperty();
	private final FilterableLogContext filterableLogContext;

	public PackageFilter(FilterableLogContext filterableLogContext) {
		this.filterableLogContext = filterableLogContext;
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

	public FilterableLogContext getFilterableLogContext() {
		return filterableLogContext;
	}

	public Skin<?> createDefaultSkin() {
		return new PackageFilterSkin(this);
	}
}
