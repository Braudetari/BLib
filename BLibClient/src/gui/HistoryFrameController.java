package gui;

import java.util.ArrayList;
import java.util.List;

import client.ClientUI;
import common.DetailedHistory;
import common.Subscriber;
import common.User;
import common.User.UserType;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Controller class for the History Frame in the GUI.
 * This frame displays detailed history of actions for a specific subscriber or user.
 */
public class HistoryFrameController implements IController {

    /** Parent node of the frame layout. */
    @FXML
    private AnchorPane parentNode = null;

    /** ComboBox for selecting search options (Action, Description, Note). */
    @FXML
    private ComboBox<String> searchOptions;

    /** TextField for entering search text. */
    @FXML
    private TextField searchField;

    /** Button for executing the search. */
    @FXML
    private Button btnSearch;

    /** Button for navigating back to the previous screen. */
    @FXML
    private Button btnBack;

    /** Button for adding a note to a selected history entry. */
    @FXML
    private Button btnAddNote;

    /** TableView for displaying the detailed history. */
    @FXML
    private TableView<DetailedHistory> historyTable;

    /** TableColumn for displaying the action in the history. */
    @FXML
    private TableColumn<DetailedHistory, String> colAction;

    /** TableColumn for displaying the date in the history. */
    @FXML
    private TableColumn<DetailedHistory, String> colDate;

    /** TableColumn for displaying the description in the history. */
    @FXML
    private TableColumn<DetailedHistory, String> colDescription;

    /** Reference to the main controller managing the current view. */
    private MenuUIController mainController;

    /** Currently selected line in the history table. */
    private DetailedHistory selectedLine = null;

    /** User permission level. */
    private User.UserType permission;

    /** Imported subscriber whose history is being displayed. */
    private Subscriber importedSubscriber;

    /** List of all detailed history entries. */
    private List<DetailedHistory> historyList = null;

    /** Observable list for binding history data to the table view. */
    private ObservableList<DetailedHistory> historyData;

    /** ID representing the user's history. */
    private Integer userHistoryId = 0;

    /**
     * Sets the object for this controller, specifying the user's history ID.
     * 
     * @param object the object containing the user's history ID.
     */
    public void setOjbect(Object object) {
        if (object instanceof Integer) {
            this.userHistoryId = (Integer) object;
        }
    }

    /**
     * Initializes the history frame. Configures table columns, populates search options,
     * and sets visibility for the Add Note button based on user type.
     */
    public void initializeHistory() {
        if (ClientUI.chat.getClientUser().getType().equals(User.UserType.LIBRARIAN)) {
            btnAddNote.setVisible(true);
        } else {
            btnAddNote.setVisible(false);
        }

        // Add search options
        searchOptions.getItems().addAll("Action", "Description", "Note");
        searchOptions.setValue("Action");

        historyData = FXCollections.observableArrayList();
        colAction.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getAction().toString()));
        colDescription.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDescription()));
        colDate.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDate().toString()));
        historyTable.setItems(historyData);
    }

    /**
     * Refreshes the history data. To be implemented.
     */
    public void refreshHistory() {
    }

    /**
     * Handles the search action when the search button is clicked.
     * Filters the history data based on the selected search option and input text.
     * 
     * @param event the action event triggered by the search button.
     */
    @FXML
    private void handleSearchAction(ActionEvent event) {
        String selectedOption = searchOptions.getValue();
        String searchText = searchField.getText().trim();
        if (importedSubscriber == null && ClientUI.chat.getClientUser().equals(User.UserType.LIBRARIAN)) {
            // Implement Librarian Request History List
            historyList = null;
        } else {
            historyList = ClientUI.chat.requestServerForHistoryList(importedSubscriber.getDetailedSubscriptionHistory());
        }
        if (historyList == null) {
            historyList = new ArrayList<>();
        }

        if (searchText.isEmpty()) {
            searchText = "";
        }
        List<DetailedHistory> filteredHistoryList = new ArrayList<>();
        for (int i = historyList.size() - 1; i >= 0; i--) {
            boolean addEntry = false;
            switch (selectedOption) {
                case "Action":
                    if (historyList.get(i).getAction().toString().toLowerCase().contains(searchText.toLowerCase())) {
                        addEntry = true;
                    }
                    break;
                case "Description":
                    if (historyList.get(i).getDescription().toString().toLowerCase().contains(searchText.toLowerCase())) {
                        addEntry = true;
                    }
                    break;
                case "Note":
                    if (historyList.get(i).getNote().toString().toLowerCase().contains(searchText.toLowerCase())) {
                        addEntry = true;
                    }
                    break;
                default:
                    break;
            }

            if (addEntry) {
                filteredHistoryList.add(historyList.get(i));
            }
        }

        historyData.clear();
        historyData.addAll(filteredHistoryList);
    }

    /**
     * Handles the row selection in the history table. Enables or disables the Add Note button
     * based on whether a row is selected.
     * 
     * @param event the mouse event triggered by selecting a row.
     * @throws Exception if an error occurs during row selection.
     */
    @FXML
    private void SelectRow(MouseEvent event) throws Exception {
        selectedLine = historyTable.getSelectionModel().getSelectedItem();
        if (selectedLine != null) {
            btnAddNote.setDisable(false);
        } else {
            btnAddNote.setDisable(true);
        }
    }

    /**
     * Opens the Add Note frame for adding a note to the selected history entry.
     * 
     * @param event the action event triggered by clicking the Add Note button.
     */
    @FXML
    private void OpenAddNote(ActionEvent event) {
        IController genericController = mainController.loadFXMLIntoPane("/gui/AddNoteFrame.fxml");
        if (genericController instanceof AddNoteFrameController) {
            AddNoteFrameController addNote = (AddNoteFrameController) genericController;
            addNote.setObject(new Object[]{selectedLine, importedSubscriber, userHistoryId});
        }
    }

    /**
     * Handles the back button action, navigating back to the Subscriber Info frame.
     * 
     * @param event the action event triggered by clicking the back button.
     * @throws Exception if an error occurs during navigation.
     */
    @FXML
    private void handleBackBtn(ActionEvent event) throws Exception {
        try {
            IController genericController = mainController.loadFXMLIntoPane("/gui/SubscriberInfoFrame.fxml");
            if (genericController instanceof SubscriberInfoFrameController) {
                SubscriberInfoFrameController infoController = (SubscriberInfoFrameController) genericController;
                infoController.setObject(importedSubscriber);
                infoController.initializeSubscriberInfo();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setMainController(MenuUIController controller) {
        this.mainController = controller;
    }

    /**
     * Starts the History Frame, initializing it with the given permission level.
     * 
     * @param primaryStage the stage to display the frame.
     * @param permission the user type permission level.
     * @throws Exception if an error occurs during initialization.
     */
    public void start(Stage primaryStage, User.UserType permission) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getResource("/gui/HistoryFrame.fxml").openStream());
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/style.css").toExternalForm());
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void setPermission(UserType type) {
        permission = type;
    }

    @Override
    public void setObject(Object object) {
        userHistoryId = (Integer) ((Object[]) object)[1];
        importedSubscriber = (Subscriber) ((Object[]) object)[0];
    }

    @Override
    public void initializeFrame(Object object) {
        // To be implemented if needed
    }

    @Override
    public void initializeFrame() {
        mainController.setPaneTitle("Detailed History");
    }
}
