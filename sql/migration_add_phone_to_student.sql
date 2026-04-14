USE bookloan_and_return;

-- Add phone number column to student table
ALTER TABLE student
  ADD COLUMN phone VARCHAR(20) NULL;
