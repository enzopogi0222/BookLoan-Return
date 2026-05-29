-- Full schema for the Book Loan and Return system.
-- Schema only. No seed or sample rows are included.

SET SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO';
SET time_zone = '+00:00';
SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS bookloan_and_return
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE bookloan_and_return;

CREATE TABLE IF NOT EXISTS book (
  book_id INT NOT NULL AUTO_INCREMENT,
  bookName VARCHAR(255) NOT NULL,
  author VARCHAR(200) NOT NULL,
  genre VARCHAR(100) NOT NULL,
  published_year VARCHAR(10) NOT NULL,
  stock INT NOT NULL DEFAULT 0,
  cost DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  PRIMARY KEY (book_id),
  INDEX idx_book_bookName (bookName),
  CONSTRAINT chk_book_stock CHECK (stock >= 0),
  CONSTRAINT chk_book_cost CHECK (cost >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS student (
  student_id BIGINT NOT NULL AUTO_INCREMENT,
  full_name VARCHAR(200) NOT NULL,
  phone VARCHAR(20) NULL,
  PRIMARY KEY (student_id),
  INDEX idx_student_full_name (full_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS loan (
  loan_id INT NOT NULL AUTO_INCREMENT,
  book_id INT NOT NULL,
  borrower_name VARCHAR(200) NOT NULL,
  student_id BIGINT NULL,
  loan_date DATE NOT NULL,
  due_date DATE NOT NULL,
  PRIMARY KEY (loan_id),
  INDEX idx_loan_book_id (book_id),
  INDEX idx_loan_student_id (student_id),
  INDEX idx_loan_dates (loan_date, due_date),
  CONSTRAINT fk_loan_book
    FOREIGN KEY (book_id) REFERENCES book (book_id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT fk_loan_student
    FOREIGN KEY (student_id) REFERENCES student (student_id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT chk_loan_date_order CHECK (due_date >= loan_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS book_return (
  return_id INT NOT NULL AUTO_INCREMENT,
  loan_id INT NOT NULL,
  return_date DATE NOT NULL,
  fine_pesos INT NOT NULL DEFAULT 0,
  amount_paid INT NOT NULL DEFAULT 0,
  fine_paid BOOLEAN NOT NULL DEFAULT FALSE,
  notes VARCHAR(500) NULL,
  book_condition VARCHAR(20) NOT NULL DEFAULT 'good',
  PRIMARY KEY (return_id),
  UNIQUE KEY uq_book_return_loan (loan_id),
  INDEX idx_book_return_date (return_date),
  INDEX idx_book_return_payment (fine_paid, fine_pesos, amount_paid),
  CONSTRAINT fk_book_return_loan
    FOREIGN KEY (loan_id) REFERENCES loan (loan_id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT chk_book_return_fine CHECK (fine_pesos >= 0),
  CONSTRAINT chk_book_return_amount_paid CHECK (amount_paid >= 0 AND amount_paid <= fine_pesos),
  CONSTRAINT chk_book_return_condition CHECK (book_condition IN ('good', 'damaged', 'lost'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS receipts (
  receipt_id INT NOT NULL AUTO_INCREMENT,
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
  printed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (receipt_id),
  INDEX idx_receipts_return_id (return_id),
  INDEX idx_receipts_loan_id (loan_id),
  INDEX idx_receipts_student_id (student_id),
  INDEX idx_receipts_printed_at (printed_at),
  CONSTRAINT fk_receipts_return
    FOREIGN KEY (return_id) REFERENCES book_return (return_id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT fk_receipts_loan
    FOREIGN KEY (loan_id) REFERENCES loan (loan_id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE,
  CONSTRAINT fk_receipts_student
    FOREIGN KEY (student_id) REFERENCES student (student_id)
    ON DELETE SET NULL
    ON UPDATE CASCADE,
  CONSTRAINT chk_receipts_days_late CHECK (days_late >= 0),
  CONSTRAINT chk_receipts_fine_amount CHECK (fine_amount >= 0),
  CONSTRAINT chk_receipts_amount_paid CHECK (amount_paid >= 0 AND amount_paid <= fine_amount)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
