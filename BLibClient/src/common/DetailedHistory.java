package common;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import common.User.UserType;
/**
 * DetailedHistory represents the detailed history record for an action.
 */
public class DetailedHistory implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
     * Recorded action type
     */
    public enum ActionType implements Serializable{
        BORROW(0), RETURN(1), RESERVE(2), FREEZE(3), UNFREEZE(4), EXTEND(5);
    	public static ActionType fromInt(int value) {
			for(ActionType type : ActionType.values()) {
				if(type.getValue() == value) {
					return type;
				}
			}
			return null;
		}
    	public static ActionType fromString(String value) {
			for(ActionType type : ActionType.values()) {
				if(type.toString().equals(value.toUpperCase())) {
					return type;
				}
			}
			return null;
		}
        private int value;
        public int getValue() {
            return this.value;
        }
        private ActionType(int value) {
            this.value = value;
        }
    }
    private String description; //system note
    private String note; //librarian note
    private LocalDate date;
    private User user;
    private ActionType action;
     
    
    ////////	CONSTUCTORS		////////
    
    public DetailedHistory() {	
    }
    
    /**
     * Constructs a DetailedHistory with the specified description, date, and user.
     * 
     * @param description the description of the history record
     * @param date of action
     * @param user affected/relevant
     */
    public DetailedHistory(User user, ActionType action, LocalDate date, String description, String note) {
        this.description = description;
        this.date = date;
        this.user = user;
        this.action = action;
        this.note = note;
    }
    
    public DetailedHistory(User user, ActionType action, LocalDate date, String description) {
        this.description = description;
        this.date = date;
        this.user = user;
        this.action = action;
        this.note = " ";
    }
    
    /**
     * Construct DetailedHistory using another DetailedHistory
     * @param detailedHistory object
     */
    public DetailedHistory(DetailedHistory history) {
    	this.action = history.action;
    	this.description = history.description;
    	this.date = history.date;
    	this.user = history.user;
    	this.note = history.note;
    }
    
    ////////	GETTERS AND SETTERS		////////
    
    /**
     * Returns the description of the history record.
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Sets the description of the history record.
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Returns the date of the history record.
     * @return the date
     */
    public LocalDate getDate() {
        return date;
    }
    
    /**
     * Sets the date of the history record.
     * @param date the date to set
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    /**
     * Returns the user associated with the history record.
     * @return the user
     */
    public User getUser() {
        return user;
    }
    
    /**
     * Sets the user associated with the history record.
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Returns the action of this history
     * @return ActionType
     */
    public ActionType getAction() {
    	return this.action;
    }
    
    /**
     * Set the action of this history
     */
    public void setAction(ActionType action) {
    	this.action = action;
    }
    
    /**
     * Get Note
     * @return String
     */
    public String getNote() {
    	return this.note;
    }
    
    /**
     * Set Note
     * @param note String
     */
    public void setNote(String note) {
    	this.note = note;
    }

    public String toString() {
    	return new String("["+
    				user+"%"+ 
    				action.toString()+"%"+
    				DateUtil.DateToString(date)+"%"+
    				description+"%"+
    				note
    			+"]");
    }
    
    public static DetailedHistory fromString(String str) {
		str = str.substring(1, str.length()-1); //remove toString []
    	StringTokenizer tokenizer = new StringTokenizer(str, "%"); 
    	User user;
    	ActionType action;
    	LocalDate date;
    	String description;
    	String note;
    	try{
    		user = User.fromString(tokenizer.nextToken());
    		action = ActionType.fromString(tokenizer.nextToken());
    		date = DateUtil.DateFromString(tokenizer.nextToken());
            description = tokenizer.nextToken();
            note = tokenizer.nextToken();
    	}
        catch(NoSuchElementException e) {
        	   return null;
        }
        DetailedHistory dh = new DetailedHistory(user, action, date, description, note);
        return dh;
	}
    
    /**
     * Get String of a DetailedHistory list
     * @param DetailedHistoryList
     * @return String
     */
    public static String detailedHistoryListToString(List<DetailedHistory> detailedList) {
    	String output = "{";
    	Iterator<DetailedHistory> iterator = detailedList.iterator();
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
     * Get DetailedHistory list from string
     * @param str of DetailedHistoryList
     * @return List<DetailedHistory>
     */
    public static List<DetailedHistory> detailedHistoryListFromString(String str){
		DetailedHistory dh;
    	List<DetailedHistory> detailedList = new ArrayList<DetailedHistory>();
    	str = str.substring(1, str.length()-1); //remove {}
    	if(str.equals(""))
    		return null;
    	StringTokenizer tokenizer = new StringTokenizer(str, ";");
    	if(!tokenizer.hasMoreTokens()) {
    		dh = fromString(str);
    		if(dh == null) {
    			return null;
    		}
    		detailedList.add(dh);
    	}
    	else {
        	while(tokenizer.hasMoreTokens()) {
        		detailedList.add(fromString(tokenizer.nextToken()));
        	}
    	}
    	return detailedList;
    }
    
    //DEBUG main
    public static void main(String args[]) {
    	User user = new User(1, "Leolel", "passwuwrd", User.UserType.LIBRARIAN);
    	LocalDate date = LocalDate.now();
    	ActionType action = ActionType.fromString("BORROW");
    	DetailedHistory a = new DetailedHistory(user, action, date, "Pretty boring action ngl", " ");
    	System.out.println(a);
    	DetailedHistory b = DetailedHistory.fromString(a.toString());
    	System.out.println(b);
    }
}
