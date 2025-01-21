package common;

import java.sql.Date;

public class Book {
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
     */
    public Book(int serial_id, String name, String author, String description, String genre, String location) {
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
     * @return
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
     * @param Location
     */
    public void setLocation(String location) {
        this.location = location;
    }

}
