package com.legyver.logmire.ui.search;

import com.legyver.fenxlib.api.context.ApplicationContext;
import com.legyver.fenxlib.extensions.tuktukfx.task.adapter.JavaFxAdapter;
import com.legyver.fenxlib.extensions.tuktukfx.task.exec.TaskExecutor;
import com.legyver.logmire.event.ResetType;
import com.legyver.logmire.task.search.SearchTask;
import com.legyver.logmire.task.search.SearchTaskContext;
import com.legyver.logmire.ui.ApplicationUIModel;
import com.legyver.logmire.ui.bean.DataSourceUI;
import com.legyver.logmire.ui.bean.LogLineUI;
import com.legyver.logmire.ui.filter.PackageFilterData;
import com.legyver.logmire.ui.filter.SeverityFilterData;
import com.legyver.logmire.ui.tabs.LogLineView;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

public class FilterableLogContext {
	private static final Logger logger = LogManager.getLogger();
	private static final int MINIMUM_SEARCH_LENGTH_DEFAULT = 3;

	private final DataSourceUI dataSourceUI;
	private final int minimumSearchLength;
	private final SeverityFilterData severityFilterData = new SeverityFilterData();
	private final PackageFilterData packageFilterData = new PackageFilterData();
	private final InMemoryIndex inMemoryIndex;

	private final ObservableMap<String, BooleanProperty> packageFilters;
	private final ObservableList<LogLineView> allItems = FXCollections.observableArrayList();
	private final ObservableList<LogLineView> showItems = FXCollections.observableArrayList();
	private final List<LogLineUI> currentSearchResult = new ArrayList<>();
	private String currentSearch = "";
	private int lastSearchHash;
	private Integer lastStateHash;//using an Object here so we know if this is initial load
	private WeakReference<JavaFxAdapter> currentSearchTask;


	private Semaphore mutex = new Semaphore(1);

	//TODO:  This doesn't belong here
	private final ObjectProperty<LogLineUI> focusLogLine = new SimpleObjectProperty<>();

	public FilterableLogContext(DataSourceUI dataSourceUI, int minimumSearchLength) {
		this.dataSourceUI = dataSourceUI;
		this.minimumSearchLength = minimumSearchLength;
		inMemoryIndex = new InMemoryIndex();

		initLines(dataSourceUI);

		ApplicationUIModel applicationUIModel = (ApplicationUIModel) ApplicationContext.getUiModel();
		packageFilters = applicationUIModel.getPackageFilters();
	}

	public FilterableLogContext(DataSourceUI dataSourceUI) {
		this(dataSourceUI, MINIMUM_SEARCH_LENGTH_DEFAULT);
	}

	private void initLines(DataSourceUI dataSourceUI) {
		dataSourceUI.acquireLock();
		dataSourceUI.getLines().addListener((ListChangeListener<LogLineUI>) c -> {
			onAddedLogLine(c);
		});
		addLine(dataSourceUI.getLines());
		dataSourceUI.releaseLock();
	}

	private synchronized void onAddedLogLine(ListChangeListener.Change<? extends LogLineUI> c) {
		if (c.next()) {
			if (c.wasAdded()) {
				logger.trace("waiting to add lines");
				synchronized (this) {
					logger.debug("adding lines");
					addLine(c.getAddedSubList());
				}
				logger.trace("done adding lines");
			}
		}
	}

