package gui;

import java.io.IOException;

import client.ClientUI;
import common.Message;
import common.Subscriber;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class SubscriberSearchFrameController {

    private static final String TITLE = "Subscriber Search";

    @FXML
    private Button btnSearch;

    @FXML
    private TextField idField;

    @FXML
    private Label labelMessage;
    private Subscriber subscriber;
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();

        Pane root = loader.load(getClass().getResource("/gui/SubscriberSearchFrame.fxml").openStream());
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/SubscriberSearchFrame.css").toExternalForm());
        primaryStage.setTitle(TITLE);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @FXML
    public void handleSearchButton(ActionEvent event) {
        String subscriberId = idField.getText();
        labelMessage.setText("Searching for Subscriber ID: " + subscriberId);
        
        // Request subscriber from server
        subscriber = ClientUI.chat.requestSubscriberFromServer(subscriberId);
        
        // Check if subscriber is null
        if (subscriber == null) {
            // Handle the case where the subscriber is not found
            labelMessage.setText("Subscriber not found for ID: " + subscriberId);
            System.out.println("No subscriber found for ID: " + subscriberId);  // Log the failure
        } else {
            // Handle the case where the subscriber is found
           // System.out.println(subscriber.toString());  // Log the subscriber information
            
            
        }
        
        // Close the current window after handling the search
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }


    

}
