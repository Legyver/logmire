package com.legyver.logmire.ui.menu;

import com.legyver.fenxlib.api.scene.controls.options.MenuItemOptions;
import com.legyver.fenxlib.core.menu.options.AbstractMenuItemProducer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class ImportFileMenuItemProducer extends AbstractMenuItemProducer {

    protected ImportFileMenuItemProducer(String text, EventHandler<ActionEvent> eventHandler) {
        super(text, eventHandler);
    }

    public ImportFileMenuItemProducer(MenuItemOptions menuItemOptions) {
        this(menuItemOptions.getText(), menuItemOptions.getEventHandler());
    }
}
