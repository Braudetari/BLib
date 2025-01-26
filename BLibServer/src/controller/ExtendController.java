package controller;

import java.sql.Connection;

public class ExtendController {
	
	/**
	 * Checks if a Book is Extendable
	 * @param connection
	 * @param bookId
	 * @return -1=fail, 0=nope, 1=yep
	 */
	public static int IsBookExtendable(Connection connection, int bookId){
		//Check if your book is not ordered
		//Extend by A certain time if yes
		return 0;
	}
}
