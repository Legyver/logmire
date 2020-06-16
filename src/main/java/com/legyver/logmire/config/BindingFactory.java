package com.legyver.logmire.config;

import com.jfoenix.controls.JFXTabPane;
import com.legyver.fenxlib.factory.options.BorderPaneInitializationOptions;
import com.legyver.fenxlib.locator.query.ComponentQuery;
import com.legyver.fenxlib.locator.query.DefaultComponentRegistry;
import com.legyver.fenxlib.tuktukfx.bindings.TaskBindingFactory;
import com.legyver.logmire.ui.bean.DataSourceUI;
import com.legyver.logmire.ui.tabs.LogView;
import javafx.scene.control.Tab;

import java.util.Optional;

public class BindingFactory extends TaskBindingFactory {
	public static final String LOG_TABS = "logtabs";
	private final DefaultComponentRegistry registry;

	public BindingFactory(LogmireApplicationOptions applicationOptions) {
		super(applicationOptions);
		this.registry = applicationOptions.getComponentRegistry();
	}

	public void bindNewTab(DataSourceUI dataSource) {
		LogView logView = new LogView(dataSource);
		Tab tab = new Tab(dataSource.getSourceName(), logView);

		Optional<JFXTabPane> tabPaneOptional = new ComponentQuery.QueryBuilder(registry)
				.inRegion(BorderPaneInitializationOptions.REGION_CENTER)
				.named(LOG_TABS).execute();
		JFXTabPane tabPane = tabPaneOptional.get();
		tabPane.getTabs().add(tab);
		tabPane.getSelectionModel().select(tabPane.getTabs().size() - 1);
	}
}
