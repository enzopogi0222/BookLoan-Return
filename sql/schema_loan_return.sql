-- Run against database: bookloan_and_return
-- Requires existing table: book (book_id, ... stock)

-- Checkout: one row per loan. No "returned" column here — closure is recorded in book_return.
CREATE TABLE IF NOT EXISTS loan (
  loan_id INT AUTO_INCREMENT PRIMARY KEY,
  book_id INT NOT NULL,
  borrower_name VARCHAR(200) NOT NULL,
  loan_date DATE NOT NULL,
  due_date DATE NOT NULL,
  CONSTRAINT fk_loan_book FOREIGN KEY (book_id) REFERENCES book(book_id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
);

-- Return: one row per completed loan (loan_id is unique so a loan cannot be "returned" twice).
-- Name avoids MySQL reserved word RETURN.
CREATE TABLE IF NOT EXISTS book_return (
  return_id INT AUTO_INCREMENT PRIMARY KEY,
  loan_id INT NOT NULL,
  return_date DATE NOT NULL,
  notes VARCHAR(500) NULL,
  CONSTRAINT fk_book_return_loan FOREIGN KEY (loan_id) REFERENCES loan(loan_id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT uq_book_return_loan UNIQUE (loan_id)
);

-- Active loans (still out): loan rows with no matching book_return row.
-- Example:
-- SELECT l.* FROM loan l
-- LEFT JOIN book_return r ON r.loan_id = l.loan_id
-- WHERE r.return_id IS NULL;
