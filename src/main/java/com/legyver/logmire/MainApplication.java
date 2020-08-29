package com.legyver.logmire;

import com.legyver.core.exception.CoreException;
import com.legyver.fenxlib.core.config.options.ApplicationOptions;
import com.legyver.fenxlib.core.context.ApplicationContext;
import com.legyver.fenxlib.core.factory.*;
import com.legyver.fenxlib.core.factory.menu.*;
import com.legyver.fenxlib.core.factory.menu.file.OpenFileDecorator;
import com.legyver.fenxlib.core.factory.options.BorderPaneInitializationOptions;
import com.legyver.fenxlib.core.factory.options.RegionInitializationOptions;
import com.legyver.fenxlib.core.locator.query.ComponentQuery;
import com.legyver.fenxlib.core.uimodel.FileOptions;
import com.legyver.fenxlib.core.widget.about.AboutMenuItemFactory;
import com.legyver.fenxlib.core.widget.about.AboutPageOptions;
import com.legyver.logmire.config.ApplicationOptionsBuilder;
import com.legyver.logmire.config.BindingFactory;
import com.legyver.logmire.config.LogmireConfig;
import com.legyver.logmire.config.LogmireVersionInfo;
import com.legyver.logmire.task.TaskFactory;
import com.legyver.logmire.task.openlog.OpenLogfileMenuFactory;
import com.legyver.logmire.task.openlog.OpenLogfileProcessor;
import com.legyver.logmire.ui.ApplicationUIModel;
import com.legyver.logmire.ui.bean.DataSourceUI;
import javafx.application.Application;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.legyver.logmire.config.BindingFactory.LOG_TABS;

public class MainApplication extends Application  {
	private static Logger logger;
	public static final String TOGGLE_CONTROLS = "Controls";

	private static ApplicationOptions applicationOptions;
	private BindingFactory bindingFactory;

	public static void main(String[] args) throws CoreException {
		applicationOptions = new ApplicationOptionsBuilder()
				.appName("Logmire")
				.customAppConfigInstantiator(map -> new LogmireConfig(map))
				.uiModel(new ApplicationUIModel())
				.build();//build() calls bootstrap() which inits logging
		logger = LogManager.getLogger(MainApplication.class);
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			logger.info("Initializing application");
			applicationOptions.startup();
			bindingFactory = new BindingFactory();

			ApplicationUIModel uiModel = (ApplicationUIModel) ApplicationContext.getUiModel();
			LogmireConfig applicationConfig = (LogmireConfig) ApplicationContext.getApplicationConfig();
			LogmireVersionInfo logmireVersionInfo = new LogmireVersionInfo();

			Supplier<StackPane> centerContentReference = () -> {
				Optional<StackPane> center = new ComponentQuery.QueryBuilder()
						.inRegion(BorderPaneInitializationOptions.REGION_CENTER)
						.type(StackPane.class).execute();
				return center.get();
			};

			SceneFactory sceneFactory = new SceneFactory(primaryStage, 1100, 750, MainApplication.class.getClassLoader().getResource("css/application.css"));

			TaskFactory taskFactory = new TaskFactory();

			OpenLogfileProcessor importProcessor = new OpenLogfileProcessor(taskFactory, bindingFactory, applicationConfig, logmireVersionInfo.getBuildProperties());

			Consumer<File> fileSelectionConsumer = file -> {
				Optional<DataSourceUI> preexistingDataSource = uiModel.getOpenSources().stream()
						.filter(ds -> ds.getSource().getAbsolutePath().equals(file.getAbsolutePath()))
						.findFirst();
				if (preexistingDataSource.isPresent()) {
					//if it is already open, make tab active
					uiModel.setActiveSource(preexistingDataSource.get());
				} else {
					//import it and add it to tabs
					DataSourceUI dataSource = importProcessor.onNewLogfileSelected(file);
					uiModel.addSource(dataSource);
				}
			};

			BorderPaneInitializationOptions options = new BorderPaneInitializationOptions.Builder()
					.center(new RegionInitializationOptions.Builder()
							//popup will display over this. See the centerContentReference Supplier above
							.factory(new StackPaneRegionFactory(true, new JFXTabPaneFactory(LOG_TABS)))
					)
					.top(new RegionInitializationOptions.Builder()
							.displayContentByDefault()
							.factory(new TopRegionFactory(
									new LeftMenuOptions(
											new MenuFactory("File",
													new OpenFileDecorator("Open", "Select logfile to open", new OpenLogfileMenuFactory(), fileOptions -> {
														fileSelectionConsumer.accept(fileOptions.getFile());
													}),
													new ExitMenuItemFactory("Exit")
											)
									),
									new CenterOptions(new TextFieldFactory(false)),
									new RightMenuOptions(
											new MenuFactory("Help", new AboutMenuItemFactory("About", centerContentReference, logmireVersionInfo.getAboutPageOptions()))
									)
							))
					)
					.bottom(new RegionInitializationOptions.SideAwareBuilder()
							.factory(BottomRegionFactory.INSTANCE)
					)
//					.right(new RegionInitializationOptions.SideBuilder(TaskPanelFactory.TASKS_MENU_TOGGLE_BUTTON)
//						.factory(
//							new AccordionMenuFactory(new TitledPaneFactory(TaskPanelFactory.TASKS_PANE_TITLE, new TaskPanelFactory())
//						)
//					)
			.build();

			BorderPane root = new BorderPaneFactory(options).makeBorderPane();

			primaryStage.setScene(sceneFactory.makeScene(root));
			primaryStage.setTitle("Logmire");
			primaryStage.show();
		} catch (Exception ex) {
			logger.error("Error in MainApplication.start() " + ex.getMessage(), ex);
			System.exit(1);
		}

	}

}
