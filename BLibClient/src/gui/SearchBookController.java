package gui;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class SearchBookController {

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
}
