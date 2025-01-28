package gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import client.ClientUI;
import common.Subscriber;
import common.User.UserType;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * Controller class for managing the Subscriber Manager UI.
 * Handles subscriber-related actions such as searching, registering, viewing details, and managing borrowed books.
 */
public class SubscriberManagerFrameController implements IController {

    /**
     * Button to open the selected subscriber's detailed information.
     */
    @FXML
    private Button btnOpen = null;

    /**
     * Label to display informational text.
     */
    @FXML
    private Label lblText = null;

    /**
     * Button to perform subscriber search.
     */
    @FXML
    private Button btnSearch = null;

    /**
     * Button to register a new subscriber.
     */
    @FXML
    private Button btnRegister = null;

    /**
     * Button to view the lent books of the selected subscriber.
     */
    @FXML
    private Button btnLentBooks = null;

    /**
     * ComboBox for selecting search options.
     */
    @FXML
    private ComboBox<String> searchOptions;

    /**
     * TableView for displaying the list of subscribers.
     */
    @FXML
    private TableView<Subscriber> tblSubscribers = null;

    /**
     * TableColumn for displaying subscriber IDs.
     */
    @FXML
    private TableColumn<Subscriber, Integer> tblColumnId = null;

    /**
     * TableColumn for displaying subscriber names.
     */
    @FXML
    private TableColumn<Subscriber, String> tblColumnName = null;

    /**
     * TableColumn for displaying subscriber statuses (Active/Frozen).
     */
    @FXML
    private TableColumn<Subscriber, String> tblColumnStatus = null;

    /**
     * TextField for entering search text.
     */
    @FXML
    private TextField searchField = null;

    /**
     * Reference to the main menu controller.
     */
    private MenuUIController mainController;

    /**
     * ObservableList to manage subscriber data for the TableView.
     */
    private final ObservableList<Subscriber> observableSubscribers = FXCollections.observableArrayList();

    /**
     * List of subscribers fetched from the server.
     */
    private List<Subscriber> subscriberList;

    /**
     * The currently selected subscriber.
     */
    private Subscriber selectedSubscriber = null;

    /**
     * Flag to handle thread termination for info listeners.
     */
    private static boolean flagKillInfoListenThread = false;

