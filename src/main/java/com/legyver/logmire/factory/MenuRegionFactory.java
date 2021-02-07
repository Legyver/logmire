package com.legyver.logmire.factory;

import com.legyver.fenxlib.core.api.locator.query.ComponentQuery;
import com.legyver.fenxlib.core.impl.factory.TextFieldFactory;
import com.legyver.fenxlib.core.impl.factory.TopRegionFactory;
import com.legyver.fenxlib.core.impl.factory.menu.*;
import com.legyver.fenxlib.core.impl.factory.menu.file.OpenFileDecorator;
import com.legyver.fenxlib.core.impl.factory.menu.file.RecentlyOpenedFileFactory;
import com.legyver.fenxlib.core.impl.factory.options.BorderPaneInitializationOptions;
import com.legyver.fenxlib.widgets.about.AboutMenuItemFactory;
import com.legyver.logmire.MainApplication;
import com.legyver.logmire.factory.util.OnFileOpen;
import com.legyver.logmire.task.openlog.OpenLogfileMenuFactory;
import javafx.scene.layout.StackPane;

import java.util.Optional;

public class MenuRegionFactory extends TopRegionFactory {

	public MenuRegionFactory(MainApplication mainApplication, OnFileOpen onFileOpen) {
		super(new LeftMenuOptions(
						new MenuFactory("File",
								new OpenFileDecorator("Open", "Select logfile to open", new OpenLogfileMenuFactory(), fileOptions -> {
									onFileOpen.accept(fileOptions.getFile());
								}),
								new RecentlyOpenedFileFactory("Recent", onFileOpen),
								new ExitMenuItemFactory("Exit")
						)
				),
				new CenterOptions(new TextFieldFactory(false)),
				new RightMenuOptions(
						new MenuFactory("Help", new AboutMenuItemFactory("About", MenuRegionFactory::centerContentReference, mainApplication.getLogmireVersionInfo().getAboutPageOptions()))
				));
	}

	public static StackPane centerContentReference() {
		Optional<StackPane> center = new ComponentQuery.QueryBuilder()
				.inRegion(BorderPaneInitializationOptions.REGION_CENTER)
				.type(StackPane.class).execute();
		return center.get();
	}
}
