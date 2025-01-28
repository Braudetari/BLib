package common;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import common.User.UserType;

/**
 * Book Class For Database
 */
public class Book implements Serializable{
	private static final long serialVersionUID = 1L;
    private int id;
    private int serial_id;
    private String name;
    private String author;
    private String description;
    private String location;
    private String genre;

    ////////	CONSTRUCTORS	////////
    
    /**
     * Empty Constructor
     */
    public Book() {
    }
    
    /**
     * String Constructor
     * @param serial_id
     * @param name
     * @param author
     * @param description
     * @param genre
     * @param location
     */
    public Book(int serial_id, String name, String author, String description, String genre, String location) {
        this.serial_id = serial_id;
    	this.name = name;
        this.author = author;
        this.description = description;
        this.genre = genre;
        this.location = location;
    }

    /**
     * Book constructor
     * @param book
     */
    public Book(Book book) {
        this.id = book.id;
        this.serial_id = book.serial_id;
        this.name = book.name;
        this.author = book.author;
        this.description = book.description;
        this.genre = book.genre;
        this.location = book.location;
    }

    ////////	GETTERS AND SETTERS	/////////

    /**
     * Get Book ID
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * Set Book ID
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get Book Serial ID
     * @return int
     */
    public int getSerial_id() {
        return serial_id;
    }

    /**
     * Set Book Serial ID
     * @param serial_id
     */
    public void setSerial_id(int serial_id) {
        this.serial_id = serial_id;
    }

    /**
     * Get Book Name
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Set Book Name
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get Book Author
     * @return
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Set Book Author
     * @param author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Get Book Description
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set Book Description
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Get Book Genre
     * @return genre
     */
    public String getGenre() {
        return genre;
    }

    /**
     * Set Book Genre
     * @param genre
     */
    public void setGenre(String genre) {
        this.genre = genre;
    }
    
    /**
     * Get Book Location
     * @return Location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Set Book Location
     * @param location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    public String toString() {
    	Object[] list = {this.serial_id, this.name, this.author, this.description, this.genre, this.location};
    	for(int i=0;i<list.length;i++) {
    		if(list[i] == null) { //if its null its probably string
    			list[i] = " ";
    		}
    		else {
    			if(list[i].getClass().equals(String.class)) {
    				if(((String)list[i]).isEmpty()) {
    					list[i] = " ";
    				}
    			}
    		}
    	};
    	name = (String)list[1];
    	author = (String)list[2];
    	description = (String)list[3];
    	genre = (String)list[4];
    	location = (String)list[5];
    	return new String("[" +
    			this.id+","+
    			this.serial_id+","+
    			this.name+","+
    			this.author+","+
    			this.description.replace(",", "~")+","+
    			this.genre+","+
    			this.location
    			+"]");
    }
    
    public static Book fromString(String str) {
    	str = str.substring(1, str.length()-1); //remove toString []
    	StringTokenizer tokenizer = new StringTokenizer(str, ",");
    	int id;
    	int serial_id;
    	String name;
    	String author;
    	String description;
    	String genre;
    	String location;
    	try{
    		id = Integer.parseInt(tokenizer.nextToken());
    		serial_id = Integer.parseInt(tokenizer.nextToken());
    		name = tokenizer.nextToken();
    		author = tokenizer.nextToken();
    		description = tokenizer.nextToken();
    		description.replace("~", ",");
    		genre = tokenizer.nextToken();
    		location = tokenizer.nextToken();
    	}
        catch(NoSuchElementException e) {
        	   return null;
        }
        Book book = new Book(serial_id, name, author, description, genre, location);
        book.setId(id);
        return book;
    }
    
    /**
     * Convert List<Book> to String
     * @param books
     * @return
     */
    public static String bookListToString(List<Book> books) {
    	String output = "{";
    	Iterator<Book> iterator = books.iterator();
    	while(iterator.hasNext()) {
    		output += iterator.next();
    		if(iterator.hasNext()) {
    			output += ";";
    		}
    	}
    	output += "}";
    	return output;
    }
    
    public static List<Book> bookListFromString(String str){
    	Book book;
    	List<Book> books = new ArrayList<Book>();
    	str = str.substring(1, str.length()-1); //remove {}
    	if(str.equals(""))
    		return null;
    	StringTokenizer tokenizer = new StringTokenizer(str, ";");
    	if(!tokenizer.hasMoreTokens()) {
    		book = Book.fromString(str);
    		if(book == null) {
    			return null;
    		}
    		books.add(book);
    	}
    	else {
        	while(tokenizer.hasMoreTokens()) {
        		books.add(Book.fromString(tokenizer.nextToken().trim()));
        	}
    	}
    	return books;
    }
}
