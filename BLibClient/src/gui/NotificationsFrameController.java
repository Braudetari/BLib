package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

public class NotificationsFrameController {

    @FXML
    private ListView<String> notificationsListView;

    @FXML
    private Button btnClose;

    // Simulated list of notifications (you can replace this with actual data from your system)
    private ObservableList<String> notifications = FXCollections.observableArrayList(
        "Client John Doe borrowed 'Java Basics' on 2025-01-20.",
        "Client Jane Smith requested 'Advanced Java' on 2025-01-21.",
        "Client Alice Cooper returned 'Data Structures' on 2025-01-22.",
        "Client Bob Marley borrowed 'Machine Learning' on 2025-01-23."
    );

    // Method to initialize the ListView with the notifications
    public void initialize() {
        notificationsListView.setItems(notifications);
    }

    // Method to handle the Close button click event
    @FXML
    public void onCloseBtnClick(ActionEvent event) {
        // Close the window when the button is clicked
        ((Button) event.getSource()).getScene().getWindow().hide();
    }
}
