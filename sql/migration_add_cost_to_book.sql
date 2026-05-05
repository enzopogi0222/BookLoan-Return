-- Run against database: bookloan_and_return
-- Adds cost column to book table for tracking book value/pricing

ALTER TABLE book ADD COLUMN IF NOT EXISTS cost DECIMAL(10,2) NOT NULL DEFAULT 0.00;

-- Update existing books with a default cost if needed
-- UPDATE book SET cost = 0.00 WHERE cost IS NULL;
