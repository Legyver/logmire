package com.legyver.logmire;

import com.legyver.core.exception.CoreException;
import com.legyver.fenxlib.core.config.options.ApplicationOptions;
import com.legyver.fenxlib.core.context.ApplicationContext;
import com.legyver.fenxlib.core.factory.*;
import com.legyver.fenxlib.core.factory.options.BorderPaneInitializationOptions;
import com.legyver.fenxlib.core.factory.options.RegionInitializationOptions;
import com.legyver.logmire.config.ApplicationOptionsBuilder;
import com.legyver.logmire.config.BindingFactory;
import com.legyver.logmire.config.LogmireConfig;
import com.legyver.logmire.config.LogmireVersionInfo;
import com.legyver.logmire.factory.MenuRegionFactory;
import com.legyver.logmire.factory.util.OnFileOpen;
import com.legyver.logmire.task.TaskFactory;
import com.legyver.logmire.task.openlog.OpenLogfileProcessor;
import com.legyver.logmire.ui.ApplicationUIModel;
import javafx.application.Application;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.legyver.logmire.config.BindingFactory.LOG_TABS;

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
			uiModel = (ApplicationUIModel) ApplicationContext.getUiModel();
			logmireVersionInfo = new LogmireVersionInfo();
			taskFactory = new TaskFactory();

			SceneFactory sceneFactory = new SceneFactory(primaryStage, 1100, 750, MainApplication.class.getClassLoader().getResource("css/application.css"));
			OpenLogfileProcessor importProcessor = new OpenLogfileProcessor(taskFactory, bindingFactory);

			BorderPaneInitializationOptions options = new BorderPaneInitializationOptions.Builder()
					.center(new RegionInitializationOptions.Builder()
							//popup will display over this. See the centerContentReference Supplier above
							.factory(new StackPaneRegionFactory(true, new JFXTabPaneFactory(LOG_TABS)))
					)
					.top(new RegionInitializationOptions.Builder()
							.displayContentByDefault()
							.factory(new MenuRegionFactory(this, new OnFileOpen(uiModel, importProcessor)))
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
