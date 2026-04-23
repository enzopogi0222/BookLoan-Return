-- Run once against: bookloan_and_return
-- Book columns match the Java app (ViewBook / AddBook).

-- Sample books (several copies on shelf)
INSERT INTO book (bookName, author, genre, published_year, stock)
VALUES
  ('Introduction to Java Programming', 'Y Daniel Liang', 'Computer Science', '2020', 5),
  ('Data Structures and Algorithms', 'Thomas H. Cormen', 'Computer Science', '2009', 3),
  ('Clean Code', 'Robert C. Martin', 'Software Engineering', '2008', 4),
  ('The Pragmatic Programmer', 'Andrew Hunt', 'Software Engineering', '1999', 3),
  ('Design Patterns', 'Erich Gamma', 'Software Engineering', '1994', 2),
  ('Database Systems', 'Abraham Silberschatz', 'Computer Science', '2019', 4);

-- Optional: store students in the database for your own lookups / future features.
-- The current loan screen uses borrower text; when testing loans, use this student name.
CREATE TABLE IF NOT EXISTS student (
  student_id INT AUTO_INCREMENT PRIMARY KEY,
  full_name VARCHAR(200) NOT NULL,
  student_code VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO student (full_name, student_code)
VALUES ('Floro Lorenzo Gagni', '2311600060');

-- When testing “Loan book” in the app, enter borrower: Maria Santos
-- (or any name — it is stored in loan.borrower_name).
