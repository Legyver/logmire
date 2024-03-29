package com.legyver.logmire.ui.tabs;

import com.legyver.fenxlib.api.context.ApplicationContext;
import com.legyver.fenxlib.api.icons.options.IconOptions;
import com.legyver.fenxlib.controls.icon.IconControl;
import com.legyver.fenxlib.icons.standard.IcoMoonFontEnum;
import com.legyver.fenxlib.icons.standard.IcoMoonIconOptions;
import com.legyver.logmire.ui.ApplicationUIModel;
import com.legyver.logmire.ui.bean.CausalSectionUI;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.legyver.logmire.config.IconConstants.FONTAWESOME_ICON_COPY;
import static com.legyver.logmire.config.IconConstants.FONTAWESOME_ICON_EYESLASH;

public class LogLineDetailSkin extends SkinBase<LogLineDetail> {
	private static final Logger logger = LogManager.getLogger(LogLineDetailSkin.class);

	private final GridPane gridPane;
	private final ListView stacktrace;
	private StringProperty copyableClassRef = new SimpleStringProperty();

	public LogLineDetailSkin(LogLineDetail logLineDetail) {
		super(logLineDetail);
		gridPane = new GridPane();
		gridPane.setVgap(10);
		gridPane.setHgap(10);
		gridPane.setPadding(new Insets(10));

		stacktrace = new ListView();

		try {
			Label messageLabel = new Label("Message");
			Node message = make(new TextArea(), logLineDetail.fullMessageProperty());

			Label reporterLabel = new Label("Reporter");
			Node reporter = make(new TextField(), logLineDetail.reporterProperty());

			Label dateLabel = new Label("Date");
			Node date = make(new TextField(), logLineDetail.dateProperty());

			Label timestampLabel = new Label("Timestamp");
			Node timestamp = make(new TextField(), logLineDetail.timestampProperty());

			Label executorLabel = new Label("Executor");
			Node executor = make(new TextField(), logLineDetail.executorProperty());

			Label severityLabel = new Label("Severity");
			Node severity = make(new TextField(), logLineDetail.severityProperty());

			Label rootErrorLabel = new Label("Root error");
			Node rootError = make(new TextField(), logLineDetail.rootErrorProperty());

			Label rootLocationLabel = new Label("Root location");
			Node rootLocation = make(new TextField(), logLineDetail.rootLocationProperty());

			Label causesLabel = new Label("Errors");
			Label label = new Label();
			label.textProperty().bind(logLineDetail.copyableMessageProperty());
			TreeItem causesRoot = new TreeItem<>(label);
			TreeView causesTree = new TreeView<>(causesRoot);
			causesTree.setShowRoot(false);

			logLineDetail.getCausalSections().addListener((ListChangeListener<CausalSectionUI>) change -> {
				if (change.next()) {
					if (change.wasAdded()) {
						List<? extends CausalSectionUI> addedList = change.getAddedSubList();
						addedList.stream().forEach(causalSectionUI -> {
							TreeItem currentLast = findLast(causesRoot);
							addToTree(currentLast, addedList, 0);
						});
					} else if (change.wasRemoved()) {
						causesRoot.getChildren().clear();
					}
				}
			});
			addToTree(causesRoot, logLineDetail.getCausalSections(), 0);

			Label stackLabel = new Label("Stack trace");


			int row = 0;
			gridPane.addRow(row, timestampLabel, timestamp, dateLabel, date, executorLabel, executor);
			GridPane.setHgrow(executor, Priority.ALWAYS);

			row++;
			gridPane.add(severityLabel, 0, row);
			gridPane.add(severity, 1, row);
			gridPane.add(reporterLabel, 2, row);
			gridPane.add(reporter, 3, row, 3, 1);
			GridPane.setHgrow(reporter, Priority.ALWAYS);

			row++;
			addCopyRow(row, messageLabel, message, logLineDetail.copyableMessageProperty());

			row++;
			addCopyRow(row, rootErrorLabel, rootError, logLineDetail.rootErrorProperty());

			row++;
			addCopyRow(row, rootLocationLabel, rootLocation, logLineDetail.rootLocationProperty());

			row++;
			gridPane.add(causesLabel, 0, row);
			gridPane.add(causesTree, 1, row, 5, 3);

			row+=3;
			gridPane.add(stackLabel, 0, row);
			gridPane.add(stacktrace, 1, row, 5, 4);
			VBox vBox = iconVBox(onClickCopy(copyableClassRef), IcoMoonFontEnum.ICON_COPY);
			gridPane.add(vBox, 6, row);
			GridPane.setValignment(vBox, VPos.TOP);
			GridPane.setHgrow(stacktrace, Priority.ALWAYS);
		} finally {
			getChildren().add(gridPane);
		}
	}

