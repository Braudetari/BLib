package gui;
import java.util.ArrayList;
import java.util.List;

import client.ClientUI;
import common.Notification;
import common.User;
import common.User.UserType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

/**
 * Controller class for the Notifications Frame UI.
 * Manages the display and behavior of notifications in the ListView.
 */
public class NotificationsFrameController implements IController {

    /**
     * ListView for displaying notifications to the user.
     */
    @FXML
    private ListView<String> notificationsListView;

    /**
     * List to store Notification objects retrieved from the server.
     */
    private List<Notification> notificationsList = null;

    /**
     * List to store notification descriptions as strings.
     */
    private List<String> notificationsStringList = null;

    /**
     * ObservableList to manage and update the ListView items.
     */
    private ObservableList<String> notificationsData = null;

    /**
     * Initializes the notifications frame.
     * Sets up the ListView and loads the notifications.
     */
    public void initializeFrame() {
        notificationsData = FXCollections.observableArrayList();
        notificationsListView.setItems(notificationsData);
        refreshNotifications();
    }

    /**
     * Refreshes the notifications displayed in the ListView.
     * Retrieves notifications from the server and updates the ListView with the latest data.
     */
    public void refreshNotifications() {
        User user = ClientUI.chat.getClientUser();
        notificationsList = ClientUI.chat.requestServerForNotifications(user.getId());

        if (notificationsList == null) {
            notificationsList = new ArrayList<>();
        }

        if (notificationsStringList == null) {
            notificationsStringList = new ArrayList<>();
        }

        // Add notifications to the list in descending order by date
        for (int i = notificationsList.size() - 1; i >= 0; i--) {
            Notification n = notificationsList.get(i);
            notificationsStringList.add(n.getDate() + " " + n.getDescription());
        }

        // Update the ListView with notification strings
        notificationsData.clear();
        if (notificationsStringList != null) {
            notificationsData.addAll(notificationsStringList);
        }
    }

    /**
     * Sets the permission level for the current user.
     * 
     * @param type The user type that defines the permissions.
     */
    @Override
    public void setPermission(UserType type) {
        // TODO Auto-generated method stub
    }

    /**
     * Sets the main menu controller.
     * 
     * @param controller The main menu controller to be set.
     */
    @Override
    public void setMainController(MenuUIController controller) {
        // TODO Auto-generated method stub
    }

    /**
     * Sets an object in the controller.
     * 
     * @param object The object to be set.
     */
    @Override
    public void setObject(Object object) {
        // TODO Auto-generated method stub
    }

    /**
     * Initializes the frame with the given object.
     * 
     * @param object The object used for initialization.
     */
    @Override
    public void initializeFrame(Object object) {
        // TODO Auto-generated method stub
    }
}
