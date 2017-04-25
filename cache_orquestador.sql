CREATE TABLE IF NOT EXISTS iata(
  id INTEGER AUTO_INCREMENT PRIMARY KEY,
  ciudad VARCHAR(100) NOT NULL UNIQUE,
  codigoIATA VARCHAR(20) NOT NULL UNIQUE
);

DROP PROCEDURE IF EXISTS obtener_codigoIATA;
DROP PROCEDURE IF EXISTS introducir_iata;
DELIMITER //

CREATE PROCEDURE obtener_codigoIATA(IN ciudad VARCHAR(20))
BEGIN
  SELECT codigoIATA
  FROM iata
  WHERE iata.ciudad = ciudad;
END//

CREATE PROCEDURE introducir_iata(IN ciudad VARCHAR(100), IN codigoIATA VARCHAR(20))
BEGIN
  IF (SELECT count(*) FROM iata WHERE iata.codigoIATA = codigoIATA OR iata.ciudad = ciudad) = 0 THEN
    BEGIN
    INSERT INTO iata(ciudad, codigoIATA) values (ciudad,codigoIATA);
    END;
  END IF;
END//

DELIMITER ;
