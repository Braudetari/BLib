package gui;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

public class SearchWindowController {

    @FXML
    private ComboBox<String> searchOptionsComboBox;

    @FXML
    private void initialize() {
        // Dynamically initialize the ComboBox with search options
        searchOptionsComboBox.getItems().addAll(
            "Search by Book Name",
            "Search by Description",
            "Search by Genre"
        );
    }
}
