package common;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * Notification represents the detailed history record for an action.
 */
public class Notification implements Serializable {

	private static final long serialVersionUID = 1L;

	
    private String description; //system note
    private LocalDate date;
    private Subscriber subscriber;
     
    
    ////////	CONSTUCTORS		////////
    
    public Notification() {	
    }
    
    /**
     * Constructs a Notification with the specified description, date, and user.
     * 
     * @param subscriber relevant subscriber
     * @param date of action
     * @param description the description of the notification record
     */
    public Notification(Subscriber subscriber, LocalDate date, String description) {
        this.description = description;
        this.date = date;
        this.subscriber = subscriber;
    }
    

    
    /**
     * Construct Notification using another Notification
     * @param n Notification object
     */
    public Notification(Notification n) {
    	this.description = n.description;
    	this.date = n.date;
    	this.subscriber = n.subscriber;
    }
    
    ////////	GETTERS AND SETTERS		////////
    
    /**
     * Returns the description of the notification
     *  @return String the description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Sets the description of the notification.
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Returns the date of the notification.
     * @return LocalDate
     */
    public LocalDate getDate() {
        return date;
    }
    
    /**
     * Sets the date of the notification record.
     * @param date the date to set
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    /**
     * Returns the subscriber associated with the notification record.
     * @return Subscriber
     */
    public Subscriber getSubscriber() {
        return subscriber;
    }
    
    /**
     * Sets the subscriber associated with the notification record.
     * @param subscriber the subscriber to set
     */
    public void setSubscriber(Subscriber subscriber) {
        this.subscriber = subscriber;
    }

    /**
     * Get Notification From String
     * @param str String
     * @return Notification
     */
    public static Notification fromString(String str) {
		str = str.substring(1, str.length()-1); //remove toString []
    	StringTokenizer tokenizer = new StringTokenizer(str, "%"); 
    	Subscriber subscriber;
    	LocalDate date;
    	String description;
    	try{
    		subscriber = Subscriber.subscriberFromString(tokenizer.nextToken().trim());
    		date = DateUtil.DateFromString(tokenizer.nextToken());
            description = tokenizer.nextToken();
    	}
        catch(NoSuchElementException e) {
        	   return null;
        }
        Notification n = new Notification(subscriber, date, description);
        return n;
	}
    
    /**
     * Get String of a Notification list
     * @param notificationList	Notification List
     * @return String
     */
    public static String NotificationListToString(List<Notification> notificationList) {
    	String output = "{";
    	Iterator<Notification> iterator = notificationList.iterator();
    	while(iterator.hasNext()) {
    		output += iterator.next();
    		if(iterator.hasNext()) {
    			output += ";";
    		}
    	}
    	output += "}";
    	return output;
    }
    
    /**
     * Get Notification list from string
     * @param str of NotificationList
     * @return List{@code<Notification>}
     */
    public static List<Notification> NotificationListFromString(String str){
		Notification n;
    	List<Notification> notificationList = new ArrayList<Notification>();
    	str = str.substring(1, str.length()-1); //remove {}
    	if(str.equals(""))
    		return null;
    	StringTokenizer tokenizer = new StringTokenizer(str, ";");
    	if(!tokenizer.hasMoreTokens()) {
    		n = fromString(str);
    		if(n == null) {
    			return null;
    		}
    		notificationList.add(n);
    	}
    	else {
        	while(tokenizer.hasMoreTokens()) {
        		notificationList.add(fromString(tokenizer.nextToken()));
        	}
    	}
    	return notificationList;
    }
    
}
