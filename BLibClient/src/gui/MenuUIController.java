package gui;

import java.io.IOException;

import client.ChatClient;
import client.ClientUI;
import common.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MenuUIController {

	@FXML
	private Button btnSearch = null;
	@FXML
	private Button btnReport = null;
	@FXML
	private Button btnLogout = null;
	@FXML
	private Button btnManager = null;
	@FXML
	private Button btnBorrow = null;
	@FXML
	private Button btnReturnABook = null;
	@FXML
	private Button btnNotifications = null;
	@FXML
	private Button btnBorrowedBooks = null;
	@FXML
	private Button btnReservations = null;
	@FXML
	private Button btnPersonalInfo = null;
	@FXML
	private Label lblWelocome = null;
	@FXML
	private VBox pane=null;
	@FXML
	private ScrollPane scrollPane=null;
	@FXML
	private AnchorPane paneScreen=null;
	@FXML
	private AnchorPane paneButtons=null;
	
	private User.UserType permission;
	private String name;
	private static Subscriber importedSubscriber;
	
	private void initializeButtons() {
		Button[] listOfButtons = {btnSearch,btnNotifications, btnManager, btnBorrow, btnReturnABook,btnBorrowedBooks,btnReservations,btnPersonalInfo};
		User.UserType[] buttonPermission = {User.UserType.GUEST,User.UserType.SUBSCRIBER, User.UserType.LIBRARIAN,User.UserType.LIBRARIAN, User.UserType.LIBRARIAN,User.UserType.SUBSCRIBER, User.UserType.SUBSCRIBER, User.UserType.SUBSCRIBER};
		int currentButtonPos = 50;
		int buttonPadding = 50;
		lblWelocome.setText("Welcome "+name);
		for(int i=0; i < listOfButtons.length; i++) {
			boolean checkPermission = buttonPermission[i].equals(permission);
			if(buttonPermission[i].equals(User.UserType.GUEST)) {
				checkPermission = true;
			}
			listOfButtons[i].setVisible(checkPermission);
			listOfButtons[i].setDisable(!checkPermission);
		}
		
	}
	
	private void initialize(User.UserType permission, String name) {
		this.permission = permission;
		this.name = name;
	}
	
	
	
	public void start(Stage primaryStage, User.UserType permission, String name) throws IOException {
		//this.permission=permission;
		//this.name=name;
		FXMLLoader loader = new FXMLLoader();
		Pane root = loader.load(getClass().getResource("/gui/MenuUI.fxml").openStream());
		Scene scene = new Scene(root);			
		scene.getStylesheets().add(getClass().getResource("/gui/style.css").toExternalForm());
		primaryStage.setResizable(false);
		primaryStage.setTitle("MenuUI");
		primaryStage.setScene(scene);		
		primaryStage.centerOnScreen();
		primaryStage.show();
		MenuUIController frame = loader.getController();
		frame.initialize(permission, name);
		frame.initializeButtons();
			
	}
	
	@FXML
	private void getReportBtn(ActionEvent event) {
		loadFXMLIntoPane("/gui/ReportFrame.fxml"); //need to be complete
	}
	
	@FXML
	private void getManagerBtn(ActionEvent event) {
		loadFXMLIntoPane("/gui/SubscriberManagerFrame.fxml"); //need to be complete
	}
	
	@FXML
	private void getBorrowBtn(ActionEvent event) {
		loadFXMLIntoPane("/gui/BorrowBookFrame.fxml");
	}
	
	@FXML
	private void getReturnABookBtn(ActionEvent event) {
		loadFXMLIntoPane("/gui/ReturnBookFrame.fxml");
	}
	
	@FXML
	private void getNotificationsBtn(ActionEvent event) {
		loadFXMLIntoPane("/gui/NotificationsFrame.fxml");
	}
	
	@FXML
	private void getBorrowedBooksBtn(ActionEvent event) {
		loadFXMLIntoPane("/gui/ShowBorrowedBooksFrame.fxml");
	}
	
	@FXML
	private void getReservationsBtn(ActionEvent event) {
		loadFXMLIntoPane("/gui/ReservationsFrame.fxml");
	}
	
	@FXML
	private void getPersonalInfoBtn(ActionEvent event) {
		loadFXMLIntoPane("/gui/SubscriberInfoFrame.fxml");
	}
	
	@FXML
	private void getLogoutBtn(ActionEvent event) {
		try {
			Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			if (currentStage == null) {
			    System.out.println("Error: currentStage is null");
			}
			else {
				(new NoticeFrameForLogout()).start(currentStage);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@FXML 
	private void getSearchBtn(ActionEvent event) {
		loadFXMLIntoPane("/gui/SearchBookFrame.fxml");
	}
	
	/**
	 * Load new FXML into MenuUI's pane
	 * @param fxmlFile
	 * @return controller of new Child
	 */
	IController loadFXMLIntoPane(String fxmlFile) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
			Node node = loader.load();
			pane.getChildren().clear();
			pane.getChildren().add(node);
			//Requires all sub-controllers ot be IController
			Object genericController = loader.getController();
			IController controller = null;
			if(genericController instanceof IController) {
				controller = (IController)genericController;
				controller.setPermission(permission);
				controller.setMainController(this);
				controller.initializeFrame();
			}
			return controller;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
}
