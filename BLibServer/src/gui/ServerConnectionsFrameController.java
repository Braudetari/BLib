package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import server.BLibServer;
import server.ConnectionToClientInfo;

/**
 * Controller class for managing the Server Connections UI.
 * Displays a list of active client connections and provides the ability to shut down the server.
 */
public class ServerConnectionsFrameController {

    /**
     * The server instance being managed.
     */
    private static BLibServer server;

    /**
     * Thread for continuously updating the client connections list.
     */
    private Thread threadShowClients;

    /**
     * Flag to terminate the client connections update thread.
     */
    private boolean flagKillThread = false;

    /**
     * Title of the Server Connections window.
     */
    public final static String title = "Server - Client Connections";

    /**
     * ObservableList for managing client connection data in the TableView.
     */
    private final ObservableList<ConnectionToClientInfo> observableClients = FXCollections.observableArrayList();

    /**
     * Button to shut down the server.
     */
    @FXML
    private Button btnShutdown = null;

    /**
     * Label for the TableView of client connections.
     */
    @FXML
    private Label lblTable = null;

    /**
     * TableView for displaying client connection information.
     */
    @FXML
    private TableView<ConnectionToClientInfo> tblConnections;

    /**
     * TableColumn for displaying the hostname of connected clients.
     */
    @FXML
    private TableColumn<ConnectionToClientInfo, String> tblColumnHostname;

    /**
     * TableColumn for displaying the IP address of connected clients.
     */
    @FXML
    private TableColumn<ConnectionToClientInfo, String> tblColumnIp;

    /**
     * TableColumn for displaying the connection status of clients.
     */
    @FXML
    private TableColumn<ConnectionToClientInfo, ConnectionToClientInfo.ClientConnectionStatus> tblColumnStatus;

    /**
     * Initializes the TableView columns and binds them to the appropriate client connection properties.
     */
    @FXML
    private void initialize() {
        tblColumnHostname.setCellValueFactory(new PropertyValueFactory<>("name"));
        tblColumnIp.setCellValueFactory(new PropertyValueFactory<>("ip"));
        tblColumnStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        tblConnections.setItems(observableClients);
    }

    /**
     * Starts the Server Connections UI.
     * 
     * @param primaryStage The primary stage for the application.
     * @param server The server instance to manage.
     * @throws Exception If there is an error loading the UI.
     */
    public void start(Stage primaryStage, BLibServer server) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ServerConnections.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/ServerConnections.css").toExternalForm());
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();

        primaryStage.show();
        ServerConnectionsFrameController controller = loader.getController();
        controller.startShowClientsThread();
    }

    /**
     * Handles the shutdown button action to stop the server and exit the application.
     * 
     * @param event The ActionEvent triggered by the Shutdown button.
     * @throws Exception If an error occurs during the shutdown process.
     */
    @FXML
    public void Shutdown(ActionEvent event) throws Exception {
        flagKillThread = true;
        if (server.isListening()) {
            server.stopListening();
            server.close();
        }
        System.out.println("Server Exit - Server Connection Frame");
        System.exit(0);
    }

    /**
     * Starts a thread to continuously update the client connections list.
     */
    private void startShowClientsThread() {
        threadShowClients = new Thread(() -> {
            showClients();
        });
        threadShowClients.start();
    }

    /**
     * Continuously updates the client connections list in the TableView.
     */
    private void showClients() {
        while (true) {
            if (flagKillThread)
                return;
            observableClients.clear();
            observableClients.addAll(server.getClientConnectionsList());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
