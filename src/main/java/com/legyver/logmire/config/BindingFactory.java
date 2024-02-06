package com.legyver.logmire.config;


import com.legyver.core.exception.CoreException;
import com.legyver.fenxlib.api.locator.query.ComponentQuery;
import com.legyver.fenxlib.core.layout.BorderPaneApplicationLayout;
import com.legyver.fenxlib.core.layout.options.BorderPaneInitializationOptions;
import com.legyver.logmire.ui.bean.DataSourceUI;
import com.legyver.logmire.ui.tabs.LogView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;

import java.util.Optional;

public class BindingFactory {
	public static final String LOG_TABS = "logtabs";

	public void bindNewTab(DataSourceUI dataSource) throws CoreException {
		LogView logView = new LogView(dataSource);

		Tab tab = new Tab(dataSource.getSourceName(), logView);
		tab.setTooltip(new Tooltip(dataSource.getSourcePath()));

		Optional<TabPane> tabPaneOptional = new ComponentQuery.QueryBuilder()
				.inRegion(BorderPaneApplicationLayout.CENTER)
				.named(LOG_TABS).execute();
		TabPane tabPane = tabPaneOptional.get();
		tabPane.getTabs().add(tab);
		tabPane.getSelectionModel().select(tabPane.getTabs().size() - 1);
	}
}
