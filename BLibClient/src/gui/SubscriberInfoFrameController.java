package gui;

import java.io.IOException;

import client.ClientUI;
import common.Subscriber;
import common.User;
import common.User.UserType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Controller class for managing subscriber information in the UI.
 * Provides functionality to view, edit, and update subscriber details.
 */
public class SubscriberInfoFrameController implements IController {

    /**
     * Toggle button to display and change the subscriber's status (Active/Frozen).
     */
    @FXML
    private ToggleButton toggleStatus = null;

    /**
     * Button to close the subscriber information frame.
     */
    @FXML
    private Button btnClose = null;

    /**
     * Button to update the subscriber information.
     */
    @FXML
    private Button btnUpdate = null;

    /**
     * Button to go back to the previous screen.
     */
    @FXML
    private Button btnBack = null;

    /**
     * Button to view the subscription history of the subscriber.
     */
    @FXML
    private Button btnHistory = null;

    /**
     * Label to display messages related to the subscriber information.
     */
    @FXML
    private Label labelmsg = null;

    /**
     * TextField to display the subscriber's ID.
     */
    @FXML
    private TextField txtId = null;

    /**
     * TextField to display and edit the subscriber's name.
     */
    @FXML
    private TextField txtName = null;

    /**
     * TextField to display and edit the subscriber's phone number.
     */
    @FXML
    private TextField txtPhone = null;

    /**
     * TextField to display and edit the subscriber's email.
     */
    @FXML
    private TextField txtEmail = null;

    /**
     * The subscriber object being managed.
     */
    private Subscriber importedSubscriber;

    /**
     * The main menu controller.
     */
    private MenuUIController mainController = null;

    /**
     * The user type to define permissions.
     */
    private User.UserType permission = null;

    /**
     * Initializes the subscriber information fields.
     */
    public void initializeSubscriberInfo() {
        boolean isLibrarian = checkUserType();
        btnBack.setVisible(isLibrarian);
        btnBack.setDisable(!isLibrarian);
        toggleStatus.setSelected(importedSubscriber.isFrozen());
        toggleStatus.setText(importedSubscriber.isFrozen() ? "FROZEN" : "ACTIVE");
        toggleStatus.setDisable(!isLibrarian);
        txtId.setDisable(true);
        txtName.setDisable(!isLibrarian);
        loadText(importedSubscriber);
    }

    /**
     * Checks if the current user is a librarian.
     * 
     * @return true if the user is a librarian, false otherwise.
     */
    private boolean checkUserType() {
        return ClientUI.chat.getClientUser().getType().equals(User.UserType.LIBRARIAN);
    }

    /**
     * Loads the subscriber's information into the UI fields.
     * 
     * @param subscriber The subscriber whose information is to be loaded.
     */
    private void loadText(Subscriber subscriber) {
        txtId.setText(String.valueOf(subscriber.getSubscriberId()));
        txtName.setText(subscriber.getSubscriberName());
        txtPhone.setText(subscriber.getSubscriberPhoneNumber());
        txtEmail.setText(subscriber.getSubscriberEmail());
    }

    /**
     * Starts the subscriber information UI.
     * 
     * @param primaryStage The primary stage for the application.
     * @param subscriber The subscriber whose information is being displayed.
     * @throws IOException If there is an error loading the UI.
     */
    public void start(Stage primaryStage, Subscriber subscriber) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        Pane root = loader.load(getClass().getResource("/gui/SubscriberInfoFrame.fxml").openStream());
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/SubscriberInfoFrame.css").toExternalForm());
        primaryStage.setTitle("Subscriber Information");
        primaryStage.setScene(scene);
        primaryStage.show();

        SubscriberInfoFrameController infoFrame = loader.getController();
        importedSubscriber = new Subscriber(subscriber);
        infoFrame.loadText(subscriber);
    }

    /**
     * Handles the update button action to save changes to the subscriber's information.
     * 
     * @param event The action event triggered by the update button.
     * @throws Exception If there is an error during the update process.
     */
    @FXML
    private void getUpdateButton(ActionEvent event) throws Exception {
        Subscriber editedSubscriber = new Subscriber(importedSubscriber);
        if (checkUserType()) {
            editedSubscriber.setSubscriberName(txtName.getText());
            editedSubscriber.setFrozen(toggleStatus.isSelected());
        }
        editedSubscriber.setSubscriberEmail(txtEmail.getText());
        editedSubscriber.setSubscriberPhoneNumber(txtPhone.getText());
        ClientUI.chat.requestServerToUpdateSubscriber(editedSubscriber);
    }

    /**
     * Handles the toggle button action to change the subscriber's status (Active/Frozen).
     */
    @FXML
    private void handleToggleStatus() {
        toggleStatus.setText(toggleStatus.isSelected() ? "FROZEN" : "ACTIVE");
    }

    /**
     * Handles the history button action to display the subscriber's subscription history.
     * 
     * @param event The action event triggered by the history button.
     * @throws Exception If there is an error during the process.
     */
    @FXML
    private void handleHistoryBtn(ActionEvent event) throws Exception {
        try {
            IController genericController = mainController.loadFXMLIntoPane("/gui/HistoryFrame.fxml");
            if (genericController instanceof HistoryFrameController) {
                HistoryFrameController infoController = (HistoryFrameController) genericController;
                infoController.setObject(new Object[]{importedSubscriber, importedSubscriber.getDetailedSubscriptionHistory()});
                infoController.initializeHistory();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the back button action to return to the previous screen.
     * 
     * @param event The action event triggered by the back button.
     * @throws Exception If there is an error during the process.
     */
    @FXML
    private void handleBackBtn(ActionEvent event) throws Exception {
        try {
            IController genericController = mainController.loadFXMLIntoPane("/gui/SubscriberManagerFrame.fxml");
            if (genericController instanceof HistoryFrameController) {
                HistoryFrameController infoController = (HistoryFrameController) genericController;
                infoController.setObject(importedSubscriber);
                infoController.initializeHistory();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the frame with a given object.
     * 
     * @param object The object used for initialization.
     */
    @Override
    public void initializeFrame(Object object) {
    }

    /**
     * Sets the user type permissions for the controller.
     * 
     * @param type The user type to set.
     */
    @Override
    public void setPermission(UserType type) {
        this.permission = type;
    }

    /**
     * Sets the main menu controller.
     * 
     * @param controller The main menu controller.
     */
    @Override
    public void setMainController(MenuUIController controller) {
        this.mainController = controller;
    }

    /**
     * Sets the subscriber object to be managed.
     * 
     * @param object The subscriber object.
     */
    @Override
    public void setObject(Object object) {
        importedSubscriber = (Subscriber) object;
    }

    /**
     * Initializes the frame and sets the pane title.
     */
    @Override
    public void initializeFrame() {
        mainController.setPaneTitle("Personal Info");
    }
}
