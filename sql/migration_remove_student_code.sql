USE bookloan_and_return;

-- Remove student_code column from student table
ALTER TABLE student
  DROP COLUMN student_code;
