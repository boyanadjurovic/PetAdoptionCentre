CREATE FUNCTION household_selection(IN input_pid INT, IN desired_wage INT, IN input_eid INT) RETURNS void
LANGUAGE plpgsql
AS $$
 DECLARE p_address TEXT;
 DECLARE p_wage INT;
 DECLARE max_wage INT DEFAULT 0;
 DECLARE max_address TEXT;
 DECLARE p_cursor CURSOR FOR 
 SELECT address, wage FROM potentialhousehold 
 WHERE address IN (
 SELECT a.address FROM received r JOIN adoptionapplication a
 ON r.aid = a.aid
 WHERE r.pid = input_pid);
BEGIN
 OPEN p_cursor;
 LOOP
  FETCH p_cursor INTO p_address, p_wage;
  EXIT WHEN NOT FOUND;

  IF (p_wage < desired_wage)
  THEN 
   INSERT INTO document(SELECT COUNT(docnumber) + 1, CURRENT_DATE FROM document);
   INSERT INTO denialdocument (SELECT COUNT(docnumber),'The potential household wage is too  low to adopt the pet'FROM document);        
   INSERT INTO receive(SELECT COUNT(docnumber), p_address FROM document);

  ELSE 
   IF (p_wage > max_wage) 
   THEN
    max_wage := p_wage;
    max_address := p_address;
   END IF;
  END IF;
 END LOOP;
 CLOSE p_cursor;
 IF (max_wage > 0) 
 THEN
  INSERT INTO document(SELECT COUNT(docnumber) + 1, CURRENT_DATE FROM document);
  INSERT INTO accepteddocument(SELECT COUNT(docnumber),input_eid,NULL,NULL,CURRENT_DATE+7,NULL FROM document);
  INSERT INTO receive(SELECT COUNT(docnumber), max_address FROM document);
  UPDATE pet SET kno = 'To be adopted' WHERE pid = input_pid;
 END IF;
END
$$;
