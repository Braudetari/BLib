package common;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;

/**
 * Represents a borrowed book record, including borrowed and return dates
 * and references to the borrowed Book.
 */
public class BorrowedBook implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Book borrowedBook;
    private Subscriber borrowingSubscriber;
    private LocalDate borrowed_date;
    private LocalDate return_date;

    
    /////////	CONSTRUCTORS	/////////
    
    /**
     * Creates a BorrowedBook object with default values (no-argument constructor).
     */
    public BorrowedBook() {
        // Fields remain at default values
    }

    /**
     * Create BorrowedBook with specified variables
     * @param borrowedBook
     * @param borrowingSubscriber
     * @param borrowedDate
     * @param returnDate
     */
    public BorrowedBook(Book borrowedBook, Subscriber borrowingSubscriber, LocalDate borrowedDate, LocalDate returnDate) {
        this.borrowedBook = borrowedBook;
        this.borrowingSubscriber = borrowingSubscriber;
        this.borrowed_date = borrowedDate;
        this.return_date = returnDate;
    }

    /**
     * Creates a BorrowedBook object by copying an existing BorrowedBook instance.
     *
     * @param bb BorrowedBook other the BorrowedBook to copy
     */
    public BorrowedBook(BorrowedBook bb) {
        this.borrowedBook = bb.borrowedBook;
        this.borrowingSubscriber = bb.borrowingSubscriber;
        this.borrowed_date = bb.borrowed_date;
        this.return_date = bb.return_date;
    }

    /////////	SETTERS AND GETTERS	/////////
    /**
     * Gets the borrowing Subscriber.
     *
     * @return Subscriber 
     */
    public Subscriber getBorrowingSubscriber() {
        return borrowingSubscriber;
    }

    /**
     * Set Borrowed Id
     * @param borrowingSubscriber
     */
    public void setBorrowedId(Subscriber borrowingSubscriber) {
        this.borrowingSubscriber = borrowingSubscriber;
    }

    /**
     * Gets the borrowed Book object.
     *
     * @return Book the borrowed Book
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
    public LocalDate getBorrowedDate() {
        return borrowed_date;
    }

    /**
     * Sets the date on which the book was borrowed.
     *
     * @param borrowedDate the borrowed date to set
     */
    public void setBorrowedDate(LocalDate borrowedDate) {
        this.borrowed_date = borrowedDate;
    }

    /**
     * Gets the date on which the book was returned or is due.
     *
     * @return the return date
     */
    public LocalDate getReturnDate() {
        return return_date;
    }

    /**
     * Sets the date on which the book was returned or is due.
     *
     * @param returnDate the return date to set
     */
    public void setReturnDate(LocalDate returnDate) {
        this.return_date = returnDate;
    }
}
