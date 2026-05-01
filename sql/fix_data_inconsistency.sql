USE bookloan_and_return;

-- Fix any data inconsistencies where amount_paid is 0 but fine_paid is TRUE
UPDATE book_return SET amount_paid = fine_pesos WHERE fine_paid = TRUE AND amount_paid = 0;

-- Fix any data inconsistencies in receipts table too
UPDATE receipts SET amount_paid = fine_amount WHERE fine_paid = TRUE AND amount_paid = 0;
