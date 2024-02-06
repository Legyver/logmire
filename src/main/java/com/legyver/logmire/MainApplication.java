package com.legyver.logmire;

import com.legyver.core.exception.CoreException;
import com.legyver.fenxlib.api.config.options.ApplicationOptions;
import com.legyver.fenxlib.api.context.ApplicationContext;
import com.legyver.fenxlib.api.context.ResourceScope;
import com.legyver.fenxlib.api.controls.ControlsFactory;
import com.legyver.fenxlib.api.scene.controls.options.TabPaneOptions;
import com.legyver.fenxlib.core.controls.factory.SceneFactory;
import com.legyver.fenxlib.core.layout.BorderPaneApplicationLayout;
import com.legyver.fenxlib.core.layout.options.CenterRegionOptions;
import com.legyver.fenxlib.core.menu.templates.MenuBuilder;
import com.legyver.fenxlib.core.menu.templates.section.FileExitMenuSection;
import com.legyver.fenxlib.extensions.tuktukfx.config.TaskExecutorShutdownApplicationLifecycleHook;
import com.legyver.fenxlib.widgets.about.AboutMenuSection;
import com.legyver.fenxlib.widgets.about.AboutPageOptions;
import com.legyver.logmire.config.ApplicationOptionsBuilder;
import com.legyver.logmire.config.BindingFactory;
import com.legyver.logmire.config.LogmireConfig;
import com.legyver.logmire.config.LogmireVersionInfo;
import com.legyver.logmire.factory.util.OnFileOpen;
import com.legyver.logmire.task.TaskFactory;
import com.legyver.logmire.task.openlog.OpenLogfileProcessor;
import com.legyver.logmire.ui.ApplicationUIModel;
import com.legyver.logmire.ui.menu.ReadOnlyFileMenuSection;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainApplication extends Application  {
	private static Logger logger;
	public static final String TOGGLE_CONTROLS = "Controls";

	private static ApplicationOptions applicationOptions;
	private BindingFactory bindingFactory;
	private TaskFactory taskFactory;
	private LogmireVersionInfo logmireVersionInfo;
	private ApplicationUIModel uiModel;

	public static void main(String[] args) throws CoreException {
		applicationOptions = new ApplicationOptionsBuilder()
				.appName("Logmire")
				.applicationConfigClass(LogmireConfig.class)
				.uiModel(new ApplicationUIModel())
				.resourceBundle("com.legyver.logmire.messages")
				.styleSheetUrl(MainApplication.class.getClassLoader().getResource("css/application.css"), ResourceScope.APPLICATION)
				.registerLifecycleHook(new TaskExecutorShutdownApplicationLifecycleHook())
				.build();//build() calls bootstrap() which inits logging
		logger = LogManager.getLogger(MainApplication.class);
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			applicationOptions.startup(this, primaryStage);
			bindingFactory = new BindingFactory();
			uiModel = (ApplicationUIModel) ApplicationContext.getUiModel();
			logmireVersionInfo = new LogmireVersionInfo();
			taskFactory = new TaskFactory();

			OpenLogfileProcessor importProcessor = new OpenLogfileProcessor(taskFactory, bindingFactory);
			OnFileOpen onFileOpen = new OnFileOpen(uiModel, importProcessor);

			TabPane tabPane = ControlsFactory.make(TabPane.class, new TabPaneOptions()
					.name(BindingFactory.LOG_TABS)
			);

			BorderPaneApplicationLayout borderPaneApplicationLayout = new BorderPaneApplicationLayout.BorderPaneBuilder()
					.title("legyver.logmire.title")
					.width(1100.0)
					.height(750.0)
					.menuBar(menuBar(onFileOpen))
					.centerRegionOptions(new CenterRegionOptions(tabPane).reRegisterComponents(true))
//                    .bottomRegionControlOptions(new BottomRegionOptions(statusMonitor))
					.build();

			SceneFactory<BorderPaneApplicationLayout> sceneFactory = new SceneFactory<>(primaryStage);
			Scene scene = sceneFactory.makeScene(borderPaneApplicationLayout);
			primaryStage.setScene(scene);
//			primaryStage.setTitle("Logmire");
			primaryStage.getIcons().add(new Image(MainApplication.class.getResourceAsStream("/legyvinicon.png")));
			primaryStage.show();
		} catch (Exception ex) {
			logger.error("Error in MainApplication.start() " + ex.getMessage(), ex);
			System.exit(1);
		}

	}

	private MenuBar menuBar(OnFileOpen onFileOpen) throws CoreException {
		MenuBar menuBar = new MenuBar();

		Menu fileMenu = new MenuBuilder()
				.name("legyver.logmire.menu.label.file")
				.menuSection(new ReadOnlyFileMenuSection(onFileOpen))
				.menuSection(new FileExitMenuSection())
				.build();
		menuBar.getMenus().add(fileMenu);


		Menu helpMenu = new MenuBuilder()
				.name("legyver.logmire.menu.label.help")
				.menuSection(new AboutMenuSection(getLogmireVersionInfo().getAboutPageOptions()))
				.build();
		menuBar.getMenus().add(helpMenu);

		return menuBar;
	}

	private AboutPageOptions getAboutPageOptions() {
		return getLogmireVersionInfo().getAboutPageOptions();
	}

	public BindingFactory getBindingFactory() {
		return bindingFactory;
	}

	public TaskFactory getTaskFactory() {
		return taskFactory;
	}

	public LogmireVersionInfo getLogmireVersionInfo() {
		return logmireVersionInfo;
	}

	public ApplicationUIModel getUiModel() {
		return uiModel;
	}
}
