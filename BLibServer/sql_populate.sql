-- Insert data into the "date" table
INSERT INTO date (day, month, year) VALUES (1, 1, 2025);   -- date_id assumed 1
INSERT INTO date (day, month, year) VALUES (15, 2, 2025);  -- date_id assumed 2
INSERT INTO date (day, month, year) VALUES (28, 2, 2025);  -- date_id assumed 3

-- Insert data into the "history" table (for notification and subscription histories)
INSERT INTO history (history_blob) VALUES (X'48656C6C6F2C20576F726C6421');  -- history_id assumed 1 ("Hello, World!" in hex)
INSERT INTO history (history_blob) VALUES (X'5375627363726970742068747970696E67');  -- history_id assumed 2
INSERT INTO history (history_blob) VALUES (X'4C696272617269616E207265636F7264');  -- history_id assumed 3

-- Insert data into the "user" table
-- The user_type must be either 'subscriber' or 'librarian'
INSERT INTO "user" (username, password, user_type) VALUES ('alice', 'alicepass', 'subscriber');  -- user_id assumed 1
INSERT INTO "user" (username, password, user_type) VALUES ('bob', 'bobpass', 'subscriber');      -- user_id assumed 2
INSERT INTO "user" (username, password, user_type) VALUES ('carol', 'carolpass', 'librarian');     -- user_id assumed 3

-- Insert data into the "subscriber" table for users of type 'subscriber'
-- Here, detailed_subscription_history references history_id and subscriber_id references user_id
INSERT INTO subscriber (subscriber_id, subscriber_name, detailed_subscription_history, subscriber_phone_number, subscriber_email, subscriber_frozen)
VALUES (1, 'Alice Subscriber', 2, '555-1234', 'alice@example.com', 0);

INSERT INTO subscriber (subscriber_id, subscriber_name, detailed_subscription_history, subscriber_phone_number, subscriber_email, subscriber_frozen)
VALUES (2, 'Bob Reader', 2, '555-2345', 'bob@example.com', 0);

-- Insert data into the "librarian" table for the librarian user
-- notification_history references history_id and librarian_id references user_id
INSERT INTO librarian (librarian_id, librarian_name, notification_history, librarian_email, librarian_phone_number)
VALUES (3, 'Carol Librarian', 1, 'carol@library.org', '555-3456');

-- Insert data into the "book" table
INSERT INTO book (book_serial_id, book_name, book_author, book_description)
VALUES ('BKS-0001', 'The Great Gatsby', 'F. Scott Fitzgerald', 'A classic novel of the Jazz Age.');

INSERT INTO book (book_serial_id, book_name, book_author, book_description)
VALUES ('BKS-0002', '1984', 'George Orwell', 'A dystopian novel about a totalitarian regime.');

INSERT INTO book (book_serial_id, book_name, book_author, book_description)
VALUES ('BKS-0003', 'To Kill a Mockingbird', 'Harper Lee', 'A novel centered on racial injustice.');

-- Insert data into the "borrowed_book" table.
-- Since "book_id" is the primary key in borrowed_book and is a foreign key referencing book(book_id),
-- ensure that the book exists. Also, subscriber_id, borrowed_date_id, and return_date_id must reference the corresponding tables.
--
-- For example, assume:
--   - Book with book_id = 1 (The Great Gatsby) is borrowed by subscriber with subscriber_id = 1.
--   - borrowed_date_id uses date_id = 1 and return_date_id uses date_id = 2.
INSERT INTO borrowed_book (book_id, subscriber_id, borrowed_date_id, return_date_id)
VALUES (1, 1, 1, 2);

-- Another borrowed book example:
--   - Book with book_id = 2 (1984) is borrowed by subscriber with subscriber_id = 2.
--   - borrowed_date_id uses date_id = 2 and return_date_id uses date_id = 3.
INSERT INTO borrowed_book (book_id, subscriber_id, borrowed_date_id, return_date_id)
VALUES (2, 2, 2, 3);