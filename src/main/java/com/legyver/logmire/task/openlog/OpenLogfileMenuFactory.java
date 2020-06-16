package com.legyver.logmire.task.openlog;

import com.legyver.fenxlib.factory.menu.file.AbstractFileMenuFactory;
import com.legyver.fenxlib.factory.menu.file.WorkingFileConfig;
import javafx.stage.FileChooser;

public class OpenLogfileMenuFactory extends AbstractFileMenuFactory {
	public OpenLogfileMenuFactory(WorkingFileConfig workingFileConfig) {
		super(workingFileConfig);
	}

	@Override
	protected FileChooser getFileChooser(String title) {
		return this.fileOptionsChooserFactory.makeFileChooser(title, new FileChooser.ExtensionFilter[]{new FileChooser.ExtensionFilter("Log", new String[]{"*.log"})});
	}
}
