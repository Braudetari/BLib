package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SearchClientFrameController {

    @FXML
    private TextField clientCardIdTxt;

    @FXML
    private Button searchBtn;

    @FXML
    private TextArea resultArea;

 void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/SearchClientFrame.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/SearchClientFrame.css").toExternalForm());
        primaryStage.setTitle("Search Client");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @FXML
    private void onSearchBtnClick(ActionEvent event) {
        String cardId = clientCardIdTxt.getText().trim();

        if (cardId.isEmpty()) {
            resultArea.setText("Please enter a client card ID.");
            return;
        }

        // Search for the client in the mock database
      
      
    }
}
