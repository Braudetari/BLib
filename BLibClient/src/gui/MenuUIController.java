package gui;

import java.io.IOException;

import client.ClientUI;
import common.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controller class for the main menu UI. Provides functionality for navigating
 * various features of the application based on user permissions.
 */
public class MenuUIController {

    @FXML
    private Button btnSearch = null;
    @FXML
    private Button btnReport = null;
    @FXML
    private Button btnLogout = null;
    @FXML
    private Button btnManager = null;
    @FXML
    private Button btnBorrow = null;
    @FXML
    private Button btnReturnABook = null;
    @FXML
    private Button btnNotifications = null;
    @FXML
    private Button btnBorrowedBooks = null;
    @FXML
    private Button btnReservations = null;
    @FXML
    private Button btnPersonalInfo = null;
    @FXML
    private Label lblWelocome = null;
    @FXML
    private Label lblPane = null;
    @FXML
    private VBox pane = null;
    @FXML
    private ScrollPane scrollPane = null;
    @FXML
    private AnchorPane paneScreen = null;
    @FXML
    private AnchorPane paneButtons = null;

    private User.UserType permission;
    private String name;

    /**
     * Initializes button visibility and functionality based on user permissions.
     */
    private void initializeButtons() {
        Button[] listOfButtons = {btnSearch, btnNotifications, btnManager, btnBorrow, btnReturnABook, btnBorrowedBooks, btnReservations, btnPersonalInfo, btnReport};
        User.UserType[] buttonPermission = {User.UserType.GUEST, User.UserType.SUBSCRIBER, User.UserType.LIBRARIAN, User.UserType.LIBRARIAN, User.UserType.LIBRARIAN, User.UserType.SUBSCRIBER, User.UserType.SUBSCRIBER, User.UserType.SUBSCRIBER, User.UserType.LIBRARIAN};
        lblWelocome.setText("Welcome " + name);
        for (int i = 0; i < listOfButtons.length; i++) {
            boolean checkPermission = buttonPermission[i].equals(permission) || buttonPermission[i].equals(User.UserType.GUEST);
            listOfButtons[i].setVisible(checkPermission);
            listOfButtons[i].setDisable(!checkPermission);
        }
    }

    /**
     * Initializes the controller with the specified user type and name.
     *
     * @param permission the user type of the logged-in user.
     * @param name       the name of the logged-in user.
     */
    private void initialize(User.UserType permission, String name) {
        this.permission = permission;
        this.name = name;
    }

