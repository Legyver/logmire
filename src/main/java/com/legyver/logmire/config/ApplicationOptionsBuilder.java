package com.legyver.logmire.config;

import com.legyver.fenxlib.core.config.options.ApplicationOptions;
import com.legyver.fenxlib.extensions.tuktukfx.config.TaskLifecycleMixin;

public class ApplicationOptionsBuilder extends ApplicationOptions.Builder<ApplicationOptionsBuilder> implements TaskLifecycleMixin {

	public ApplicationOptionsBuilder() {
		super();
		registerLifecycleHook(shutDownThreadPoolOnExit());
	}

}
