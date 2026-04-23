USE bookloan_and_return;

-- Change student_id to BIGINT to support larger student ID numbers
ALTER TABLE student
  MODIFY COLUMN student_id BIGINT AUTO_INCREMENT;

-- Add student_id column to loan table
ALTER TABLE loan
  ADD COLUMN student_id BIGINT NULL,
  ADD CONSTRAINT fk_loan_student_id FOREIGN KEY (student_id) REFERENCES student(student_id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE;

-- Also add to receipts table for consistency
ALTER TABLE receipts
  ADD COLUMN student_id BIGINT NULL;
