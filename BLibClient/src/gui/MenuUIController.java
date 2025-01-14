// MenuUIController.java
package gui;

import common.Client;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.fxml.FXML;

public class MenuUIController extends Application {

    @FXML
    private Label messageLabel;

    private Client client1;

    // Initialize method called automatically after FXML elements are loaded
    public void initialize() {
        // Set a default welcome message
        messageLabel.setText("Welcome to the Application!");
    }

    // Event handler for Button 1
    @FXML
    private void handleButton1Click() {
        messageLabel.setText("Register button clicked!");
        // Implement the logic for the Register button
    }

    // Event handler for Button 2
    @FXML
    private void handleButton2Click() {
        messageLabel.setText("Search User button clicked!");
        // Implement the logic for the Search User button
    }

    // Event handler for Button 3
    @FXML
    private void handleButton3Click() {
        messageLabel.setText("Return Book button clicked!");
        // Implement the logic for the Return Book button
    }

    // Event handler for Button 4
    @FXML
    private void handleButton4Click() {
        messageLabel.setText("Reports button clicked!");
        // Implement the logic for the Reports button
    }

    // Event handler for Button 5
    @FXML
    private void handleButton5Click() {
        messageLabel.setText("Lend button clicked!");
        // Implement the logic for the Lend button
    }

    // Default `start` method
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuUI.fxml"));
        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.setTitle("Menu UI");
        primaryStage.show();
    }

    // Overloaded `start` method to accept a Client object
    public void start(Stage primaryStage, Client loggedInClient) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuUI.fxml"));
        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.setTitle("Menu UI");
        primaryStage.show();

        // Pass the client data to the controller
        MenuUIController controller = loader.getController();
        controller.setClient(loggedInClient);
        controller.displayClientInfo();
    }

    // Setter method to inject the Client object
    private void setClient(Client client) {
        this.client1 = client;
    }

    // Display client information
    private void displayClientInfo() {
        if (client1 != null) {
            messageLabel.setText("Logged in as: " + client1.toString());
        } else {
            messageLabel.setText("Client information not available.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
