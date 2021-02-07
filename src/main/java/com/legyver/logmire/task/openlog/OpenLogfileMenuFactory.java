package com.legyver.logmire.task.openlog;

import com.legyver.fenxlib.core.impl.factory.menu.file.AbstractFileMenuFactory;
import javafx.stage.FileChooser;

public class OpenLogfileMenuFactory extends AbstractFileMenuFactory {
	public OpenLogfileMenuFactory() {
		super();
	}

	@Override
	protected FileChooser getFileChooser(String title) {
		return this.fileOptionsChooserFactory.makeFileChooser(title, new FileChooser.ExtensionFilter[]{new FileChooser.ExtensionFilter("Log", new String[]{"*.log"})});
	}
}
