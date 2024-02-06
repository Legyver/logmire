package com.legyver.logmire.config;

import com.legyver.fenxlib.api.config.ApplicationConfig;
import com.legyver.fenxlib.api.config.options.ApplicationOptions;
import com.legyver.fenxlib.api.context.ApplicationContext;
import com.legyver.fenxlib.core.lifecycle.hooks.PreShutdownConfigSyncLifecycleHook;
import com.legyver.fenxlib.core.lifecycle.hooks.UiModelConfigInitializer;
import com.legyver.fenxlib.extensions.tuktukfx.config.TaskExecutorShutdownApplicationLifecycleHook;
import com.legyver.logmire.ui.ApplicationUIModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableMap;

import java.util.stream.Collectors;

public class ApplicationOptionsBuilder extends ApplicationOptions.Builder<ApplicationOptionsBuilder> implements IconConstants {

	public ApplicationOptionsBuilder() {
		super();
		registerLifecycleHook(new TaskExecutorShutdownApplicationLifecycleHook());
		registerLifecycleHook(new PreShutdownConfigSyncLifecycleHook(){
			@Override
			public int getPriority() {
				return super.getPriority() + 1;
			}

			@Override
			protected void syncToConfig(ApplicationConfig applicationConfig) {
				ApplicationUIModel applicationUIModel = (ApplicationUIModel) ApplicationContext.getUiModel();
				syncPackageFiltersToConfig((LogmireConfig) applicationConfig, applicationUIModel);
				syncSeverityFiltersToConfig((LogmireConfig) applicationConfig, applicationUIModel);
			}
		});
//		registerLifecycleHook(new SVGGlyphLoadingApplicationLifecycleHook(FONTAWESOME_FREE_REGULAR, "/fonts/fa-free-regular.svg"));
//		registerLifecycleHook(new SVGGlyphLoadingApplicationLifecycleHook(FONTAWESOME_FREE_SOLID, "/fonts/fa-free-solid.svg"));
		registerLifecycleHook(new UiModelConfigInitializer<ApplicationUIModel>() {
			@Override
			protected void syncToUiModel(ApplicationConfig applicationConfig, ApplicationUIModel uiModel) {
				initPackageFilters((LogmireConfig) applicationConfig, uiModel);
				initSeverityFilters((LogmireConfig) applicationConfig, uiModel);
			}
		});
	}

	private void initPackageFilters(LogmireConfig applicationConfig, ApplicationUIModel uiModel) {
		ObservableMap<String, BooleanProperty> filters = uiModel.getPackageFilters();
		applicationConfig.getPackageFilters().entrySet().stream().forEach(e -> filters.put(e.getKey(), new SimpleBooleanProperty(e.getValue())));
		if (filters.isEmpty()) {
			initializeJavaDefaultInternalPackages(filters);
		}
	}

	private void initSeverityFilters(LogmireConfig applicationConfig, ApplicationUIModel uiModel) {
		ObservableMap<String, BooleanProperty> filters = uiModel.getSeverityFilters();
		applicationConfig.getSeverityFilters().entrySet().stream().forEach(e -> filters.put(e.getKey(), new SimpleBooleanProperty(e.getValue())));
		if (filters.isEmpty()) {
			initializeJavaDefaultSeverities(filters);
		}
	}

	private void initializeJavaDefaultInternalPackages(ObservableMap<String, BooleanProperty> filters) {
		filters.put("java.", new SimpleBooleanProperty(false));//to stay consistent with severityFilters, true means they will be shown
		filters.put("javax.", new SimpleBooleanProperty(false));
		filters.put("javafx.", new SimpleBooleanProperty(false));
		filters.put("sun.", new SimpleBooleanProperty(false));
		filters.put("org.springframework.", new SimpleBooleanProperty(false));
		filters.put("com.oracle.", new SimpleBooleanProperty(false));
		filters.put("org.apache.", new SimpleBooleanProperty(false));
		filters.put("org.jboss.", new SimpleBooleanProperty(false));
	}

	private void initializeJavaDefaultSeverities(ObservableMap<String, BooleanProperty> filters) {
		filters.put("TRACE", new SimpleBooleanProperty(false));
		filters.put("DEBUG", new SimpleBooleanProperty(false));
		filters.put("INFO", new SimpleBooleanProperty(false));
		filters.put("WARN", new SimpleBooleanProperty(true));
		filters.put("ERROR", new SimpleBooleanProperty(true));
		filters.put("FATAL", new SimpleBooleanProperty(true));
	}

	private void syncPackageFiltersToConfig(LogmireConfig applicationConfig, ApplicationUIModel applicationUIModel) {
		applicationConfig.setPackageFilters(applicationUIModel.getPackageFilters().entrySet().stream()
				.collect(Collectors.toMap(
						e -> e.getKey(),//packageName
						e -> e.getValue().get())//true/false
				));
	}

	private void syncSeverityFiltersToConfig(LogmireConfig applicationConfig, ApplicationUIModel applicationUIModel) {
		applicationConfig.setSeverityFilters(applicationUIModel.getSeverityFilters().entrySet().stream()
				.collect(Collectors.toMap(
						e -> e.getKey(),//severity
						e -> e.getValue().get())//true/false
				));
	}


}
