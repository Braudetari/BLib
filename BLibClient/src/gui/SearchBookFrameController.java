package gui;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import client.ClientUI;
import common.Book;
import common.DateUtil;
import common.User;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Controller class for the Search Book Frame UI.
 * Provides functionality for searching books by various criteria, displaying search results,
 * and performing actions like lending or reserving books.
 */
public class SearchBookFrameController implements IController {

    /**
     * Root AnchorPane for the parent node.
     */
    @FXML
    private AnchorPane parentNode = null;

    /**
     * ComboBox for selecting search options (e.g., by name, description, or genre).
     */
    @FXML
    private ComboBox<String> searchOptions;

    /**
     * TextField for entering search terms.
     */
    @FXML
    private TextField searchField;

    /**
     * Button for initiating the search.
     */
    @FXML
    private Button btnSearch;

    /**
     * Button for lending a selected book.
     */
    @FXML
    private Button btnLend;

    /**
     * Button for reserving a selected book.
     */
    @FXML
    private Button btnReserve;

    /**
     * TableView for displaying search results (list of books).
     */
    @FXML
    private TableView<Book> booksTable;

    /**
     * TableColumn for displaying book names.
     */
    @FXML
    private TableColumn<Book, String> colBookName;

    /**
     * TableColumn for displaying book categories (genres).
     */
    @FXML
    private TableColumn<Book, String> colCategory;

    /**
     * TableColumn for displaying book descriptions.
     */
    @FXML
    private TableColumn<Book, String> colDescription;

    /**
     * TableColumn for displaying the availability status of books.
     */
    @FXML
    private TableColumn<Book, String> colStatus;

    /**
     * TableColumn for displaying the location of books.
     */
    @FXML
    private TableColumn<Book, String> colLocation;

    /**
     * TableColumn for displaying the closest return date of books.
     */
    @FXML
    private TableColumn<Book, String> colReturnDate;

    /**
     * Reference to the main menu controller.
     */
    private MenuUIController mainController;

    /**
     * Currently selected book in the table.
     */
    private Book selectedBook = null;

    /**
     * Permission level of the current user.
     */
    private User.UserType permission;

    /**
     * List of books matching the search criteria.
     */
    private List<Book> booksList = null;

    /**
     * List of availability statuses for the books.
     */
    private List<String> booksAvailibility = null;

    /**
     * List of closest return dates for the books.
     */
    private List<String> booksClosestReturnDate = null;

    /**
     * ObservableList to hold and display book data in the table.
     */
    private ObservableList<Book> booksData = FXCollections.observableArrayList();

