package gui;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class ScreenLoginController {

	@FXML
	private Label labelmsg=null;
	
	
    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private Button btnLogin = null;
    
    @FXML
    private Button btnGuest = null;
    
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        Pane root = loader.load(getClass().getResource("/gui/LoginFrame.fxml").openStream());
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/style.css").toExternalForm());
        primaryStage.setResizable(false);
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @FXML
    private void getLoginBtn(ActionEvent event) throws Exception {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Please fill in both username and password.");
            // You can also display this message in a new alert window.
        } else {
            // Perform login validation (replace with actual logic)
            if (username.equals("admin") && password.equals("1234"))//logged in as librarian 
            {
                //start the window with librarian message
            	((Stage)((Node)event.getSource()).getScene().getWindow()).close(); //close ConnectionFrame
				RoleSelectionController mainPane = new RoleSelectionController();
				mainPane.start(new Stage(),"librarian");
            } else if(username.equals("client") && password.equals("123")) {//logged in as a client
            	((Stage)((Node)event.getSource()).getScene().getWindow()).close(); //close ConnectionFrame
				RoleSelectionController mainPane = new RoleSelectionController();
				mainPane.start(new Stage(),"client");
            }
            else {
            	String noticeMessage = "Invalid userName or password ";
				 NoticeFrameController noticeFrameController = new NoticeFrameController();
				 noticeFrameController.start(noticeMessage);
            }
        }
    }

    @FXML
    private void getGuestBtn(ActionEvent event) {
        //start the window with guest message
    }
}
