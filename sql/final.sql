-- Final schema for bookloan_and_return.
-- Schema-only: no INSERT statements are included in this file.

CREATE DATABASE IF NOT EXISTS bookloan_and_return
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE bookloan_and_return;

CREATE TABLE IF NOT EXISTS book (
  book_id INT AUTO_INCREMENT PRIMARY KEY,
  bookName VARCHAR(255) NOT NULL,
  author VARCHAR(200) NOT NULL,
  genre VARCHAR(100) NOT NULL,
  published_year VARCHAR(10) NOT NULL,
  stock INT NOT NULL DEFAULT 0,
  cost DECIMAL(10,2) NOT NULL DEFAULT 0.00
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS student (
  student_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  full_name VARCHAR(200) NOT NULL,
  phone VARCHAR(20) NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS loan (
  loan_id INT AUTO_INCREMENT PRIMARY KEY,
  book_id INT NOT NULL,
  borrower_name VARCHAR(200) NOT NULL,
  student_id BIGINT NULL,
  loan_date DATE NOT NULL,
  due_date DATE NOT NULL,
  CONSTRAINT fk_loan_book FOREIGN KEY (book_id) REFERENCES book(book_id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT fk_loan_student_id FOREIGN KEY (student_id) REFERENCES student(student_id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS book_return (
  return_id INT AUTO_INCREMENT PRIMARY KEY,
  loan_id INT NOT NULL,
  return_date DATE NOT NULL,
  fine_pesos INT NOT NULL DEFAULT 0,
  amount_paid INT NOT NULL DEFAULT 0,
  fine_paid BOOLEAN NOT NULL DEFAULT FALSE,
  notes VARCHAR(500) NULL,
  CONSTRAINT fk_book_return_loan FOREIGN KEY (loan_id) REFERENCES loan(loan_id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT uq_book_return_loan UNIQUE (loan_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS receipts (
  receipt_id INT AUTO_INCREMENT PRIMARY KEY,
  return_id INT NOT NULL,
  loan_id INT NOT NULL,
  borrower_name VARCHAR(200) NOT NULL,
  book_title VARCHAR(300) NOT NULL,
  student_id BIGINT NULL,
  loan_date DATE NULL,
  due_date DATE NOT NULL,
  return_date DATE NOT NULL,
  days_late INT NOT NULL DEFAULT 0,
  fine_amount INT NOT NULL DEFAULT 0,
  amount_paid INT NOT NULL DEFAULT 0,
  fine_paid BOOLEAN NOT NULL DEFAULT FALSE,
  notes VARCHAR(500) NULL,
  printed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_receipt_return FOREIGN KEY (return_id) REFERENCES book_return(return_id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT fk_receipt_loan FOREIGN KEY (loan_id) REFERENCES loan(loan_id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
