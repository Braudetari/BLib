package common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class Subscriber implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int subscriberId;
    private String subscriberName;
    private int detailedSubscriptionHistory;
    private int notificationHistory;
    private String subscriberPhoneNumber;
    private String subscriberEmail;
    private int subscriberFrozen;

    public Subscriber() {
    	this.subscriberId = -1;
    }
    
    public Subscriber(Subscriber subscriber) {
    	this.subscriberId = subscriber.subscriberId;
    	this.subscriberName = subscriber.subscriberName;
    	this.detailedSubscriptionHistory = subscriber.detailedSubscriptionHistory;
    	this.notificationHistory = subscriber.notificationHistory;
    	this.subscriberPhoneNumber = subscriber.subscriberPhoneNumber;
    	this.subscriberEmail = subscriber.subscriberEmail;
    	this.subscriberFrozen = subscriber.subscriberFrozen;
    }
    
    // Constructor
    public Subscriber(int subscriberId, String subscriberName, int detailedSubscriptionHistory, int notificationHistory, String subscriberPhoneNumber, String subscriberEmail, int frozen) {
        this.subscriberId = subscriberId;
        this.subscriberName = subscriberName;
        this.detailedSubscriptionHistory = detailedSubscriptionHistory;
        this.notificationHistory = notificationHistory;
        this.subscriberPhoneNumber = subscriberPhoneNumber;
        this.subscriberEmail = subscriberEmail;
        this.subscriberFrozen = frozen;
    }

    // Getter and setter for subscriberId
    public int getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(int subscriberId) {
        this.subscriberId = subscriberId;
    }

    // Getter and setter for subscriberName
    public String getSubscriberName() {
        return subscriberName;
    }

    public void setSubscriberName(String subscriberName) {
        this.subscriberName = subscriberName;
    }

    // Getter and setter for detailedSubscriptionHistory
    public int getDetailedSubscriptionHistory() {
        return detailedSubscriptionHistory;
    }

    public void setDetailedSubscriptionHistory(int detailedSubscriptionHistory) {
        this.detailedSubscriptionHistory = detailedSubscriptionHistory;
    }
    
 // Getter and setter for notificationHistory
    public int getNotificationHistory() {
        return notificationHistory;
    }

    public void setNotificationHistory(int notificationHistory) {
        this.notificationHistory = notificationHistory;
    }

    // Getter and setter for subscriberPhoneNumber
    public String getSubscriberPhoneNumber() {
        return subscriberPhoneNumber;
    }

    public void setSubscriberPhoneNumber(String subscriberPhoneNumber) {
        this.subscriberPhoneNumber = subscriberPhoneNumber;
    }

    // Getter and setter for subscriberEmail
    public String getSubscriberEmail() {
        return subscriberEmail;
    }

    public void setSubscriberEmail(String subscriberEmail) {
        this.subscriberEmail = subscriberEmail;
    }

    public boolean isFrozen() {
    	if(this.subscriberFrozen>0)
    		return true;
    	return false;
    }
    
    @Override
    public String toString() {
    	if(this.subscriberId == -1)
    		return new String("[]");
    	
        return new String("["+subscriberId+","
        					+subscriberName+","
        					+detailedSubscriptionHistory+","
        					+notificationHistory+","
        					+subscriberPhoneNumber+","
        					+subscriberEmail+","
        					+subscriberFrozen
        					+"]");
    }
    
    public static Subscriber subscriberFromString(String str) {
    	str = str.substring(1, str.length()-1); //remove toString []
    	StringTokenizer tokenizer = new StringTokenizer(str, ",");
    	int subscriberId;
    	String subscriberName;
    	int detailedSubscriptionHistory;
    	int notificationHistory;
    	String subscriberPhoneNumber;
    	String subscriberEmail;
    	int subscriberFrozen;
    	try{
    		subscriberId = Integer.parseInt(tokenizer.nextToken().trim());
    		subscriberName = tokenizer.nextToken().trim();
            detailedSubscriptionHistory = Integer.parseInt(tokenizer.nextToken().trim());
            notificationHistory = Integer.parseInt(tokenizer.nextToken().trim());
    	}
        catch(NoSuchElementException e) {
        	   return null;
        }
        try {
            subscriberPhoneNumber = tokenizer.nextToken().trim();
        }
        catch(NoSuchElementException e) {
        	subscriberPhoneNumber = "";
        }
        try {
            subscriberEmail = tokenizer.nextToken().trim();
        }
    	catch(NoSuchElementException e) {
    		subscriberEmail = "";
    	}
        try {
        	subscriberFrozen = Integer.parseInt(tokenizer.nextToken().trim());
        }
        catch(NoSuchElementException e) {
        	subscriberFrozen = 0;
        }
        Subscriber subscriber = new Subscriber(subscriberId, subscriberName, detailedSubscriptionHistory, notificationHistory, subscriberPhoneNumber, subscriberEmail, subscriberFrozen);
        return subscriber;
    }
    public static String subscriberListToString(List<Subscriber> subscriberList) {
    	String output = "{";
    	Iterator<Subscriber> iterator = subscriberList.iterator();
    	while(iterator.hasNext()) {
    		output += iterator.next();
    		if(iterator.hasNext()) {
    			output += ";";
    		}
    	}
    	output += "}";
    	return output;
    }
    
    public static List<Subscriber> subscriberListFromString(String str){
		Subscriber subscriber;
    	List<Subscriber> subscriberList = new ArrayList<Subscriber>();
    	str = str.substring(1, str.length()-1); //remove {}
    	if(str.equals(""))
    		return null;
    	StringTokenizer tokenizer = new StringTokenizer(str, ";");
    	if(!tokenizer.hasMoreTokens()) {
    		subscriber = subscriberFromString(str);
    		if(subscriber == null) {
    			return null;
    		}
    		subscriberList.add(subscriber);
    	}
    	else {
        	while(tokenizer.hasMoreTokens()) {
        		subscriberList.add(subscriberFromString(tokenizer.nextToken().trim()));
        	}
    	}
    	return subscriberList;
    }
}
