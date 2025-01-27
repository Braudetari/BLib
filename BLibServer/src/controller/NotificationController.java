package controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import common.*;
public class NotificationController {
	
	/**
	 * Remind every subscriber a day before borrow that their return date is due
	 * @param connection
	 */
	public static void BorrowReminderDayBefore(Connection connection) {
		if(connection == null) {
			System.err.println("Cant connect to database");
			return;
		}
		try {
			LocalDate today = LocalDate.now();
			List<BorrowedBook> borrowedBooks =  LendController.GetAllBorrowedBooks(connection);
			for(BorrowedBook bb : borrowedBooks) {
				//If its a day before
				if(bb != null && today.isEqual(bb.getReturnDate().minusDays(1))) {
					Subscriber subscriber = bb.getBorrowingSubscriber();
					String notificationMessage = "Book " + bb.getBorrowedBook().getName() +" Return Date is DUE tomorrow (" + DateUtil.DateToString(today.plusDays(1))+ ")";
					Notification n = new Notification(subscriber, LocalDate.now(), notificationMessage);
					Notify(connection, n);
					SendSmsNotification(n.getDescription(), n.getSubscriber());
					SendEmailNotification(n.getDescription(), n.getSubscriber());
					
				}
			}
		}
		catch(Exception e) {
			System.err.println("Couldn't remind Subscribers to return the book a day before");
		}
	}
	