    /**
     * Initializes the frame and sets up the UI components.
     * Populates the search options and configures table columns.
     */
    public void initializeFrame() {
        initializeButtons();
        searchOptions.getItems().addAll("Search by Name", "Search by Description", "Search by Genre");
        searchOptions.setValue("Search by Name");

        booksAvailibility = new ArrayList<>();
        booksClosestReturnDate = new ArrayList<>();

        colBookName.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getName()));
        colCategory.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getGenre()));
        colLocation.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getLocation()));
        colDescription.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDescription()));
        colStatus.setCellValueFactory(cellData -> {
            Book book = cellData.getValue();
            int row = booksData.indexOf(book);
            if (row >= 0) {
                String availibility = booksAvailibility.get(row);
                return new SimpleObjectProperty<>(availibility);
            } else {
                return new SimpleObjectProperty<>("");
            }
        });
        colReturnDate.setCellValueFactory(cellData -> {
            Book book = cellData.getValue();
            int row = booksData.indexOf(book);
            if (row >= 0) {
                String closestReturnDate = booksClosestReturnDate.get(row);
                return new SimpleObjectProperty<>(closestReturnDate);
            } else {
                return new SimpleObjectProperty<>("");
            }
        });
        booksTable.setItems(booksData);
    }

    /**
     * Configures the visibility of buttons based on user permissions.
     */
    private void initializeButtons() {
        Button[] listOfButtons = {btnLend, btnReserve};
        User.UserType[] buttonPermission = {User.UserType.LIBRARIAN, User.UserType.SUBSCRIBER};
        for (int i = 0; i < listOfButtons.length; i++) {
            boolean checkPermission = buttonPermission[i].equals(permission);
            listOfButtons[i].setVisible(checkPermission);
        }
    }

    /**
     * Sets the permission level for the current user.
     * 
     * @param permission The user type to set.
     */
    public void setPermission(User.UserType permission) {
        this.permission = permission;
    }

    /**
     * Handles the search button click event.
     * Performs a search based on the selected criteria and updates the table with results.
     * 
     * @param event The action event triggered by the search button.
     */
    @FXML
    private void handleSearchAction(ActionEvent event) {
        String selectedOption = searchOptions.getValue();
        String searchText = searchField.getText().trim();

        if (searchText.isEmpty()) {
            searchText = " ";
        }
        if (booksList != null) booksList.clear();
        switch (selectedOption) {
            case "Search by Name":
                booksList = ClientUI.chat.requestServerSearchForBooks("book_name", searchText);
                break;
            case "Search by Description":
                booksList = ClientUI.chat.requestServerSearchForBooks("book_description", searchText);
                break;
            case "Search by Genre":
                booksList = ClientUI.chat.requestServerSearchForBooks("book_genre", searchText);
                break;
            default:
                break;
        }
        if (booksList == null) {
            booksList = new ArrayList<>();
        }

        if (!permission.equals(User.UserType.LIBRARIAN)) {
            booksList = filterUniqueBooks(booksList);
        }

        Object[] bookInfoList = ClientUI.chat.requestServerForBookListAvailibilityInfo(booksList);
        if (bookInfoList == null) {
            showAlert(AlertType.ERROR, "Error", "Could not get book search info");
            return;
        }

        updateBookAvailability(bookInfoList);
        booksData.clear();
        if (booksList != null) {
            booksData.addAll(booksList);
        }
    }

    /**
     * Filters the book list to include only unique books by serial ID for non-librarian users.
     * 
     * @param booksList The original list of books.
     * @return A filtered list of unique books.
     */
    private List<Book> filterUniqueBooks(List<Book> booksList) {
        List<Book> booksListUnique = new ArrayList<>();
        for (Book book : booksList) {
            boolean bookFound = false;
            for (Book bookUnique : booksListUnique) {
                if (book.getSerial_id() == bookUnique.getSerial_id()) {
                    bookFound = true;
                    break;
                }
            }
            if (!bookFound) booksListUnique.add(book);
        }
        return booksListUnique;
    }

    /**
     * Updates the availability and closest return date information for books.
     * 
     * @param bookInfoList The book info list containing availability and return date data.
     */
    private void updateBookAvailability(Object[] bookInfoList) {
        try {
            @SuppressWarnings("unchecked")
            List<Boolean> booleanList = (List<Boolean>) bookInfoList[0];
            @SuppressWarnings("unchecked")
            List<LocalDate> dateList = (List<LocalDate>) bookInfoList[1];
            booksAvailibility.clear();
            booksClosestReturnDate.clear();
            for (int i = 0; i < booleanList.size(); i++) {
                booksAvailibility.add(booleanList.get(i) ? "Available" : "Unavailable");
                booksClosestReturnDate.add(dateList.get(i) != null ? DateUtil.DateToString(dateList.get(i)) : "");
            }
        } catch (Exception e) {
            System.err.println("Could not show book availability info");
        }
    }

    /**
     * Handles row selection in the table. Enables or disables buttons based on the selected book's availability.
     * 
     * @param event The mouse event triggered by selecting a row in the table.
     * @throws Exception If an error occurs during row selection.
     */
    @FXML
    private void SelectRow(MouseEvent event) throws Exception {
        selectedBook = booksTable.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            if (booksAvailibility.get(booksData.indexOf(selectedBook)).equals("Available")) {
                btnReserve.setDisable(true);
                btnLend.setDisable(false);
            } else {
                btnReserve.setDisable(false);
                btnLend.setDisable(true);
            }
        } else {
            btnReserve.setDisable(true);
            btnLend.setDisable(true);
        }
    }

    /**
     * Handles the lend button click event. Opens the Borrow Book Frame for lending the selected book.
     * 
     * @param event The action event triggered by the lend button.
     */
    @FXML
    private void Lend(ActionEvent event) {
        try {
            IController genericController = mainController.loadFXMLIntoPane("/gui/BorrowBookFrame.fxml");
            if (genericController instanceof BorrowBookFrameController) {
                BorrowBookFrameController borrowController = (BorrowBookFrameController) genericController;
                borrowController.initializeText("" + selectedBook.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the main menu controller.
     * 
     * @param controller The main menu controller to set.
     */
    @Override
    public void setMainController(MenuUIController controller) {
        this.mainController = controller;
    }

    /**
     * Handles the reserve button click event. Sends a request to reserve the selected book.
     * 
     * @param event The action event triggered by the reserve button.
     */
    @FXML
    private void Reserve(ActionEvent event) {
        try {
            int success = ClientUI.chat.requestServerToReserveBook(selectedBook, ClientUI.chat.getClientUser().getId());
            if (success > 0) {
                showAlert(AlertType.INFORMATION, "Reservation", "The book " + selectedBook.getName() + "(id:" + selectedBook.getId() + ") was reserved successfully");
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Reservation", ClientUI.chat.getClientLastResponses()[2]);
        }
    }

    /**
     * Displays an alert dialog with the given title and message.
     * 
     * @param type The type of the alert.
     * @param title The title of the alert.
     * @param message The message to display in the alert.
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Starts the Search Book Frame UI.
     * 
     * @param primaryStage The primary stage for the application.
     * @param permission The user type permission for the session.
     * @throws Exception If an error occurs during initialization.
     */
    public void start(Stage primaryStage, User.UserType permission) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getResource("/gui/SearchBookFrame.fxml").openStream());
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/style.css").toExternalForm());
        primaryStage.setResizable(false);
        primaryStage.setTitle("Search Book");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Sets an object for the controller.
     * 
     * @param object The object to set.
     */
    @Override
    public void setObject(Object object) {
        // TODO Auto-generated method stub
    }

    /**
     * Initializes the frame with a given object.
     * 
     * @param object The object used for initialization.
     */
    @Override
    public void initializeFrame(Object object) {
        // TODO Auto-generated method stub
    }
}