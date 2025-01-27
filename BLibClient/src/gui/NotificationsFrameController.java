package gui;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import client.ClientUI;
import common.Notification;
import common.User;
import common.User.UserType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

public class NotificationsFrameController implements IController{

    @FXML
    private ListView<String> notificationsListView;
    


    private List<Notification> notificationsList = null;
    private List<String> notificationsStringList = null;
    private ObservableList<String> notificationsData = null;

    // Method to initialize the ListView with the notifications
    public void initializeFrame() {
    	notificationsData = FXCollections.observableArrayList();
    	notificationsListView.setItems(notificationsData);
    	refreshNotifications();
    }

    public void refreshNotifications() {
    	User user = ClientUI.chat.getClientUser();
    	notificationsList = ClientUI.chat.requestServerForNotifications(user.getId());
    	if(notificationsList == null) {
    		notificationsList = new ArrayList<Notification>();
    	}
    	//Get strings of notification by date descending order (latest to first)
    	if(notificationsStringList == null)
    		notificationsStringList = new ArrayList<String>();
    	for(int i=notificationsList.size()-1; i>=0; i--) {
    		Notification n = notificationsList.get(i);
    		notificationsStringList.add(n.getDate()+" "+n.getDescription());
    	}
    	//Put Notifications Strings in Data ListView
    	notificationsData.clear();
    	if(notificationsStringList != null) {
        	notificationsData.addAll(notificationsStringList);
    	}
    }
    
	@Override
	public void setPermission(UserType type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMainController(MenuUIController controller) {
		// TODO Auto-generated method stub
		
	}


}
