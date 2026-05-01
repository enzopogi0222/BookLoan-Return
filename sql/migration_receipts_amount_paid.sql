USE bookloan_and_return;

-- Add amount_paid column to receipts table (book_return already has it)
ALTER TABLE receipts
  ADD COLUMN IF NOT EXISTS amount_paid INT NOT NULL DEFAULT 0;

-- Update existing records
UPDATE book_return SET amount_paid = fine_pesos WHERE fine_paid = TRUE AND amount_paid = 0;
UPDATE receipts SET amount_paid = fine_amount WHERE fine_paid = TRUE AND amount_paid = 0;
