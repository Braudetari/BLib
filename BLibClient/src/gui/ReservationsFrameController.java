package gui;

import java.util.ArrayList;
import java.util.List;

import client.ClientUI;
import common.Book;
import common.User.UserType;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * Controller class for the Reservations Frame UI.
 * Manages the display and interaction with the reserved books table.
 */
public class ReservationsFrameController implements IController {

    /**
     * Button for extending the reservation of a book.
     */
    @FXML
    private Button btnExtend;

    /**
     * TableView for displaying reserved books.
     */
    @FXML
    private TableView<Book> reservedTable;

    /**
     * TableColumn for displaying the book name.
     */
    @FXML
    private TableColumn<Book, String> colBookName;

    /**
     * TableColumn for displaying the book ID.
     */
    @FXML
    private TableColumn<Book, Integer> colBookId;

    /**
     * TableColumn for displaying the status of the book (Available or Reserved).
     */
    @FXML
    private TableColumn<Book, String> colStatus;

    /**
     * List to store reserved books retrieved from the server.
     */
    private List<Book> reservedList = null;

    /**
     * List to store the availability status of books as boolean values.
     */
    private List<Boolean> booksAvailibility = null;

    /**
     * ObservableList for managing and updating the TableView with reserved books.
     */
    private ObservableList<Book> reservedData;

    /**
     * Initializes the Reservations Frame.
     * Configures the TableView columns and populates it with data from the server.
     */
    public void initializeFrame() {
        reservedData = FXCollections.observableArrayList();
        colBookId.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getId()));
        colBookName.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getName()));
        colStatus.setCellValueFactory(cellData -> {
            Book book = cellData.getValue();
            int row = reservedData.indexOf(book);
            if (row >= 0) {
                boolean available = booksAvailibility.get(row);
                return new SimpleObjectProperty<>((available) ? "Available" : "Reserved");
            } else {
                return new SimpleObjectProperty<>("");
            }
        });

        reservedList = ClientUI.chat.requestServerForReservedBooks(ClientUI.chat.getClientUser().getId());
        if (reservedList == null) {
            reservedList = new ArrayList<Book>();
        }
        try {
        	booksAvailibility = (List<Boolean>)((Object[])ClientUI.chat.requestServerForBookListAvailibilityInfo(reservedList))[0];
        } catch (Exception e) {
            booksAvailibility = new ArrayList<Boolean>();
        }
        reservedTable.setItems(reservedData);
        reservedData.clear();
        if (reservedList != null) {
            reservedData.addAll(reservedList);
        }
    }

    /**
     * Starts the Reservations Frame.
     * 
     * @param primaryStage The primary stage for the application.
     * @throws Exception if the FXML file cannot be loaded.
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
     * Initializes the frame with a given object.
     * 
     * @param object The object used to initialize the frame.
     */
    @Override
    public void initializeFrame(Object object) {
        // TODO Auto-generated method stub
    }

    /**
     * Sets the permission level for the current user.
     * 
     * @param type The user type that defines permissions.
     */
    @Override
    public void setPermission(UserType type) {
        // TODO Auto-generated method stub
    }

    /**
     * Sets an object in the controller.
     * 
     * @param object The object to set in the controller.
     */
    @Override
    public void setObject(Object object) {
        // TODO Auto-generated method stub
    }

    /**
     * Sets the main menu controller for the UI.
     * 
     * @param controller The main menu controller to set.
     */
    @Override
    public void setMainController(MenuUIController controller) {
        // TODO Auto-generated method stub
    }
}