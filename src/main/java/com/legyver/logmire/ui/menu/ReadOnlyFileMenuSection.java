package com.legyver.logmire.ui.menu;

import com.legyver.core.exception.CoreException;
import com.legyver.fenxlib.api.scene.controls.options.MenuItemOptions;
import com.legyver.fenxlib.core.files.action.OpenFileAction;
import com.legyver.fenxlib.core.menu.options.RecentFilesMenuProducer;
import com.legyver.fenxlib.core.menu.section.MenuSection;
import com.legyver.fenxlib.core.menu.templates.section.AbstractMenuSection;
import com.legyver.logmire.factory.util.OnFileOpen;

import java.util.Arrays;

public class ReadOnlyFileMenuSection extends AbstractMenuSection implements MenuSection {
    public ReadOnlyFileMenuSection(OnFileOpen onFileOpen) throws CoreException {
        super(Arrays.asList(
                new ImportFileMenuItemProducer(fileOpen(onFileOpen)),
                new RecentFilesMenuProducer("legyver.logmire.menu.file.recent")
        ));
    }

    private static MenuItemOptions fileOpen(OnFileOpen onFileOpen) throws CoreException {
        return new MenuItemOptions().builder()
                .text("legyver.logmire.menu.file.open")
                .useTextForName(true)
                .eventHandler(new OpenFileAction("legyver.logmire.file.select.title", null, onFileOpen));
    }
}
