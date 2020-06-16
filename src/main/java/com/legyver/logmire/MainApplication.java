package com.legyver.logmire;

import com.legyver.fenxlib.factory.*;
import com.legyver.fenxlib.factory.menu.*;
import com.legyver.fenxlib.factory.menu.file.OpenFileDecorator;
import com.legyver.fenxlib.factory.menu.file.WorkingFileConfig;
import com.legyver.fenxlib.factory.options.BorderPaneInitializationOptions;
import com.legyver.fenxlib.locator.query.ComponentQuery;
import com.legyver.fenxlib.tasks.factory.TaskPanelFactory;
import com.legyver.fenxlib.uimodel.FileOptions;
import com.legyver.fenxlib.util.GuiUtil;
import com.legyver.fenxlib.widget.about.AboutMenuItemFactory;
import com.legyver.fenxlib.widget.about.AboutPageOptions;
import com.legyver.logmire.config.BindingFactory;
import com.legyver.logmire.config.LogmireApplicationOptions;
import com.legyver.logmire.config.LogmireConfig;
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

public class MainApplication extends Application {
	private static final Logger logger = LogManager.getLogger(MainApplication.class);
	public static final String TOGGLE_CONTROLS = "Controls";

	private BindingFactory bindingFactory;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			logger.info("Starting application");

			LogmireApplicationOptions applicationOptions = new LogmireApplicationOptions(primaryStage);
			GuiUtil.init(applicationOptions);
			bindingFactory = new BindingFactory(applicationOptions);

			Supplier<StackPane> centerContentReference = () -> {
				Optional<StackPane> center = new ComponentQuery.QueryBuilder(applicationOptions.getComponentRegistry())
						.inRegion(BorderPaneInitializationOptions.REGION_CENTER)
						.type(StackPane.class).execute();
				return center.get();
			};

			AboutPageOptions aboutPageOptions = new AboutPageOptions.Builder(getClass())
					.dependenciesFile("licenses/license.properties")
					.buildPropertiesFile("buildlabel.properties")
					.copyrightPropertiesFile("licenses/copyright.properties")
					.title("Logmire")
					.intro("An logfile monitoring desktop client")
					.build();
			Properties buildProperties = aboutPageOptions.getBuildProperties();

			SceneFactory sceneFactory = new SceneFactory(primaryStage, 1100, 750, MainApplication.class.getClassLoader().getResource("css/application.css"));
			ApplicationUIModel uiModel = applicationOptions.getUiModel();

			TaskFactory taskFactory = new TaskFactory();
			LogmireConfig applicationConfig = (LogmireConfig) applicationOptions.getApplicationConfig();

			OpenLogfileProcessor importProcessor = new OpenLogfileProcessor(taskFactory, bindingFactory, applicationConfig, buildProperties);
			FileOptions workingFile = uiModel.getWorkingFileOptions();
			WorkingFileConfig workingFileConfig = applicationOptions.getWorkingFileConfig();
			Consumer<File> fileSelectionConsumer = file -> {
				workingFileConfig.setInitialDirectory(file.getParentFile());
				workingFile.setFile(file);
				workingFile.setFilePath(file.getAbsolutePath());
				workingFile.setFileName(file.getName());
				workingFile.setNewFile(false);//once it has been opened/saved-as it can be saved via save option
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
					.center()
					//popup will display over this. See the centerContentReference Supplier above
					.factory(new StackPaneRegionFactory(true, new JFXTabPaneFactory(LOG_TABS)))
					.up().top()
					.displayContentByDefault()
					.factory(new TopRegionFactory(
									new LeftMenuOptions(
											new MenuFactory("File",
													new OpenFileDecorator("Open", "Select logfile to open", new OpenLogfileMenuFactory(workingFileConfig), file -> {
														fileSelectionConsumer.accept(file);
													}),
													new ExitMenuItemFactory("Exit")
											)
									),
									new CenterOptions(new TextFieldFactory(false)),
									new RightMenuOptions(
											new MenuFactory("Help", new AboutMenuItemFactory("About", centerContentReference, aboutPageOptions))
									)
							)
					).up().bottom().factory(BottomRegionFactory.INSTANCE)
					.up().right(TaskPanelFactory.TASKS_MENU_TOGGLE_BUTTON).factory(
							new AccordionMenuFactory(new TitledPaneFactory(TaskPanelFactory.TASKS_PANE_TITLE, new TaskPanelFactory()))
					).up().build();

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
