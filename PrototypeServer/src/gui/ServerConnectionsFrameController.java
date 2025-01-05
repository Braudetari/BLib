package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import server.BLibServer;
import server.ServerUI;
import server.ConnectionToClientInfo;

public class ServerConnectionsFrameController  {
	private static BLibServer server;
	private Thread threadShowClients;
	private boolean flagKillThread = false;
	String temp="";
	public final static String title = "Server - Client Connections";
	private final ObservableList<ConnectionToClientInfo> observableClients = FXCollections.observableArrayList();	
	
	@FXML
	private Button btnShutdown = null;
	@FXML
	private Label lblTable = null;
	@FXML
	private TableView<ConnectionToClientInfo> tblConnections;
	@FXML
	private TableColumn<ConnectionToClientInfo, String> tblColumnHostname;
	@FXML
	private TableColumn<ConnectionToClientInfo, String> tblColumnIp;
	@FXML
	private TableColumn<ConnectionToClientInfo, ConnectionToClientInfo.ClientConnectionStatus> tblColumnStatus;

	@FXML
	private void initialize() {
		tblColumnHostname.setCellValueFactory(new PropertyValueFactory<>("name"));
		tblColumnIp.setCellValueFactory(new PropertyValueFactory<>("ip"));
		tblColumnStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        tblConnections.setItems(observableClients);
	}
	
	public void start(Stage primaryStage, BLibServer server) throws Exception {	
		this.server = server;
		
	    FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ServerConnections.fxml"));
	    Parent root = loader.load();
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/gui/ServerConnections.css").toExternalForm());
		primaryStage.setTitle(title);
		primaryStage.setScene(scene);
		
		primaryStage.show();
		ServerConnectionsFrameController controller = loader.getController();
		controller.startShowClientsThread();
	}
	
	@FXML
	public void Shutdown(ActionEvent event) throws Exception {
		flagKillThread = true;
		if(server.isListening()) {
			
			server.stopListening();
			server.close();
		}
		System.out.println("Server Exit - Server Connection Frame");
		System.exit(0);		
	}
	
	private void startShowClientsThread() {
		threadShowClients = new Thread(() -> {
			showClients();
		});
	    threadShowClients.start();
	}
	
	private void showClients() {
		while(true) {
			if(flagKillThread)
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