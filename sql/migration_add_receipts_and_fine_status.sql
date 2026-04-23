USE bookloan_and_return;

-- Add fine payment status to book_return
ALTER TABLE book_return
  ADD COLUMN fine_paid BOOLEAN NOT NULL DEFAULT FALSE;

-- Create receipts table to store receipt records
CREATE TABLE IF NOT EXISTS receipts (
  receipt_id INT AUTO_INCREMENT PRIMARY KEY,
  return_id INT NOT NULL,
  loan_id INT NOT NULL,
  borrower_name VARCHAR(200) NOT NULL,
  book_title VARCHAR(300) NOT NULL,
  loan_date DATE NULL,
  due_date DATE NOT NULL,
  return_date DATE NOT NULL,
  days_late INT NOT NULL DEFAULT 0,
  fine_amount INT NOT NULL DEFAULT 0,
  fine_paid BOOLEAN NOT NULL DEFAULT FALSE,
  notes VARCHAR(500) NULL,
  printed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_receipt_return FOREIGN KEY (return_id) REFERENCES book_return(return_id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT fk_receipt_loan FOREIGN KEY (loan_id) REFERENCES loan(loan_id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
);