    /**
     * Initializes the TableView with subscriber data.
     */
    @FXML
    private void initializeTable() {
        if (subscriberList != null) {
            observableSubscribers.addAll(subscriberList);
        }

        tblColumnId.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getSubscriberId()));
        tblColumnName.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getSubscriberName()));
        tblColumnStatus.setCellValueFactory(cellData -> {
            String bool = (cellData.getValue().isFrozen()) ? "Frozen" : "Active";
            return new SimpleObjectProperty<>(bool);
        });
        tblSubscribers.setItems(observableSubscribers);
    }

    /**
     * Starts the Subscriber Manager UI.
     * 
     * @param primaryStage The primary stage for the application.
     * @throws IOException If there is an error loading the UI.
     */
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getResource("/gui/SubscriberManagerFrame.fxml").openStream());
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/style.css").toExternalForm());
        primaryStage.setResizable(false);
        primaryStage.setTitle("Subscriber Management");
        primaryStage.setScene(scene);
        primaryStage.show();
        SubscriberManagerFrameController controller = loader.getController();
        controller.initializeTable();
    }

    /**
     * Handles the action of registering a new subscriber.
     * 
     * @param event The ActionEvent triggered by the Register button.
     */
    @FXML
    private void handleRegisterBtn(ActionEvent event) {
        try {
            IController genericController = mainController.loadFXMLIntoPane("/gui/RegisterSubscriberFrame.fxml");
            genericController.setMainController(mainController);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the action of searching for subscribers.
     * 
     * @param event The ActionEvent triggered by the Search button.
     */
    @FXML
    private void handleSearchAction(ActionEvent event) {
        String selectedOption = searchOptions.getValue();
        String searchText = searchField.getText().trim();

        if (searchText.isEmpty()) {
            searchText = "";
        }

        switch (selectedOption) {
            case "Search Id":
                Subscriber subscriber = null;
                subscriberList = new ArrayList<>();
                if (!(searchText.contentEquals("") || searchText.isEmpty())) {
                    subscriber = ClientUI.chat.requestServerForSubscriber(searchText);
                    subscriberList.add(subscriber);
                }
                break;
            case "Search by Name":
                subscriberList = ClientUI.chat.requestServerForSubscriberList("subscriber_name", searchText);
                break;
            case "Search by Frozen Status":
                if (searchText.contentEquals("") || searchText.isEmpty())
                    break;
                if ("frozen".contains(searchText.toLowerCase())) {
                    subscriberList = ClientUI.chat.requestServerForSubscriberList("subscriber_frozen", "1");
                } else if ("active".contains(searchText.toLowerCase())) {
                    subscriberList = ClientUI.chat.requestServerForSubscriberList("subscriber_frozen", "0");
                } else {
                    subscriberList = new ArrayList<>();
                }
                break;
            default:
                break;
        }
        if (subscriberList == null) {
            subscriberList = new ArrayList<>();
        }

        observableSubscribers.clear();
        observableSubscribers.addAll(subscriberList);
    }

    /**
     * Displays an alert with the given title and message.
     * 
     * @param type The type of alert to display.
     * @param title The title of the alert.
     * @param message The message to display in the alert.
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Handles the action of viewing lent books of the selected subscriber.
     * 
     * @param event The ActionEvent triggered by the Lent Books button.
     * @throws Exception If there is an error loading the UI.
     */
    @FXML
    private void handleLentBooksBtn(ActionEvent event) throws Exception {
        try {
            IController genericController = mainController.loadFXMLIntoPane("/gui/ShowBorrowedBooksFrame.fxml");
            if (genericController instanceof ShowBorrowedBooksController) {
                ShowBorrowedBooksController borrowedController = (ShowBorrowedBooksController) genericController;
                genericController.setObject(selectedSubscriber);
                borrowedController.initializeBorrowedBooks();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the detailed information view of the selected subscriber.
     * 
     * @param event The ActionEvent triggered by the Open button.
     * @throws Exception If there is an error loading the UI.
     */
    @FXML
    private void Open(ActionEvent event) throws Exception {
        try {
            IController genericController = mainController.loadFXMLIntoPane("/gui/SubscriberInfoFrame.fxml");
            if (genericController instanceof SubscriberInfoFrameController) {
                SubscriberInfoFrameController infoController = (SubscriberInfoFrameController) genericController;
                infoController.setObject(selectedSubscriber);
                infoController.setPermission(ClientUI.chat.getClientUser().getType());
                infoController.initializeSubscriberInfo();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the main menu controller.
     * 
     * @param controller The main menu controller to set.
     */
    @Override
    public void setMainController(MenuUIController controller) {
        this.mainController = controller;
    }

    /**
     * Handles the row selection in the TableView.
     * Enables or disables buttons based on the selection.
     * 
     * @param event The MouseEvent triggered by selecting a row.
     * @throws Exception If an error occurs during selection.
     */
    @FXML
    private void SelectRow(MouseEvent event) throws Exception {
        selectedSubscriber = tblSubscribers.getSelectionModel().getSelectedItem();
        if (selectedSubscriber != null) {
            btnOpen.setDisable(false);
            btnLentBooks.setDisable(false);
        } else {
            btnOpen.setDisable(true);
            btnLentBooks.setDisable(true);
        }
    }

    /**
     * Initializes the frame with default settings and populates search options.
     */
    @Override
    public void initializeFrame() {
        mainController.setPaneTitle("Subscriber Manager");
        searchOptions.getItems().addAll("Search Id", "Search by Name", "Search by Frozen Status");
        searchOptions.setValue("Search Id");
        initializeTable();
    }

    /**
     * Sets the permission for the current user.
     * 
     * @param type The user type defining permissions.
     */
    @Override
    public void setPermission(UserType type) {
        // Not implemented
    }

    /**
     * Sets the object for the controller.
     * 
     * @param object The object to set.
     */
    @Override
    public void setObject(Object object) {
        // Not implemented
    }

    /**
     * Initializes the frame with a given object.
     * 
     * @param object The object used for initialization.
     */
    @Override
    public void initializeFrame(Object object) {
        // Not implemented
    }
}
