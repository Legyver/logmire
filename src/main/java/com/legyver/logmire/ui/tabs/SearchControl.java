package com.legyver.logmire.ui.tabs;

import com.legyver.logmire.ui.search.FilterableLogContext;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public class SearchControl extends Control {
	private final StringProperty searchText = new SimpleStringProperty();
	private final BooleanProperty enabled = new SimpleBooleanProperty(false);
	private final int minimumSearchLength = 3;
	private final FilterableLogContext filterableLogContext;

	public SearchControl(FilterableLogContext filterableLogContext) {
		this.filterableLogContext = filterableLogContext;
	}

	public String getSearchText() {
		return searchText.get();
	}

	public StringProperty searchTextProperty() {
		return searchText;
	}

	public boolean isEnabled() {
		return enabled.get();
	}

	public BooleanProperty enabledProperty() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled.set(enabled);
	}

	public void setSearchText(String searchText) {
		this.searchText.set(searchText);
	}

	public FilterableLogContext getFilterableLogContext() {
		return filterableLogContext;
	}

	public int getMinimumSearchLength() {
		return minimumSearchLength;
	}

	public Skin<?> createDefaultSkin() {
		return new SearchControlSkin(this);
	}

}
