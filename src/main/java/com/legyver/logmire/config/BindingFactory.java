package com.legyver.logmire.config;

import com.jfoenix.controls.JFXTabPane;

import com.legyver.fenxlib.core.factory.options.BorderPaneInitializationOptions;
import com.legyver.fenxlib.core.locator.query.ComponentQuery;
import com.legyver.fenxlib.extensions.tuktukfx.bindings.TaskAbortBindingFactory;
import com.legyver.logmire.ui.bean.DataSourceUI;
import com.legyver.logmire.ui.tabs.LogView;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;

import java.util.Optional;

public class BindingFactory implements TaskAbortBindingFactory {
	public static final String LOG_TABS = "logtabs";

	public void bindNewTab(DataSourceUI dataSource) {
		LogView logView = new LogView(dataSource);
		Tab tab = new Tab(dataSource.getSourceName(), logView);
		tab.setTooltip(new Tooltip(dataSource.getSourcePath()));

		Optional<JFXTabPane> tabPaneOptional = new ComponentQuery.QueryBuilder()
				.inRegion(BorderPaneInitializationOptions.REGION_CENTER)
				.named(LOG_TABS).execute();
		JFXTabPane tabPane = tabPaneOptional.get();
		tabPane.getTabs().add(tab);
		tabPane.getSelectionModel().select(tabPane.getTabs().size() - 1);
	}
}
