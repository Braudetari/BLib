package gui;

import java.util.ArrayList;
import java.util.List;

import client.ClientUI;
import common.*;
import common.User.UserType;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * Controller class for managing the borrowed books UI.
 * This class handles the display and functionality related to borrowed books,
 * such as extending loan durations and viewing details.
 */
public class ShowBorrowedBooksController implements IController {

    /**
     * Button to extend the loan period of a selected borrowed book.
     */
    @FXML
    private Button btnExtend;

    /**
     * TableView for displaying the list of borrowed books.
     */
    @FXML
    private TableView<BorrowedBook> borrowedBooksTable;

    /**
     * TableColumn for displaying the ID of borrowed books.
     */
    @FXML
    private TableColumn<BorrowedBook, Integer> colBookId;

    /**
     * TableColumn for displaying the name of borrowed books.
     */
    @FXML
    private TableColumn<BorrowedBook, String> colBookName;

    /**
     * TableColumn for displaying the borrowed date of books.
     */
    @FXML
    private TableColumn<BorrowedBook, String> colBorrowedDate;

    /**
     * TableColumn for displaying the return date of borrowed books.
     */
    @FXML
    private TableColumn<BorrowedBook, String> colReturnDate;

    /**
     * The currently selected borrowed book.
     */
    private BorrowedBook selectedBook = null;

    /**
     * List of borrowed books fetched from the server.
     */
    private List<BorrowedBook> borrowedBooksList = null;

    /**
     * List of extendable statuses for the borrowed books.
     */
    private List<Boolean> extendableList = null;

    /**
     * ObservableList for managing the data in the TableView.
     */
    private ObservableList<BorrowedBook> borrowedBooksData = FXCollections.observableArrayList();

    /**
     * Subscriber whose borrowed books are being displayed.
     */
    private Subscriber importedSubscriber = null;

    /**
     * Initializes the borrowed books TableView and fetches data from the server.
     */
    public void initializeBorrowedBooks() {
        btnExtend.setVisible(true);
        btnExtend.setDisable(true);

        colBookId.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getBorrowedBook().getId()));
        colBookName.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getBorrowedBook().getName()));
        colBorrowedDate.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getBorrowedDate().toString()));
        colReturnDate.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getReturnDate().toString()));

        borrowedBooksTable.setItems(borrowedBooksData);

        RefreshBorrowedBooks();
    }

    /**
     * Handles the selection of a row in the TableView.
     * Enables or disables the "Extend" button based on the selected book's extendability.
     * 
     * @param event MouseEvent triggered by row selection.
     * @throws Exception If an error occurs while processing the selection.
     */
    @FXML
    private void SelectRow(MouseEvent event) throws Exception {
        selectedBook = borrowedBooksTable.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            int index = borrowedBooksList.indexOf(selectedBook);
            btnExtend.setDisable(!extendableList.get(index));
        } else {
            btnExtend.setDisable(true);
        }
    }

    /**
     * Handles the action of extending the loan period for the selected book.
     * Sends a request to the server and displays the result.
     * 
     * @param event ActionEvent triggered by clicking the "Extend" button.
     */
    @FXML
    private void Extend(ActionEvent event) {
        int success = ClientUI.chat.requestServerToExtendBookReturnDate(selectedBook.getBorrowedBook().getId(), 14);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Extend Loan");

        if (success > 0) {
            alert.setHeaderText("Loan Extended Successfully");
            alert.setContentText("The loan for '" + selectedBook.getBorrowedBook().getName() + "' has been extended.");
        } else {
            alert.setHeaderText("Failed to Extend Loan");
            String errorMessage = String.format("The loan for '%s' could not be extended.\n%s", selectedBook.getBorrowedBook().getName(), ClientUI.chat.getClientLastResponses()[2]);
            alert.setContentText(errorMessage);
        }

        alert.showAndWait();
        RefreshBorrowedBooks();
    }

    /**
     * Starts the UI for displaying borrowed books.
     * 
     * @param primaryStage The primary stage for the application.
     * @throws Exception If an error occurs while loading the UI.
     */
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getResource("/gui/ShowBorrowedBooksFrame.fxml").openStream());
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/style.css").toExternalForm());
        primaryStage.setResizable(false);
        primaryStage.setTitle("Show Borrowed Books");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Refreshes the data in the TableView by fetching the latest borrowed books from the server.
     */
    private void RefreshBorrowedBooks() {
        borrowedBooksData.clear();

        borrowedBooksList = ClientUI.chat.requestServerForBorrowedBooksBySubscriber(importedSubscriber.getSubscriberId());

        List<Integer> intList = new ArrayList<>();
        for (BorrowedBook bb : borrowedBooksList) {
            intList.add(bb.getBorrowedBook().getId());
        }
        extendableList = ClientUI.chat.requestServerForBookListExtendability(intList);

        if (borrowedBooksList != null) {
            borrowedBooksData.addAll(borrowedBooksList);
        }
    }

    /**
     * Sets the main menu controller.
     * 
     * @param controller The main menu controller.
     */
    @Override
    public void setMainController(MenuUIController controller) {
        // Not implemented
    }

    /**
     * Sets the imported subscriber whose borrowed books are being displayed.
     * 
     * @param object The Subscriber object.
     */
    @Override
    public void setObject(Object object) {
        this.importedSubscriber = (Subscriber) object;
    }

    /**
     * Initializes the frame with the given object.
     * 
     * @param object The object used for initialization.
     */
    @Override
    public void initializeFrame(Object object) {
        // Not implemented
    }

    /**
     * Sets the user permissions for the controller.
     * 
     * @param type The user type defining permissions.
     */
    @Override
    public void setPermission(UserType type) {
        // Not implemented
    }

    /**
     * Initializes the frame without additional parameters.
     */
    @Override
    public void initializeFrame() {
        // Not implemented
    }
}