	private TreeItem findLast(TreeItem<String> item) {
		if (item.getChildren().isEmpty()) {
			return item;
		}
		return findLast(item.getChildren().get(0));//assumes only one child
	}

	private void addToTree(TreeItem parent, List<? extends CausalSectionUI> causes, int cursor) {
		if (cursor == causes.size()) {
			return;
		}
		CausalSectionUI causalSectionUI = causes.get(cursor);
		causalSectionUI.acquireLock();
		Label label = new Label(deriveShort(causalSectionUI.getShortMessage()));

		label.setOnMouseClicked(new StackTraceContext(causalSectionUI));
		TreeItem treeNode = new TreeItem(label);
		parent.getChildren().add(treeNode);
		causalSectionUI.releaseLock();
		addToTree(treeNode, causes, ++cursor);
	}

	private static String deriveShort(String shortMessage) {
		if (shortMessage.contains(": ")) {
			String[] parts = shortMessage.split(": ");
			for (int i = parts.length - 1; i > -1; i--) {
				String part = parts[i];
				if (!StringUtils.isAllBlank(part)) {
					return part.trim();
				}
			}
		}
		return shortMessage;
	}

	private void addCopyRow(int row, Label label, Node node, StringProperty copyableProperty) {
		addIconRow(row, label, node, onClickCopy(copyableProperty), IcoMoonFontEnum.ICON_COPY);
	}

	private void addSettingsRow(int row, Label label, Node node, StringProperty hideableProperty) {
		addIconRow(row, label, node, onClickHide(hideableProperty), IcoMoonFontEnum.ICON_EYE);
	}

	private void addIconRow(int row, Label label, Node node, EventHandler<MouseEvent> onClick, IcoMoonFontEnum icon) {
		gridPane.add(label, 0, row);
		gridPane.add(node, 1, row, 5, 1);
		VBox vBox = iconVBox(onClick, icon);
		gridPane.add(vBox, 6, row, 1, 1);//copy icon to right of message
		GridPane.setValignment(vBox, VPos.TOP);
		GridPane.setHgrow(node, Priority.ALWAYS);
	}

	private VBox iconVBox(EventHandler<MouseEvent> onClick, IcoMoonFontEnum icon) {
		IconControl iconControl = new IconControl();
		IconOptions iconOptions = new IcoMoonIconOptions.Builder()
				.icoMoonIcon(icon)
				.iconColorString("#68b1e3")
				.iconSize(18)
				.build();

		iconControl.setIconOptions(iconOptions);
		iconControl.setOnMouseClicked(onClick);

		Region spacer = new Region();
		VBox vBox = new VBox(iconControl, spacer);
		VBox.setVgrow(spacer, Priority.ALWAYS);
		return vBox;
	}

	private EventHandler<MouseEvent> onClickCopy(StringProperty property) {
		Clipboard clipboard = Clipboard.getSystemClipboard();
		return event -> {
			final ClipboardContent content = new ClipboardContent();
			content.putString(property.get());
			clipboard.setContent(content);
		};
	}

	private EventHandler<MouseEvent> onClickHide(StringProperty property) {
		ApplicationUIModel applicationUIModel = (ApplicationUIModel) ApplicationContext.getUiModel();
		Map<String, BooleanProperty> packageFilters = applicationUIModel.getPackageFilters();
		return event -> {
			BooleanProperty booleanProperty = packageFilters.get(property.get());
			if (booleanProperty == null) {
				booleanProperty = new SimpleBooleanProperty(false);
				packageFilters.put(property.get(), booleanProperty);
			}
			booleanProperty.set(!booleanProperty.get());
		};
	}

	private Node make(TextInputControl textInputControl, StringProperty textProperty) {
		textInputControl.setEditable(false);
		textInputControl.textProperty().bind(textProperty);
		return textInputControl;
	}

	private class StackTraceContext implements EventHandler<MouseEvent> {
		private final CausalSectionUI causalSectionUI;

		private StackTraceContext(CausalSectionUI causalSectionUI) {
			this.causalSectionUI = causalSectionUI;
		}

		@Override
		public void handle(MouseEvent mouseEvent) {
			stacktrace.getItems().clear();
			List<Label> labels = causalSectionUI.getStackTraceElements().stream()
					.map(stackTraceElementUI -> {
						Label label = new Label(stackTraceElementUI.getStackTraceElementLine());
						label.setOnMouseClicked(e-> copyableClassRef.set(stackTraceElementUI.getCopyableClassRef()));
						return label;
					}).collect(Collectors.toList());
			stacktrace.setItems(FXCollections.observableArrayList(labels));
		}
	}
}
