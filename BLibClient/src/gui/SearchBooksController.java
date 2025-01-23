package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class SearchBooksController {

    @FXML
    private ComboBox<String> searchCriteriaCombo;

    @FXML
    private TextField searchField;

    public void initialize() {
        searchCriteriaCombo.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleSearch() {
        String criteria = searchCriteriaCombo.getValue();
        String term = searchField.getText();

        if (term.isEmpty()) {
            System.out.println("Please enter a search term.");
        } else {
            System.out.println("Searching for books by " + criteria + ": " + term);
        }
    }

    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        Pane root = loader.load(getClass().getResource("/gui/SearchBoosPane.fxml").openStream());
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/SearchBooksPane.css").toExternalForm());
        primaryStage.setTitle("Search Books");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
