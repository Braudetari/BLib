package common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * Represents a library subscriber with related subscription details,
 * contact information, and status (active/frozen).
 */
public class Subscriber implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int subscriberId;
    private String subscriberName;
    private int detailedSubscriptionHistory;
    private int notificationHistory;
    private String subscriberPhoneNumber;
    private String subscriberEmail;
    private int subscriberFrozen;

    /**
     * Default constructor that initializes subscriberId to -1
     * and leaves other fields unset.
     */
    public Subscriber() {
        this.subscriberId = -1;
    }
    
    /**
     * Copy constructor. Creates a new Subscriber
     * by copying the properties of the given Subscriber.
     *
     * @param subscriber the subscriber to copy
     */
    public Subscriber(Subscriber subscriber) {
        this.subscriberId = subscriber.subscriberId;
        this.subscriberName = subscriber.subscriberName;
        this.detailedSubscriptionHistory = subscriber.detailedSubscriptionHistory;
        this.notificationHistory = subscriber.notificationHistory;
        this.subscriberPhoneNumber = subscriber.subscriberPhoneNumber;
        this.subscriberEmail = subscriber.subscriberEmail;
        this.subscriberFrozen = subscriber.subscriberFrozen;
    }
    
    /**
     * Constructs a Subscriber with the given details.
     *
     * @param subscriberId the subscriber's unique ID
     * @param subscriberName the subscriber's name
     * @param detailedSubscriptionHistory the subscriber's subscription history count
     * @param notificationHistory the subscriber's notification history count
     * @param subscriberPhoneNumber the subscriber's phone number
     * @param subscriberEmail the subscriber's email address
     * @param frozen the frozen status (1 for frozen, 0 for active)
     */
    public Subscriber(int subscriberId, String subscriberName, int detailedSubscriptionHistory,
                      int notificationHistory, String subscriberPhoneNumber,
                      String subscriberEmail, int frozen) {
        this.subscriberId = subscriberId;
        this.subscriberName = subscriberName;
        this.detailedSubscriptionHistory = detailedSubscriptionHistory;
        this.notificationHistory = notificationHistory;
        this.subscriberPhoneNumber = subscriberPhoneNumber;
        this.subscriberEmail = subscriberEmail;
        this.subscriberFrozen = frozen;
    }

    /**
     * Returns the subscriber's unique ID.
     * 
     * @return the subscriber ID
     */
    public int getSubscriberId() {
        return subscriberId;
    }

    /**
     * Sets the subscriber's unique ID.
     * 
     * @param subscriberId the ID to set
     */
    public void setSubscriberId(int subscriberId) {
        this.subscriberId = subscriberId;
    }

    /**
     * Returns the subscriber's name.
     * 
     * @return the subscriber name
     */
    public String getSubscriberName() {
        return subscriberName;
    }

    /**
     * Sets the subscriber's name.
     * 
     * @param subscriberName the name to set
     */
    public void setSubscriberName(String subscriberName) {
        this.subscriberName = subscriberName;
    }

    /**
     * Returns the detailed subscription history count.
     * 
     * @return the detailedSubscriptionHistory
     */
    public int getDetailedSubscriptionHistory() {
        return detailedSubscriptionHistory;
    }

    /**
     * Sets the detailed subscription history count.
     * 
     * @param detailedSubscriptionHistory the history count to set
     */
    public void setDetailedSubscriptionHistory(int detailedSubscriptionHistory) {
        this.detailedSubscriptionHistory = detailedSubscriptionHistory;
    }

    /**
     * Returns the notification history count.
     * 
     * @return the notification history count
     */
    public int getNotificationHistory() {
        return notificationHistory;
    }

    /**
     * Sets the notification history count.
     * 
     * @param notificationHistory the notification history count to set
     */
    public void setNotificationHistory(int notificationHistory) {
        this.notificationHistory = notificationHistory;
    }

    /**
     * Returns the subscriber's phone number.
     * 
     * @return the phone number
     */
    public String getSubscriberPhoneNumber() {
        return subscriberPhoneNumber;
    }

    /**
     * Sets the subscriber's phone number.
     * 
     * @param subscriberPhoneNumber the phone number to set
     */
    public void setSubscriberPhoneNumber(String subscriberPhoneNumber) {
        this.subscriberPhoneNumber = subscriberPhoneNumber;
    }

    /**
     * Returns the subscriber's email address.
     * 
     * @return the email
     */
    public String getSubscriberEmail() {
        return subscriberEmail;
    }

    /**
     * Sets the subscriber's email address.
     * 
     * @param subscriberEmail the email to set
     */
    public void setSubscriberEmail(String subscriberEmail) {
        this.subscriberEmail = subscriberEmail;
    }

    /**
     * Indicates whether the subscriber is frozen (unable to borrow/reserve).
     * 
     * @return true if frozen, false otherwise
     */
    public boolean isFrozen() {
        return (this.subscriberFrozen > 0);
    }

    /**
     * Sets the frozen status of the subscriber.
     * 
     * @param frozen true to freeze the subscriber, false to unfreeze
     */
    public void setFrozen(boolean frozen) {
        this.subscriberFrozen = (frozen) ? 1 : 0;
    }

    /**
     * Returns a string representation of the Subscriber in the format:
     * <pre>[subscriberId, subscriberName, detailedSubscriptionHistory, notificationHistory, subscriberPhoneNumber, subscriberEmail, subscriberFrozen]</pre>
     */
    @Override
    public String toString() {
        if (this.subscriberId == -1) {
            return "[]";
        }
        return "[" + subscriberId + "," 
                   + subscriberName + "," 
                   + detailedSubscriptionHistory + "," 
                   + notificationHistory + "," 
                   + subscriberPhoneNumber + "," 
                   + subscriberEmail + "," 
                   + subscriberFrozen + "]";
    }

    /**
     * Creates a Subscriber object from its string representation in the format:
     * <pre>[subscriberId, subscriberName, detailedSubscriptionHistory, notificationHistory, subscriberPhoneNumber, subscriberEmail, subscriberFrozen]</pre>
     * 
     * @param str the string to parse
     * @return a Subscriber object or null if parsing fails
     */
    public static Subscriber subscriberFromString(String str) {
        str = str.substring(1, str.length() - 1); // remove toString []
        StringTokenizer tokenizer = new StringTokenizer(str, ",");
        int subscriberId;
        String subscriberName;
        int detailedSubscriptionHistory;
        int notificationHistory;
        String subscriberPhoneNumber;
        String subscriberEmail;
        int subscriberFrozen;
        try {
            subscriberId = Integer.parseInt(tokenizer.nextToken().trim());
            subscriberName = tokenizer.nextToken().trim();
            detailedSubscriptionHistory = Integer.parseInt(tokenizer.nextToken().trim());
            notificationHistory = Integer.parseInt(tokenizer.nextToken().trim());
        } catch (NoSuchElementException e) {
            return null;
        }
        try {
            subscriberPhoneNumber = tokenizer.nextToken().trim();
        } catch (NoSuchElementException e) {
            subscriberPhoneNumber = "";
        }
        try {
            subscriberEmail = tokenizer.nextToken().trim();
        } catch (NoSuchElementException e) {
            subscriberEmail = "";
        }
        try {
            subscriberFrozen = Integer.parseInt(tokenizer.nextToken().trim());
        } catch (NoSuchElementException e) {
            subscriberFrozen = 0;
        }
        Subscriber subscriber = new Subscriber(
            subscriberId,
            subscriberName,
            detailedSubscriptionHistory,
            notificationHistory,
            subscriberPhoneNumber,
            subscriberEmail,
            subscriberFrozen
        );
        return subscriber;
    }

    /**
     * Converts a list of Subscribers into a single string in the format:
     * <pre>{subscriber1;subscriber2;...}</pre>
     *
     * @param subscriberList the list of subscribers to convert
     * @return a string representation of the subscriber list
     */
    public static String subscriberListToString(List<Subscriber> subscriberList) {
        String output = "{";
        Iterator<Subscriber> iterator = subscriberList.iterator();
        while (iterator.hasNext()) {
            output += iterator.next();
            if (iterator.hasNext()) {
                output += ";";
            }
        }
        output += "}";
        return output;
    }

    /**
     * Parses a string in the format <pre>{subscriber1;subscriber2;...}</pre>
     * into a list of Subscriber objects. Returns null if the string is empty
     * or invalid.
     *
     * @param str the string to parse
     * @return a list of Subscribers or null if invalid
     */
    public static List<Subscriber> subscriberListFromString(String str) {
        List<Subscriber> subscriberList = new ArrayList<>();
        str = str.substring(1, str.length() - 1); // remove {}
        if (str.equals("")) {
            return null;
        }
        StringTokenizer tokenizer = new StringTokenizer(str, ";");
        if (!tokenizer.hasMoreTokens()) {
            Subscriber subscriber = subscriberFromString(str);
            if (subscriber == null) {
                return null;
            }
            subscriberList.add(subscriber);
        } else {
            while (tokenizer.hasMoreTokens()) {
                subscriberList.add(subscriberFromString(tokenizer.nextToken().trim()));
            }
        }
        return subscriberList;
    }
}