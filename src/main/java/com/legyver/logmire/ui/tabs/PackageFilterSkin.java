package com.legyver.logmire.ui.tabs;

import com.legyver.fenxlib.api.context.ApplicationContext;
import com.legyver.fenxlib.api.icons.options.IconOptions;
import com.legyver.fenxlib.controls.icon.IconControl;
import com.legyver.fenxlib.icons.standard.IcoMoonFontEnum;
import com.legyver.fenxlib.icons.standard.IcoMoonIconOptions;
import com.legyver.logmire.event.ResetType;
import com.legyver.logmire.ui.ApplicationUIModel;
import com.legyver.logmire.ui.search.FilterableLogContext;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PackageFilterSkin extends SkinBase<PackageFilter> {
	private static final Logger logger = LogManager.getLogger(PackageFilterSkin.class);
	public static final String DARK_GREY = "#36423d";
	public static final String DARK_RED = "#660202";
	private IconControl showHideControl;

	public PackageFilterSkin(PackageFilter packageFilter) {
		super(packageFilter);
		showHideControl = new IconControl();
		IconOptions showHideIcon = new IcoMoonIconOptions.Builder()
				.icoMoonIcon(IcoMoonFontEnum.ICON_EYE)
				.iconColor(Color.GREEN)
				.iconSize(20)
				.build();
		showHideControl.setIconOptions(showHideIcon);
		setColor(null, !packageFilter.isHidePackage(), packageFilter.isHidePackage());
		packageFilter.hidePackageProperty().addListener(this::setColor);
		packageFilter.hidePackageProperty().addListener(this::onPackageToggle);

		ContextMenu contextMenu = new ContextMenu();
		ApplicationUIModel applicationUIModel = (ApplicationUIModel) ApplicationContext.getUiModel();
		applicationUIModel.getPackageFilters().entrySet().stream().forEach(e-> {
			BooleanProperty hideProperty = e.getValue();
			IconControl checkmarkControl = new IconControl();
			IconOptions checkmarkIcon =  new IcoMoonIconOptions.Builder()
					.icoMoonIcon(IcoMoonFontEnum.ICON_CHECKMARK)
					.iconColor(Color.DARKGRAY)
					.iconSize(9)
					.build();
			checkmarkControl.setIconOptions(checkmarkIcon);
			checkmarkControl.visibleProperty().bind(hideProperty);

			MenuItem menuItem = new MenuItem(e.getKey(), checkmarkControl);
			menuItem.setOnAction(actionEvent -> hideProperty.set(!hideProperty.get()));
			contextMenu.getItems().add(menuItem);
		});

		packageFilter.setContextMenu(contextMenu);
		packageFilter.setOnMouseClicked(actionEvent -> {
			if (MouseButton.PRIMARY == actionEvent.getButton()) {
				packageFilter.setHidePackage(!packageFilter.isHidePackage());
			}
		});
		getChildren().add(showHideControl);
	}

	private void onPackageToggle(ObservableValue observable, Boolean oldValue, Boolean newValue) {
		FilterableLogContext filterableLogContext = getSkinnable().getFilterableLogContext();
		filterableLogContext.reset(ResetType.PACKAGE_TOGGLE);
	}


	private void setColor(ObservableValue observableValue, Boolean oldValue, Boolean newValue) {
		if (newValue) {
			showHideControl.iconPaintProperty().set(Paint.valueOf(DARK_RED));
		} else {
			showHideControl.iconPaintProperty().set(Paint.valueOf(DARK_GREY));
		}
	}
}