	private void addLine(List<? extends LogLineUI> lines) {
		for (LogLineUI logLineUI : lines) {
			LogLineView logLineView = logLineUI.getLogLineView();
			if (logLineView == null) {
				logLineView = new LogLineView(logLineUI);
				logLineView.setOnMouseClicked(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						focusLogLine.setValue(logLineUI);
					}
				});
			}
			allItems.add(logLineView);
		}
	}

	private int calculateHash() {
		return new HashCodeBuilder()
				.append(currentSearch)
				.append(severityFilterData)
				.append(packageFilterData).toHashCode();
	}

	public void searchFilterAdded(String newValue) {
		currentSearch = newValue;
		reset(ResetType.SEARCH_ADDED);
	}

	public void searchFilterRemoved(String newValue) {
		currentSearch = newValue;
		reset(ResetType.SEARCH_CLEARED);
	}

	public ObservableList<LogLineView> getAllItems() {
		return allItems;
	}

	public ReadOnlyListProperty<LogLineView> showItems() {
		return new SimpleListProperty<>(showItems);
	}

	public InMemoryIndex getInMemoryIndex() {
		return inMemoryIndex;
	}

	public synchronized void reset(ResetType resetType) {
		switch (resetType) {
			case SEARCH_ADDED:
				logger.debug("search added");
				refresh();
			case SEARCH_CLEARED:
				currentSearch = "";
				logger.debug("search cleared");
				//redo without search filter
				refresh();
				break;
			case LOGFILE_ROLLOVER:
				logger.info("logfile rollover");
				allItems.clear();
				inMemoryIndex.reset();
				currentSearchResult.clear();//leave the current search though
				refresh();
				break;
			case SEVERITY_TOGGLE:
				logger.debug("Severity toggle");
				refresh();
				break;
			case REPORTER_TOGGLE:
				logger.debug("Reporter toggle");
				refresh();
				break;
			case INITIAL_LOAD:
				logger.info("initial load");
				refresh();
				break;
			default:
				logger.warn("Unknown reset type: " + resetType);
				//do nothing
		}
	}

	private boolean filter(LogLineView logLineView) {
		boolean matchesCondition;
		if (severityShown(logLineView.getSeverity())
				&& !hideReporterMessage(logLineView.getReporter())) {
			if (logger.isDebugEnabled()) {
				logger.debug("Adding message: " + logLineView.getTruncated());
			}
			matchesCondition = true;
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Skipping message: " + logLineView.getTruncated());
			}
			matchesCondition = false;
		}
		return matchesCondition;
	}

	private boolean hideReporterMessage(String reporter) {
		boolean doNotShow;
		if (packageFilterData.isFilterEnabled()) {
			Optional<String> filterPackage = packageFilters.keySet().stream()
					.filter(s -> reporter != null && reporter.startsWith(s))
					.findFirst();
			if (filterPackage.isPresent()) {
				doNotShow =  packageFilters.get(filterPackage.get()).get();
				if (logger.isDebugEnabled()) {
					logger.debug("Filtering on reporter " + reporter + " : " + (doNotShow ? "enabled" : "disabled"));
				}
			} else {
				doNotShow = false;
				if (logger.isDebugEnabled()) {
					logger.debug("No filter for reporter: " + reporter);
				}
			}
		} else {
			doNotShow = false;
		}

		return doNotShow;
	}

	private boolean severityShown(String severity) {
		if (severity == null) {
			return true;
		} else {
			switch (severity) {
				case "TRACE":
					return severityFilterData.isShowTrace();
				case "DEBUG":
					return severityFilterData.isShowDebug();
				case "INFO":
					return severityFilterData.isShowInfo();
				case "WARN":
					return severityFilterData.isShowWarn();
				case "ERROR":
					return severityFilterData.isShowError();
				case "FATAL":
					return severityFilterData.isShowFatal();
				default:
					return true;
			}
		}
	}

	private synchronized void refresh() {
		int hashCheck = this.calculateHash();
		int searchHashCheck = currentSearch.hashCode();
		if (currentSearch.length() > minimumSearchLength && searchHashCheck != lastSearchHash) {
			//search has changed, re-run
			SearchTaskContext searchTaskContext = inMemoryIndex.searchTaskContext(currentSearch);
			searchTaskContext.setLock(mutex);
			SearchTask searchTask = new SearchTask(searchTaskContext);

			JavaFxAdapter adapter = new JavaFxAdapter(searchTask);
			adapter.setOnSucceeded(e-> {
				lastSearchHash = searchHashCheck;
				currentSearchResult.clear();
				currentSearchResult.addAll(searchTaskContext.getSearchResult());
				refreshShowItems();
			});

			if (currentSearchTask != null) {
				//abort old one
				currentSearchTask.get().abort();
			}
			currentSearchTask = new WeakReference<>(adapter);

			TaskExecutor.getInstance().submitTask(adapter);
		} else if (lastStateHash == null || hashCheck != lastStateHash.intValue()) {
			//if initial load, or filter changed, re-run all items through filters
			Platform.runLater(() -> {
				currentSearchResult.clear();
				currentSearchResult.addAll(allItems.stream()
						.map(logLineView -> logLineView.getValue())
						.collect(Collectors.toList()));
				lastStateHash = hashCheck;
				refreshShowItems();
			});
		}

	}

	private synchronized void refreshShowItems() {
		showItems.clear();
		showItems.addAll(currentSearchResult.stream()
				.map(logLineUI -> logLineUI.getLogLineView())
				.filter(this::filter)
				.collect(Collectors.toList()));
	}

	private void acquireLock() {
		try {
			mutex.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void releaseLock() {
		mutex.release();
	}

	public SeverityFilterData getSeverityFilterData() {
		return severityFilterData;
	}

	public PackageFilterData getPackageFilterData() {
		return packageFilterData;
	}

	public LogLineUI getFocusLogLine() {
		return focusLogLine.get();
	}

	public ObjectProperty<LogLineUI> focusLogLineProperty() {
		return focusLogLine;
	}
}
