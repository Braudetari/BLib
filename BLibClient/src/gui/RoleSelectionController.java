package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RoleSelectionController {

    @FXML
    private VBox buttonPanel;

    @FXML
    private Pane dynamicPane;

    public void start(Stage primaryStage, String userType) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        Pane root = loader.load(getClass().getResource("/gui/RoleSelection.fxml").openStream());
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/RoleSelection.css").toExternalForm());
        RoleSelectionController controller = loader.getController();
        controller.initialize(userType);
        primaryStage.setTitle("Role Selection");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initialize(String userType) {
        switch (userType.toLowerCase()) {
            case "librarian":
                loadLibrarianButtons();
                break;
            case "guest":
                loadGuestButtons();
                break;
            case "client":
                loadClientButtons();
                break;
            default:
                System.out.println("Invalid user type.");
        }
    }

    private void loadLibrarianButtons() {
        buttonPanel.getChildren().clear();

        addButton("Search Book", this::showSearchBooksPane);
        addButton("Search Client", this::showSearchClientsPane);
        addButton("Loan Book", this::showLoanBookPane);
        addButton("Return Book", this::showReturnBookPane);
        addButton("Manage", this::showManagePane);
        addButton("Register Client", this::showRegisterClientPane);
        addButton("Notifications", this::showNotificationsPane);
    }


    private void loadClientButtons() {
        buttonPanel.getChildren().clear();
        addButton("Borrow Book", () -> showContent("Client: Borrow Book"));
        addButton("Return Book", () -> showContent("Client: Return Book"));
        addButton("Check Account Status", () -> showContent("Client: Check Account Status"));
    }

	private void loadGuestButtons() {
        buttonPanel.getChildren().clear();
        addButton("Search Books", this::showSearchBooksPane);
    }

    private void addButton(String text, Runnable action) {
        Button button = new Button(text);
        button.getStyleClass().add("button");
        button.setOnAction(e -> action.run());
        buttonPanel.getChildren().add(button);
    }

    private void showSearchBooksPane() {
        try {
            FXMLLoader loader = new FXMLLoader();
            Pane searchPane = loader.load(getClass().getResource("/gui/SearchBookFrame.fxml").openStream());
            dynamicPane.getChildren().setAll(searchPane);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showSearchClientsPane() {
        try {
            FXMLLoader loader = new FXMLLoader();
            Pane searchPane = loader.load(getClass().getResource("/gui/SearchClientFrame.fxml").openStream());
            dynamicPane.getChildren().setAll(searchPane);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showLoanBookPane() {
        try {
            FXMLLoader loader = new FXMLLoader();
            Pane loanPane = loader.load(getClass().getResource("/gui/LoanBookFrame.fxml").openStream());
            dynamicPane.getChildren().setAll(loanPane);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showReturnBookPane() {
        try {
            FXMLLoader loader = new FXMLLoader();
            Pane returnPane = loader.load(getClass().getResource("/gui/ReturnBookFrame.fxml").openStream());
            dynamicPane.getChildren().setAll(returnPane);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showManagePane() {
        try {
            FXMLLoader loader = new FXMLLoader();
            Pane managePane = loader.load(getClass().getResource("/gui/ManageFrame.fxml").openStream());
            dynamicPane.getChildren().setAll(managePane);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showRegisterClientPane() {
        try {
            FXMLLoader loader = new FXMLLoader();
            Pane registerPane = loader.load(getClass().getResource("/gui/RegisterClientFrame.fxml").openStream());
            dynamicPane.getChildren().setAll(registerPane);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showNotificationsPane() {
        try {
            FXMLLoader loader = new FXMLLoader();
            Pane notificationsPane = loader.load(getClass().getResource("/gui/NotificationsFrame.fxml").openStream());
            dynamicPane.getChildren().setAll(notificationsPane);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void showContent(String content) {
        dynamicPane.getChildren().clear();
        Button contentButton = new Button(content);
        contentButton.setStyle("-fx-background-color: transparent; -fx-text-fill: black; -fx-font-size: 16px;");
        dynamicPane.getChildren().add(contentButton);
    }
}