	/**
	 * Check All DetailedHistory Freezes and unfreeze if frozen more than a month ago
	 * @param connection
	 */
	public static void UnfreezeAfterAMonth(Connection connection) {
		if(connection == null) {
			System.err.println("Cant connect to database");
			return;
		}
		try {
			//for each frozen subscriber
			List<Subscriber> subscriberList = SubscriberController.getAllSubscribers(connection);
			for(Subscriber subscriber : subscriberList) {
				List<DetailedHistory> dhList = null;
				if(subscriber.getDetailedSubscriptionHistory() != 0) {
					dhList = DetailedHistoryController.GetHistoryListFromDatabase(connection, subscriber.getDetailedSubscriptionHistory());	
				}
				if(dhList != null) {
					if(subscriber.isFrozen()) {
						//for each frozen subscriber, go reverse order and check if a month past since the last FREEZE action
						LocalDate dateNow = LocalDate.now();
						LocalDate maximumDate = null;
						//get first instance of FREEZE
						for(int i=dhList.size()-1; i>=0; i--) {
							if(dhList.get(i).getAction().equals(DetailedHistory.ActionType.FREEZE)) {
								//If its been 30 days
								if(dhList.get(i).getDate().plusDays(30).isBefore(dateNow)){
									SubscriberController.SetFreezeSubscriber(connection, subscriber.getSubscriberId(), false);
									Notification n = new Notification(subscriber, dateNow, "Account unfrozen, 30 days have passed.");
									Notify(connection, n);
									SendSmsNotification(n.getDescription(), n.getSubscriber());
									SendEmailNotification(n.getDescription(), n.getSubscriber());
								}
								break;
							}
						}
					}
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			System.err.println("Could not unfreeze frozen accounts after 30 days");
		}
	}
	
	/**
	 * Get Notifications for Subscriber from Database
	 * @param connection
	 * @param subscriberId
	 * @return List<Notification>
	 */
	public static List<Notification> GetNotificationsForSubscriber(Connection connection, int subscriberId){
		if(connection == null) {
			System.err.println("Could not connect to Database");
			return null;
		}
		if(subscriberId <= 0) {
			System.err.println("Valid subscriberId not provided");
		}
		Subscriber subscriber = SubscriberController.getSubscriberById(connection, subscriberId);
		if(subscriber.getNotificationHistory() == 0) {
			System.out.println("Subscriber doesn't have notification history");
		}
		return GetNotificationsFromDatabase(connection, subscriber.getNotificationHistory());
	}
	
	/**
	 * Get Notification list from database using notification_id
	 * @param connection
	 * @param notificationId
	 * @return List<Notification>
	 */
	public static List<Notification> GetNotificationsFromDatabase(Connection connection, int notificationId){
		if(connection == null) {
			System.err.println("Could not connect to Database");
			return null;
		}
		if(notificationId <= 0) {
			return null;
		}
		
		try {
			String query = "SELECT notification_blob FROM notification WHERE notification_id = ?";
			PreparedStatement pstmt = connection.prepareStatement(query);
			pstmt.setInt(1, notificationId);
			ResultSet rs = pstmt.executeQuery();
			if(!rs.next()) {
				System.out.println("There is no such notification_id in Database: " + notificationId);
				return null;
			}
			byte[] blobBytes = rs.getBytes("notification_blob");
			if(blobBytes == null) {
				System.err.println("Could not get notification_blob from database");
				return null;
			}
			try{
				ByteArrayInputStream bis = new ByteArrayInputStream(blobBytes);
				ObjectInputStream ois = new ObjectInputStream(bis);
				@SuppressWarnings("unchecked")
				List<Notification> notificationList = (List<Notification>)ois.readObject();
				return notificationList;
			}
			catch(Exception e) {
				e.printStackTrace();
				System.err.println("Could not Serialize Bytes back into List<Notification>");
				return null;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			System.err.println("Could not get notification List from Database");
			return null;
		}
	}
	
	
	/**
	 * Update Notification Blob in Database, if notification_id = 0 then create one and return it
	 * @param connection
	 * @param nList
	 * @return -1=error, 0=fail, >0=notification_id (success)
	 */
	public static int UpdateNotificationInDatabase(Connection connection, List<Notification> nList, int notificationId) {
		if(connection == null) {
			System.err.println("Could not connect to Database");
			return -1;
		}
		if(nList == null) {
			System.err.println("No Notification List was provided");
			return -1;
		}
		//Serialize List into Bytes (our Blob)
		byte[] blobBytes = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(nList);
			oos.flush();
			blobBytes = bos.toByteArray();
		}
		catch(Exception e) {
			e.printStackTrace();
			System.err.println("Could not serialize Notification List.");
			return -1;
		}
		
		//Store (or Create) notification List as Blob in Database
		try {
			PreparedStatement pstmt;
			String query;
			if(notificationId == 0) {
				query = "INSERT INTO notification(notification_blob) VALUES (?)";
				pstmt = connection.prepareStatement(query);
			}
			else {
				query = "UPDATE notification SET notification_blob = ? WHERE notification_id = ?";
				pstmt = connection.prepareStatement(query);
				pstmt.setInt(2, notificationId);
			}
			pstmt.setBytes(1, blobBytes);
			int success = pstmt.executeUpdate();
			if(success==0) { //couldnt update
				System.err.println("Failed to Insert Notifications into Database");
				return 0;
			}
			//get notificationId and return it
			if(notificationId==0) {
				ResultSet st = pstmt.getGeneratedKeys();
				return st.getInt(1);
			}
			return notificationId;
		}
		catch(Exception e) {
			e.printStackTrace();
			System.err.println("Could not update Notifications List in database");
			return -1;
		}
	}
	
	/**
	 * Create Notification Blob in Database, simpler reuse for Update
	 * @param connection
	 * @param nList
	 * @return
	 */
	public static int CreateNotificationsInDatabase(Connection connection, List<Notification> nList) {
		int result = UpdateNotificationInDatabase(connection, nList, 0);
		return result;
	}
	

	/**
	 * Updates subscriber's notification id 
	 * @param connection
	 * @param userId
	 * @return -1=error, 0=fail, 1=success
	 */
	public static int UpdateNotificationIdForSubscriberId(Connection connection, int subscriberId, int notificationId) {
		if(connection == null) {
			System.err.println("Could not connect to Database");
			return -1;
		}
		if(subscriberId<=0) {
			System.out.println("No valid userId was provided for notificationId update");
			return 0;
		}
		try {
			//GET notification_id from USER (either subscriber or librarian)
			PreparedStatement pstmt;
			pstmt = connection.prepareStatement("UPDATE subscriber SET notification_history = ? WHERE subscriber_id = ?");
			pstmt.setInt(1, notificationId);
			pstmt.setInt(2, subscriberId);
			int result = pstmt.executeUpdate();
			if(result<=0){
				System.out.println("User notification id update failed for userId" + subscriberId);
				return 0;
			}
			return 1;
		}
		catch(Exception e){
			e.printStackTrace();
			System.err.println("Could not update notification id for User " + subscriberId);
			return -1;
		}
	}
	
	/**
	 * Get Notification_history id for subscriber from database
	 * @param connection
	 * @param subscriberId
	 * @return int notification_history id
	 */
	public static int GetNotificationIdForSubscriber(Connection connection, int subscriberId) {
		if(connection == null) {
			System.err.println("Could not connect to Database");
			return -1;
		}
		if(subscriberId<=0) {
			System.out.println("No valid subscriberId was provided for notificationId retrieval");
			return 0;
		}
		try {
			//GET notification_history from subscriber
			PreparedStatement pstmt;
			pstmt = connection.prepareStatement("SELECT notification_history FROM subscriber WHERE subscriber_id = ?");
			pstmt.setInt(1, subscriberId);
			ResultSet rs = pstmt.executeQuery();
			if(!rs.next()){
				System.out.println("Subscriber notification id retrieval failed");
				return 0;
			}
			return rs.getInt(1);
		}
		catch(Exception e){
			e.printStackTrace();
			System.err.println("Could not update notification_id for User");
			return -1;
		}
	}
	
	/**
	 * Records Given Notification into Database
	 * @param connection
	 * @param dh Notification
	 * @return -1=error, 0=fail, 1=success
	 */
	public static int Notify(Connection connection, Notification n) {
		if(connection == null) {
			System.err.println("Could not connect to Database");
			return -1;
		}
		if(n == null) {
			System.err.println("No Notification was provided");
			return -1;
		}
		
		try {
			//Get Notification List for relevant Notification
			int subscriberId = n.getSubscriber().getSubscriberId();
			Subscriber subscriber = SubscriberController.getSubscriberById(connection, subscriberId);
			int result;
			List<Notification> subscriberNotifications = GetNotificationsFromDatabase(connection, subscriber.getNotificationHistory());
			if(subscriberNotifications == null) { //doesnt exist, create one
				subscriberNotifications = new ArrayList<Notification>();
				int resultId = CreateNotificationsInDatabase(connection, subscriberNotifications);
				if(resultId <=0) {
					return resultId;
				}
				//update new notificationId for subscriber
				result = UpdateNotificationIdForSubscriberId(connection, subscriberId, resultId);
			}
			//Update subscriber notification
			subscriberNotifications.add(n);
			int notificationId = GetNotificationIdForSubscriber(connection, subscriberId);
			int resultSubscriber = UpdateNotificationInDatabase(connection, subscriberNotifications,  notificationId);
			if(resultSubscriber<=0) {
				System.out.println("Could not update notification for subscriber");
				return 0;
			}
			return 1;
		}
		catch(Exception e) {
			e.printStackTrace();
			System.err.println("Could not add notification.");
			return -1;
		}
	}
	
	/**
	 * Sends an SMS notification
	 * @param message
	 * @param subscriber
	 */
	public static void SendSmsNotification(String str, Subscriber subscriber) {
		//stub for now unless someone is willing to give me a sim card
		System.out.println("Sent SMS to: " + subscriber.getSubscriberPhoneNumber() + " with message: " + str);
	}
	
	/**
	 * Sends an Email notification
	 * @param str
	 */
	public static void SendEmailNotification(String str, Subscriber subscriber) {
		//stub for now unless someone is willing to give me a sim card
		System.out.println("Sent Email to: " + subscriber.getSubscriberEmail() + " with message: " + str);
	}
	
}
