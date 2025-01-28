package common;

import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * Represents a user in the system, including an ID, username,
 * password, and user type.
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String username = null;
    private String password = null;
    private UserType type = null;

    /**
     * Enumeration of possible user types.
     */
    public static enum UserType {
        SUBSCRIBER(0), 
        LIBRARIAN(1), 
        GUEST(2);

        private final int value;

        /**
         * Constructor for the UserType enum.
         *
         * @param value the integer value associated with this user type
         */
        UserType(int value) {
            this.value = value;
        }

        /**
         * Returns the UserType corresponding to the specified integer value.
         *
         * @param value the integer value
         * @return the matching UserType, or null if not found
         */
        public static UserType fromInt(int value) {
            for (UserType type : UserType.values()) {
                if (type.getValue() == value) {
                    return type;
                }
            }
            return null;
        }

        /**
         * Returns the UserType corresponding to the specified string value.
         * This method compares the uppercase string representation.
         *
         * @param value the string value
         * @return the matching UserType, or null if not found
         */
        public static UserType fromString(String value) {
            for (UserType type : UserType.values()) {
                if (type.toString().equals(value.toUpperCase())) {
                    return type;
                }
            }
            return null;
        }

        /**
         * Returns the integer value associated with this user type.
         *
         * @return the integer value
         */
        public int getValue() {
            return value;
        }
    }

    //////////	Constructors	////////

    /**
     * Default constructor. Initializes the user as a GUEST with
     * no username or password.
     */
    public User() {
        this.username = null;
        this.password = null;
        this.type = UserType.GUEST;
    }

    /**
     * Constructs a User with specified ID, username, password, and type.
     *
     * @param id       the unique user ID
     * @param username the username
     * @param password the password
     * @param type     the user type
     */
    public User(int id, String username, String password, UserType type) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.type = type;
    }

    /**
     * Copy constructor. Creates a new User by copying data from an existing User.
     *
     * @param user the user to copy
     */
    public User(User user) {
        this.id = user.id;
        this.password = user.password;
        this.type = user.type;
        this.username = user.username;
    }

    //////////	SETTER/GETTER	////////

    /**
     * Returns the username of this user.
     *
     * @return the username, or null if none was set
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Sets the username of this user.
     *
     * @param username the new username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the password of this user.
     *
     * @return the password, or null if none was set
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Sets the password of this user.
     *
     * @param password the new password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns the user type of this user.
     *
     * @return the user type
     */
    public UserType getType() {
        return this.type;
    }

    /**
     * Sets the user type of this user.
     *
     * @param type the new user type
     */
    public void setType(UserType type) {
        this.type = type;
    }

    /**
     * Returns the unique ID of this user.
     *
     * @return the user ID
     */
    public int getId() {
        return this.id;
    }

    /**
     * Sets the unique ID of this user.
     *
     * @param id the new user ID
     */
    public void setId(int id) {
        this.id = id;
    }

    //////////	FUNCTIONS	////////

    /**
     * Returns a string representation of this User in the format:
     * <pre>[id,username,password,type]</pre>
     */
    @Override
    public String toString() {
        int id = this.id;
        String username = this.username;
        String password = this.password;
        int typeValue = (this.type != null) ? this.type.value : -1;
        
        if (this.username == null) {
            username = " ";
        }
        if (this.password == null) {
            password = " ";
        }

        return "[" + id + "," + username + "," + password + "," + typeValue + "]";
    }

    /**
     * Parses a string in the format <pre>[id,username,password,type]</pre>
     * and returns a corresponding User object. Returns null if parsing fails.
     *
     * @param str the string to parse
     * @return a User object or null if invalid
     */
    public static User fromString(String str) {
        // Remove surrounding brackets [ ]
        str = str.substring(1, str.length() - 1);
        StringTokenizer tokenizer = new StringTokenizer(str, ",");
        int id;
        String username;
        String password;
        int type;
        try {
            id = Integer.parseInt(tokenizer.nextToken().trim());
            username = tokenizer.nextToken().trim();
            password = tokenizer.nextToken().trim();
            type = Integer.parseInt(tokenizer.nextToken().trim());
        } catch (NoSuchElementException e) {
            return null;
        }
        User user = new User(id, username, password, UserType.fromInt(type));
        return user;
    }
}