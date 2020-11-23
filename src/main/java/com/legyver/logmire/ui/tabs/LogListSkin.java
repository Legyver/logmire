package com.legyver.logmire.ui.tabs;

import com.legyver.logmire.event.ResetType;
import com.legyver.logmire.ui.bean.LogLineUI;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SkinBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogListSkin extends SkinBase<LogList> {
	private static final Logger logger = LogManager.getLogger();
	private final ListView<LogLineView> logs;

	public LogListSkin(LogList control) {
		super(control);
		logs = new ListView<>();
//		logs.itemsProperty().bind(control.getFilterableLogContext().showItems());
		control.getFilterableLogContext().showItems().addListener(new ListChangeListener<LogLineView>() {
			@Override
			public void onChanged(Change<? extends LogLineView> c) {
				//doing this because binding results in weird blank lines being added
				if (c.next()) {
					if (c.wasRemoved()) {
						logger.debug("resetting list");
						logs.getItems().clear();
					}
					if (c.wasAdded()) {
						logger.debug("adding lines");
						logs.getItems().addAll(c.getAddedSubList());
					}
				}
			}
		});

		MultipleSelectionModel<LogLineView> selectionModel = logs.getSelectionModel();
		selectionModel.setSelectionMode(SelectionMode.SINGLE);
		ObservableList<LogLineView> selectedItems = selectionModel.getSelectedItems();
		selectedItems.addListener((ListChangeListener<LogLineView>) c -> {
			logger.debug("selected items changed");
			if (c.next()) {
				logger.debug("selected items changed acknowledged");
				LogLineView selected = selectionModel.getSelectedItem();
				if (selected != null && selected.getValue() != null) {
					control.getLogView().setFocusLogLine(selected.getValue());
				}
			}
		});
		control.getFilterableLogContext().focusLogLineProperty().addListener(new ChangeListener<LogLineUI>() {
			@Override
			public void changed(ObservableValue<? extends LogLineUI> observable, LogLineUI oldValue, LogLineUI newValue) {
				logger.debug("Clearing old selection");
//				selectionModel.clearSelection();
				if (newValue != null) {
					logger.debug("Setting new selection");
					selectionModel.select(newValue.getLogLineView());
//					logger.debug("Focussing on new selection");
//					int selected = selectionModel.getSelectedIndex();
//					logs.getFocusModel().focus(selected);
				}
			}
		});

		control.getFilterableLogContext().reset(ResetType.INITIAL_LOAD);
		getChildren().add(logs);
	}
}
