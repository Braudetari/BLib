package common;

import java.sql.Date;

/**
 * Represents a borrowed book record, including borrowed and return dates
 * and references to the borrowed Book.
 */
public class BorrowedBook {
    private Book borrowedBook;
    private Subscriber borrowingSubscriber;
    private Date borrowed_date;
    private Date return_date;

    
    /////////	CONSTRUCTORS	/////////
    
    /**
     * Creates a BorrowedBook object with default values (no-argument constructor).
     */
    public BorrowedBook() {
        // Fields remain at default values
    }

    /**
     * Creates a BorrowedBook object with specified values.
     *
     * @param borrowedId   the unique borrowed record ID
     * @param borrowedBook the Book that was borrowed
     * @param borrowedDate the date the book was borrowed
     * @param returnDate   the date the book was returned or is due
     */
    public BorrowedBook(Book borrowedBook, Subscriber borrowingSubscriber, Date borrowedDate, Date returnDate) {
        this.borrowedBook = borrowedBook;
        this.borrowingSubscriber = borrowingSubscriber;
        this.borrowed_date = borrowedDate;
        this.return_date = returnDate;
    }

    /**
     * Creates a BorrowedBook object by copying an existing BorrowedBook instance.
     *
     * @param other the BorrowedBook to copy
     */
    public BorrowedBook(BorrowedBook bb) {
        this.borrowedBook = bb.borrowedBook;
        this.borrowedBook = bb.borrowedBook;
        this.borrowed_date = bb.borrowed_date;
        this.return_date = bb.return_date;
    }

    /////////	SETTERS AND GETTERS	/////////
    /**
     * Gets the borrowed record ID.
     *
     * @return the borrowed record ID
     */
    public Subscriber getBorrowingSubscriber() {
        return borrowingSubscriber;
    }

    /**
     * Sets the borrowed record ID.
     *
     * @param borrowedId the borrowed record ID to set
     */
    public void setBorrowedId(Subscriber borrowingSubscriber) {
        this.borrowingSubscriber = borrowingSubscriber;
    }

    /**
     * Gets the borrowed Book object.
     *
     * @return the borrowed Book
     */
    public Book getBorrowedBook() {
        return borrowedBook;
    }

    /**
     * Sets the borrowed Book object.
     *
     * @param borrowedBook the Book to set as borrowed
     */
    public void setBorrowedBook(Book borrowedBook) {
        this.borrowedBook = borrowedBook;
    }

    /**
     * Gets the date on which the book was borrowed.
     *
     * @return the borrowed date
     */
    public Date getBorrowedDate() {
        return borrowed_date;
    }

    /**
     * Sets the date on which the book was borrowed.
     *
     * @param borrowedDate the borrowed date to set
     */
    public void setBorrowedDate(Date borrowedDate) {
        this.borrowed_date = borrowedDate;
    }

    /**
     * Gets the date on which the book was returned or is due.
     *
     * @return the return date
     */
    public Date getReturnDate() {
        return return_date;
    }

    /**
     * Sets the date on which the book was returned or is due.
     *
     * @param returnDate the return date to set
     */
    public void setReturnDate(Date returnDate) {
        this.return_date = returnDate;
    }
}