    /**
     * Starts the menu UI stage and initializes it with user details.
     *
     * @param primaryStage the primary stage for the menu UI.
     * @param permission   the user type of the logged-in user.
     * @param name         the name of the logged-in user.
     * @throws IOException if there is an error loading the FXML file.
     */
    public void start(Stage primaryStage, User.UserType permission, String name) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        Pane root = loader.load(getClass().getResource("/gui/MenuUI.fxml").openStream());
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/style.css").toExternalForm());
        primaryStage.setResizable(false);
        primaryStage.setTitle("MenuUI");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
        MenuUIController frame = loader.getController();
        frame.initialize(permission, name);
        frame.initializeButtons();
    }

    /**
     * Handles the "Report" button click event. Loads the report frame into the menu pane.
     *
     * @param event the action event triggered by the button click.
     */
    @FXML
    private void getReportBtn(ActionEvent event) {
        loadFXMLIntoPane("/gui/ReportFrame.fxml");
        lblPane.setText("Report");
    }

    /**
     * Handles the "Manager" button click event. Loads the subscriber manager frame.
     *
     * @param event the action event triggered by the button click.
     */
    @FXML
    private void getManagerBtn(ActionEvent event) {
        loadFXMLIntoPane("/gui/SubscriberManagerFrame.fxml");
        lblPane.setText("Subscriber Manager");
    }

    /**
     * Handles the "Borrow" button click event. Loads the borrow book frame.
     *
     * @param event the action event triggered by the button click.
     */
    @FXML
    private void getBorrowBtn(ActionEvent event) {
        loadFXMLIntoPane("/gui/BorrowBookFrame.fxml");
        lblPane.setText("Lend Book");
    }

    /**
     * Handles the "Return Book" button click event. Loads the return book frame.
     *
     * @param event the action event triggered by the button click.
     */
    @FXML
    private void getReturnABookBtn(ActionEvent event) {
        loadFXMLIntoPane("/gui/ReturnBookFrame.fxml");
        lblPane.setText("Return Book");
    }

    /**
     * Handles the "Notifications" button click event. Loads the notifications frame.
     *
     * @param event the action event triggered by the button click.
     */
    @FXML
    private void getNotificationsBtn(ActionEvent event) {
        loadFXMLIntoPane("/gui/NotificationsFrame.fxml");
        lblPane.setText("Notifications");
    }

    /**
     * Handles the "Borrowed Books" button click event. Loads the borrowed books frame and initializes it with user data.
     *
     * @param event the action event triggered by the button click.
     */
    @FXML
    private void getBorrowedBooksBtn(ActionEvent event) {
        IController genericController = loadFXMLIntoPane("/gui/ShowBorrowedBooksFrame.fxml");
        lblPane.setText("Borrowed Books");
        User user = ClientUI.chat.getClientUser();
        Subscriber subscriber = ClientUI.chat.requestServerForSubscriber("" + user.getId());
        if (genericController instanceof ShowBorrowedBooksController) {
            ShowBorrowedBooksController borrowedController = (ShowBorrowedBooksController) genericController;
            genericController.setObject(subscriber);
            borrowedController.initializeBorrowedBooks();
        }
    }

    /**
     * Handles the "Reservations" button click event. Loads the reservations frame.
     *
     * @param event the action event triggered by the button click.
     */
    @FXML
    private void getReservationsBtn(ActionEvent event) {
        loadFXMLIntoPane("/gui/ReservationFrame.fxml");
        lblPane.setText("Reservation");
    }

    /**
     * Handles the "Personal Info" button click event. Loads the personal information frame and initializes it with user data.
     *
     * @param event the action event triggered by the button click.
     */
    @FXML
    private void getPersonalInfoBtn(ActionEvent event) {
        IController genericController = loadFXMLIntoPane("/gui/SubscriberInfoFrame.fxml");
        User user = ClientUI.chat.getClientUser();
        Subscriber subscriber = ClientUI.chat.requestServerForSubscriber("" + user.getId());
        if (genericController instanceof SubscriberInfoFrameController) {
            SubscriberInfoFrameController infoController = (SubscriberInfoFrameController) genericController;
            infoController.setMainController(this);
            infoController.setObject(subscriber);
            infoController.setPermission(user.getType());
            infoController.initializeSubscriberInfo();
        }
        lblPane.setText("Personal Info");
    }

    /**
     * Handles the "Logout" button click event. Logs out the user and displays the logout confirmation screen.
     *
     * @param event the action event triggered by the button click.
     */
    @FXML
    private void getLogoutBtn(ActionEvent event) {
        try {
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            if (currentStage != null) {
                (new NoticeFrameForLogout()).start(currentStage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the "Search" button click event. Loads the search book frame.
     *
     * @param event the action event triggered by the button click.
     */
    @FXML
    private void getSearchBtn(ActionEvent event) {
        loadFXMLIntoPane("/gui/SearchBookFrame.fxml");
        lblPane.setText("Browse Books");
    }

    /**
     * Loads a new FXML file into the menu pane and returns its controller.
     *
     * @param fxmlFile the path to the FXML file.
     * @return the controller of the loaded FXML, if it implements {@code IController}.
     */
    IController loadFXMLIntoPane(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Node node = loader.load();
            pane.getChildren().clear();
            pane.getChildren().add(node);
            Object genericController = loader.getController();
            IController controller = null;
            if (genericController instanceof IController) {
                controller = (IController) genericController;
                controller.setPermission(permission);
                controller.setMainController(this);
                controller.initializeFrame();
            }
            return controller;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Sets the title of the pane in the menu UI.
     *
     * @param title the title to be displayed in the pane.
     */
    public void setPaneTitle(String title) {
        lblPane.setText(title);
    }
}
