USE bookloan_and_return;

ALTER TABLE book_return
  ADD COLUMN fine_pesos INT NOT NULL DEFAULT 0;
