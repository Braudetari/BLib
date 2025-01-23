package gui;

import common.Book;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class SearchBookFrameController {

    @FXML
    private ComboBox<String> searchOptions;

    @FXML
    private TextField searchField;

    @FXML
    private Button btnSearch;

    @FXML
    private TableView<Book> booksTable;

    @FXML
    private TableColumn<Book, String> colBookName;

    @FXML
    private TableColumn<Book, String> colCategory;

    @FXML
    private TableColumn<Book, String> colStatus;

    @FXML
    private TableColumn<Book, String> colLocation;

    @FXML
    private TableColumn<Book, String> colReturnDate;

    private ObservableList<Book> booksData = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Add search options
        searchOptions.getItems().addAll("Search by Name", "Search by Description", "Search by Genre");
        searchOptions.setValue("Search by Name");

        
        booksTable.setItems(booksData);
    }

    @FXML
    private void handleSearchAction(ActionEvent event) {
        String selectedOption = searchOptions.getValue();
        String searchText = searchField.getText().trim();

        if (searchText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Search text cannot be empty.");
            return;
        }

        
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getResource("/gui/SearchBookFrame.fxml").openStream());
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/SearchBookFrame.css").toExternalForm());
        primaryStage.setTitle("Search Book");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
