package com.legyver.logmire.ui.tabs;

import com.legyver.logmire.event.ResetType;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class SearchControlSkin extends SkinBase<SearchControl> {
	private final TextField searchField;

	public SearchControlSkin(SearchControl searchControl) {
		super(searchControl);
		searchField = new TextField();
		searchField.setPrefWidth(300);
		searchField.setMaxWidth(600);
		searchControl.searchTextProperty().bind(searchField.textProperty());
		searchField.disableProperty().bind(searchControl.enabledProperty());
		searchField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (oldValue == null && newValue == null) {
					return;
				}
				if (oldValue != null && newValue != null) {
					if (newValue.length() > oldValue.length()) {
						searchControl.getFilterableLogContext().searchFilterAdded(newValue);
					} else {
						searchControl.getFilterableLogContext().searchFilterRemoved(newValue);
					}
				} else {
					searchControl.getFilterableLogContext().reset(ResetType.SEARCH_CLEARED);
				}
			}
		});

		HBox hbox = new HBox(searchField);
		hbox.setSpacing(4);
		HBox.setHgrow(searchField, Priority.ALWAYS);
		getChildren().add(hbox);
	}
}
