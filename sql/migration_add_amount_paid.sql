USE bookloan_and_return;

-- Add amount_paid column to track partial payments
ALTER TABLE book_return
  ADD COLUMN amount_paid INT NOT NULL DEFAULT 0;

-- Add amount_paid column to receipts table too
ALTER TABLE receipts
  ADD COLUMN amount_paid INT NOT NULL DEFAULT 0;

-- Update existing records where fine_paid is TRUE to set amount_paid = fine_pesos
UPDATE book_return
SET amount_paid = fine_pesos
WHERE fine_paid = TRUE;

UPDATE receipts
SET amount_paid = fine_amount
WHERE fine_paid = TRUE;
