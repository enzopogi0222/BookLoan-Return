USE bookloan_and_return;

-- Add book condition column to track damaged or lost books
ALTER TABLE book_return
  ADD COLUMN book_condition VARCHAR(20) NOT NULL DEFAULT 'good';

-- Add constraint to ensure valid values
-- good = returned in good condition
-- damaged = returned damaged/broken
-- lost = book was lost
