CREATE DATABASE IF NOT EXISTS folhaDePonto;

USE folhaDePonto;

CREATE TABLE IF NOT EXISTS MOMENTO_BATIDA (
    id int NOT NULL AUTO_INCREMENT,
    momento TIMESTAMP NOT NULL,
    dia VARCHAR(2) NOT NULL,
    mes VARCHAR(2) NOT NULL,
    ano VARCHAR(4) NOT NULL,
    PRIMARY KEY(id),
    UNIQUE (momento)
);

DELIMITER $$
CREATE TRIGGER max_entries_per_date
BEFORE INSERT ON MOMENTO_BATIDA
FOR EACH ROW
BEGIN
  DECLARE count INT;
  SELECT COUNT(*) INTO count FROM MOMENTO_BATIDA 
  WHERE dia = NEW.dia AND mes = NEW.mes AND ano = NEW.ano;
  IF count >= 4 THEN
    SIGNAL SQLSTATE '45000' 
      SET MESSAGE_TEXT = 'MAXIMUM_NUMBER_OF_ENTRIES_EXCEEDED';
  END IF;
END$$
DELIMITER ;

