package gui;

import java.io.IOException;
import java.time.LocalDate;

import client.ClientUI;
import common.DetailedHistory;
import common.Subscriber;
import common.User.UserType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
/**
 * A frame controller for adding notes by librarian to History of a subscriber.
 * In case of problems like if subscriber lost book.
 */
public class AddNoteFrameController implements IController{
	 /** Button for updating the subscriber's history with a note. */
	@FXML
	private Button btnUpdate = null;
	/** Text field for entering the note description. */
	@FXML
	private TextField txtDescription=null;
	/** The history entry to which the note will be added. */
	private DetailedHistory importedHistory = null;
	/** The subscriber whose history is being modified. */
	private Subscriber importedSubscriber = null;
	/** The main controller for managing the UI. */
	private MenuUIController mainController = null;
	/** The ID of the user's history entry. */
	private Integer userHistoryId = 0;

	
	/**
     * Starts the "Add Note" frame.
     * 
     * @param primaryStage the main application stage.
     * @param subscriber the subscriber whose history is being modified.
     * @throws IOException if there is an error loading the FXML file.
     */
	public void start(Stage primaryStage, Subscriber subscriber) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		Pane root = loader.load(getClass().getResource("/gui/AddNoteFrame.fxml").openStream());
		Scene scene = new Scene(root);			
		scene.getStylesheets().add(getClass().getResource("/gui/style.css").toExternalForm());
		primaryStage.setScene(scene);		
		primaryStage.show();
		
	}
	
	/**
     * Handles the update button click event. Adds the note to the subscriber's detailed history
     * if the description field is not empty, and navigates back to the history frame.
     * 
     * @param event the action event triggered by the button click.
     * @throws Exception if an error occurs while adding the note.
     */
	@FXML
	private void getUpdateButton(ActionEvent event) throws Exception{
		if(!txtDescription.getText().isEmpty()) {
			DetailedHistory editedHistory=new DetailedHistory(importedHistory);
			editedHistory.setAction(DetailedHistory.ActionType.NOTE);//need to be implemented
			editedHistory.setDescription("Note by librarian "+ ClientUI.chat.getClientName() +": "+ txtDescription.getText() +"--> "+ importedHistory.getDescription());
			editedHistory.setDate(LocalDate.now());
			editedHistory.setUser(null);
			boolean success = ClientUI.chat.requestServerToAddHistoryToSubscriber(editedHistory, importedSubscriber.getDetailedSubscriptionHistory(), importedSubscriber.getSubscriberId());
			if(success) {
				showAlert("Add Note", "Note added to Detailed History");
			}
			else{
				showAlert("Error", "Note could not be added to Detailed History");
			}
		}
		
		Back();
	}
	
	/**
     * Navigates back to the history frame and reinitializes it.
     */
	private void Back() {
		try {
    		IController genericController = mainController.loadFXMLIntoPane("/gui/HistoryFrame.fxml");
    		if(genericController instanceof HistoryFrameController) {
    			HistoryFrameController historyController = (HistoryFrameController)genericController;
    			historyController.setObject(new Object[] {importedSubscriber, (Integer)userHistoryId});
    			historyController.initializeHistory();
    		}
		}
		catch(Exception e) {
		e.printStackTrace();
		}
	}
	
	/**
     * Initializes the frame, coming from interface. Currently, this method does not perform specific initialization.
     * 
     * @param object the initialization data.
     */
	@Override
	public void initializeFrame(Object object) {
		
		
	}

	/**
     * Initializes the frame and sets the title in the main controller.
     */
	@Override
	public void initializeFrame() {
		mainController.setPaneTitle("Add Note");
		
	}
	
	/**
     * Sets the permission level for the user, comes from interface.
     * 
     * @param type the user type to set permissions for.
     */
	@Override
	public void setPermission(UserType type) {
		// TODO Auto-generated method stub
		
	}

	/**
     * Sets the imported objects for this frame, including the subscriber, history entry, and user history ID.
     * 
     * @param object an array containing the imported objects.
     */
	@Override
	public void setObject(Object object) {
		Object[] objectImport = (Object[])object;
		this.importedHistory = (DetailedHistory)objectImport[0];
		this.importedSubscriber = (Subscriber)objectImport[1];
		this.userHistoryId = (Integer)objectImport[2];
		
	}
	
	/**
     * Sets the main controller for this frame.
     * 
     * @param controller the main UI controller.
     */
	@Override
	public void setMainController(MenuUIController controller) {
		this.mainController = controller;
		
	}
	
	/**
     * Displays an alert dialog with the given title and message.
     * 
     * @param title the title of the alert dialog.
     * @param message the message content of the alert dialog.
     */
	private void showAlert(String title, String message) {
		Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
	}
	
	
	
	
}
