package com.legyver.logmire.ui.tabs;

import com.jfoenix.svg.SVGGlyph;
import com.jfoenix.svg.SVGGlyphLoader;
import com.legyver.fenxlib.core.context.ApplicationContext;
import com.legyver.logmire.config.ApplicationOptionsBuilder;
import com.legyver.logmire.ui.ApplicationUIModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Paint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PackageFilterSkin extends SkinBase<PackageFilter> {
	private static final Logger logger = LogManager.getLogger(PackageFilterSkin.class);
	public static final String DARK_GREY = "#36423d";
	public static final String DARK_RED = "#660202";
	private SVGGlyph showHide;

	public PackageFilterSkin(PackageFilter packageFilter) {
		super(packageFilter);
		showHide = loadIcon(ApplicationOptionsBuilder.FONTAWESOME_FREE_REGULAR, "eye-slash");
		showHide.setSize(20);
		setColor(null, !packageFilter.isHidePackage(), packageFilter.isHidePackage());
		packageFilter.hidePackageProperty().addListener(this::setColor);


		ContextMenu contextMenu = new ContextMenu();
		ApplicationUIModel applicationUIModel = (ApplicationUIModel) ApplicationContext.getUiModel();
		applicationUIModel.getPackageFilters().entrySet().stream().forEach(e-> {
			BooleanProperty hideProperty = e.getValue();
			SVGGlyph checkmark = loadIcon(ApplicationOptionsBuilder.FONTAWESOME_FREE_SOLID, "check");
			checkmark.setSize(9);
			checkmark.setFill(Paint.valueOf(DARK_GREY));
			checkmark.visibleProperty().bind(hideProperty);

			MenuItem menuItem = new MenuItem(e.getKey(), checkmark);
			menuItem.setOnAction(actionEvent -> hideProperty.set(!hideProperty.get()));
			contextMenu.getItems().add(menuItem);
		});

		packageFilter.setContextMenu(contextMenu);
		packageFilter.setOnMouseClicked(actionEvent -> {
			if (MouseButton.PRIMARY == actionEvent.getButton()) {
				packageFilter.setHidePackage(!packageFilter.isHidePackage());
			}
		});
		getChildren().add(showHide);
	}

	private SVGGlyph loadIcon(String prefix, String iconName) {
		try {
			return SVGGlyphLoader.getIcoMoonGlyph(prefix + "." + iconName);
		} catch (Exception exception) {
			logger.error("Error loading " + prefix + " icon: " + iconName, exception);
		}
		return null;
	}

	private void setColor(ObservableValue observableValue, Boolean oldValue, Boolean newValue) {
		if (newValue) {
			showHide.setFill(Paint.valueOf(DARK_RED));
		} else {
			showHide.setFill(Paint.valueOf(DARK_GREY));
		}
	}
}
