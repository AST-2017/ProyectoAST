use orquestadorBBDD;

DROP PROCEDURE IF EXISTS insertarCliente;
DROP PROCEDURE IF EXISTS borrarOfertas;
DROP PROCEDURE IF EXISTS insertarOferta;
DROP PROCEDURE IF EXISTS insertarReserva;
DROP PROCEDURE IF EXISTS verReservasCliente;
DROP PROCEDURE IF EXISTS comprobarRegistro;
DROP PROCEDURE IF EXISTS obtenerCodigoIATA;
DROP PROCEDURE IF EXISTS introducirIata;
DROP PROCEDURE IF EXISTS obtenerEmail;
DROP PROCEDURE IF EXISTS obtenerPrecioOferta;

DELIMITER //
CREATE PROCEDURE obtenerCodigoIATA(IN ciudad VARCHAR(20))
  BEGIN
    SELECT codigoIATA
    FROM iata
    WHERE iata.ciudad = ciudad;
  END//
DELIMITER ;

DELIMITER //
CREATE PROCEDURE introducirIata(
  IN ciudad VARCHAR(100),
  IN codigoIATA VARCHAR(20))
  BEGIN
    IF (SELECT count(*) FROM iata WHERE iata.codigoIATA = codigoIATA OR iata.ciudad = ciudad) = 0 THEN
      BEGIN
        INSERT INTO iata(ciudad, codigoIATA) values (ciudad,codigoIATA);
      END;
    END IF;
  END//
DELIMITER ;

/* Procedimiento almacenado insertarCliente: inserta un cliente en la base de datos si no existe. */
DELIMITER //
CREATE PROCEDURE insertarCliente(
  IN _nombre VARCHAR(100),
  IN _apellido1 VARCHAR(100),
  IN _apellido2 VARCHAR(100),
  IN _dni CHAR(9),
  IN _email VARCHAR(100),
  IN _telefono INTEGER,
  IN _password VARCHAR(100))
  BEGIN
    IF(SELECT EXISTS(SELECT * FROM clientes WHERE clientes.dni = _dni)) THEN
      SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT ='Ya existe un usuario con ese nombre';
    ELSE
      INSERT INTO clientes(nombre, apellido1, apellido2, dni, email, telefono, password)
      VALUES (_nombre,_apellido1,_apellido2,_dni,_email,_telefono,_password);
    END IF;
  END //
DELIMITER ;


/**
Procedimiento almacenado que borra todas las ofertas de un cliente.
 */

DELIMITER //
CREATE PROCEDURE borrarOfertas(IN _dni CHAR(9))
  BEGIN
    DECLARE _id_cliente INTEGER DEFAULT 0;

    IF(SELECT EXISTS(SELECT * FROM clientes WHERE clientes.dni = _dni)) THEN
      SET _id_cliente = (SELECT clientes.id FROM clientes WHERE clientes.dni = _dni);
      DELETE FROM ofertas WHERE ofertas.id_cliente = _id_cliente;
    ELSE
      SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT ='No existe ningun cliente registrado con este DNI.';
    END IF;
  END //
DELIMITER ;


/**
* Procedimiento almacenado insertarOferta: inserta una nueva oferta para un cliente.
*/
DELIMITER //
CREATE PROCEDURE insertarOferta(
  IN _dni CHAR(9),
  IN _id_oferta_vuelo INTEGER,
  IN _precio INTEGER,
  IN _vueloDirectoSalida BOOLEAN,
  IN _vueloDirectoRegreso BOOLEAN,
  IN _fechaSalida VARCHAR(50),
  IN _fechaRegreso VARCHAR(50),
  IN _origen VARCHAR(100),
  IN _destino VARCHAR(100),
  IN _codigoIATAOrigen VARCHAR(20),
  IN _codigoIATADestino VARCHAR(20),
  IN _aerolineaSalida VARCHAR(100),
  IN _aerolineaRegreso VARCHAR(100))
  BEGIN
    DECLARE _id_cliente INTEGER DEFAULT 0;

    IF(SELECT EXISTS(SELECT * FROM clientes WHERE clientes.dni = _dni)) THEN
      SET _id_cliente = (SELECT clientes.id FROM clientes WHERE clientes.dni = _dni);
      INSERT INTO ofertas(id_cliente,
                          id_oferta_vuelo,
                          precio,
                          vueloDirectoSalida,
                          vueloDirectoRegreso,
                          fechaSalida,
                          fechaRegreso,
                          origen,
                          destino,
                          codigoIATAOrigen,
                          codigoIATADestino,
                          aerolineaSalida,
                          aerolineaRegreso)
      VALUES(_id_cliente,
        _id_oferta_vuelo,
        _precio,
        _vueloDirectoSalida,
        _vueloDirectoRegreso,
        _fechaSalida,
        _fechaRegreso,
        _origen,
        _destino,
        _codigoIATAOrigen,
        _codigoIATADestino,
             _aerolineaSalida,
             _aerolineaRegreso);
    ELSE
      SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT ='No existe ningun cliente registrado con este DNI.';
    END IF;
  END //

DELIMITER ;



/* Procedimiento almacenado insertarReserva: inserta una nueva reserva para un cliente.*/
DELIMITER //
CREATE PROCEDURE insertarReserva(
  IN _dni CHAR(9),
  IN _id_oferta_vuelo INTEGER)
  BEGIN
    DECLARE _id_cliente INTEGER DEFAULT 0;
    DECLARE _precio INTEGER DEFAULT 0;
    DECLARE _vueloDirectoSalida BOOLEAN DEFAULT TRUE;
    DECLARE _vueloDirectoRegreso BOOLEAN DEFAULT TRUE;
    DECLARE _fechaSalida VARCHAR(50) DEFAULT NULL;
    DECLARE _fechaRegreso VARCHAR(50) DEFAULT NULL;
    DECLARE _origen VARCHAR(100) DEFAULT NULL;
    DECLARE _destino VARCHAR(100) DEFAULT NULL;
    DECLARE _codigoIATAOrigen VARCHAR(20) DEFAULT NULL;
    DECLARE _codigoIATADestino VARCHAR(20) DEFAULT NULL;
    DECLARE _aerolineaSalida VARCHAR(100) DEFAULT NULL;
    DECLARE _aerolineaRegreso VARCHAR(100) DEFAULT NULL;

    IF(SELECT EXISTS(SELECT * FROM clientes WHERE clientes.dni = _dni)) THEN
      IF (SELECT EXISTS(
          SELECT *
          FROM ofertas,clientes
          WHERE
            clientes.dni = _dni AND
            clientes.id = ofertas.id_cliente AND
            ofertas.id_oferta_vuelo = _id_oferta_vuelo)
      ) THEN

        SET _id_cliente = (SELECT clientes.id FROM clientes WHERE clientes.dni = _dni);
        SET _precio = (SELECT ofertas.precio FROM ofertas WHERE ofertas.id_cliente = _id_cliente AND ofertas.id_oferta_vuelo = _id_oferta_vuelo);
        SET _vueloDirectoSalida = (SELECT ofertas.vueloDirectoSalida FROM ofertas WHERE ofertas.id_cliente = _id_cliente AND ofertas.id_oferta_vuelo = _id_oferta_vuelo);
        SET _vueloDirectoRegreso = (SELECT ofertas.vueloDirectoRegreso FROM ofertas WHERE ofertas.id_cliente = _id_cliente AND ofertas.id_oferta_vuelo = _id_oferta_vuelo);
        SET _fechaSalida = (SELECT ofertas.fechaSalida FROM ofertas WHERE ofertas.id_cliente = _id_cliente AND ofertas.id_oferta_vuelo = _id_oferta_vuelo);
        SET _fechaRegreso = (SELECT ofertas.fechaRegreso FROM ofertas WHERE ofertas.id_cliente = _id_cliente AND ofertas.id_oferta_vuelo = _id_oferta_vuelo);
        SET _origen = (SELECT ofertas.origen FROM ofertas WHERE ofertas.id_cliente = _id_cliente AND ofertas.id_oferta_vuelo = _id_oferta_vuelo);
        SET _destino = (SELECT ofertas.destino FROM ofertas WHERE ofertas.id_cliente = _id_cliente AND ofertas.id_oferta_vuelo = _id_oferta_vuelo);
        SET _codigoIATAOrigen = (SELECT ofertas.codigoIATAOrigen FROM ofertas WHERE ofertas.id_cliente = _id_cliente AND ofertas.id_oferta_vuelo = _id_oferta_vuelo);
        SET _codigoIATADestino = (SELECT ofertas.codigoIATADestino FROM ofertas WHERE ofertas.id_cliente = _id_cliente AND ofertas.id_oferta_vuelo = _id_oferta_vuelo);
        SET _aerolineaSalida = (SELECT ofertas.aerolineaSalida FROM ofertas WHERE ofertas.id_cliente = _id_cliente AND ofertas.id_oferta_vuelo = _id_oferta_vuelo);
        SET _aerolineaRegreso = (SELECT ofertas.aerolineaRegreso FROM ofertas WHERE ofertas.id_cliente = _id_cliente AND ofertas.id_oferta_vuelo = _id_oferta_vuelo);

        INSERT INTO reservas(id_cliente,
                             precio,
                             vueloDirectoSalida,
                             vueloDirectoRegreso,
                             fechaSalida,
                             fechaRegreso,
                             origen,
                             destino,
                             codigoIATAOrigen,
                             codigoIATADestino,
                             aerolineaSalida,
                             aerolineaRegreso)
        VALUES(_id_cliente,
          _precio,
          _vueloDirectoSalida,
          _vueloDirectoRegreso,
          _fechaSalida,
          _fechaRegreso,
          _origen,
          _destino,
          _codigoIATAOrigen,
          _codigoIATADestino,
          _aerolineaSalida,
               _aerolineaRegreso);

      ELSE
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT ='No existe dicha oferta para ese cliente.';
      END IF;
    ELSE
      SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT ='No existe ningun cliente registrado con este DNI.';
    END IF;
  END //

DELIMITER ;


/*Procedimiento almacenado ver_reservas cliente: obtiene todas las reservas de un cliente dado su dni. */
DELIMITER //
CREATE PROCEDURE verReservasCliente(IN _dni CHAR(9))
  BEGIN
    IF (SELECT EXISTS(SELECT * FROM clientes WHERE clientes.dni = _dni)) THEN
      SELECT
        reservas.precio,
        reservas.vueloDirectoSalida,
        reservas.vueloDirectoRegreso,
        reservas.fechaSalida,
        reservas.fechaRegreso,
        reservas.origen,
        reservas.destino,
        reservas.codigoIATAOrigen,
        reservas.codigoIATADestino,
        reservas.aerolineaSalida,
        reservas.aerolineaRegreso
      FROM clientes,reservas
      WHERE clientes.dni = _dni AND clientes.id = reservas.id_cliente;
    ELSE
      SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT ='No existe ningun cliente registrado con este DNI.';
    END IF;
  END //
DELIMITER ;

/* Procedimiento almacenado comprobar_registro: comprueba si un cliente esta registrado en el sistema por medio de su dni y una contraseña. */

DELIMITER //
CREATE PROCEDURE comprobarRegistro(
  IN _dni CHAR(9),
  IN _password VARCHAR(100))
  BEGIN
    SELECT id FROM clientes WHERE clientes.dni = _dni AND clientes.password = _password;
  END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE obtenerEmail(IN _dni CHAR(9))
  BEGIN
    IF (SELECT EXISTS(SELECT * FROM clientes WHERE clientes.dni = _dni)) THEN
      SELECT email FROM clientes WHERE clientes.dni = _dni;
    ELSE
      SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT ='No existe ningun cliente registrado con este DNI.';
    END IF;
  END//
DELIMITER ;

/*Procedimiento almacenado obtenerPrecioOferta: usado para obtener el precio de la oferta seleccionada por el cliente. */
DELIMITER //
CREATE PROCEDURE obtenerPrecioOferta(IN _dni CHAR(9), IN _id_oferta_vuelo INTEGER)
  BEGIN
    IF(SELECT EXISTS(SELECT * FROM clientes WHERE clientes.dni = _dni)) THEN
      IF (SELECT EXISTS(SELECT *
                        FROM clientes,ofertas
                        WHERE
                          clientes.dni = _dni AND
                          clientes.id = ofertas.id_cliente AND
                          ofertas.id_oferta_vuelo = _id_oferta_vuelo)) THEN
        SELECT ofertas.precio
        FROM clientes,ofertas
        WHERE
          clientes.dni = _dni AND
          clientes.id = ofertas.id_cliente AND
          ofertas.id_oferta_vuelo = _id_oferta_vuelo;
      ELSE
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT ='No existe tal oferta para dicho cliente.';
      END IF;
    ELSE
      SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT ='No existe ningun cliente registrado con este DNI.';
    END IF;
  END //
DELIMITER ;


/*
Procedimiento almacenado buscarOferta: usado para mandar el mensaje al banco
 */
DELIMITER //
CREATE PROCEDURE buscarOferta(IN _dni CHAR(9), IN _id_oferta_vuelo INT)
BEGIN
  SELECT
    clientes.nombre,
    clientes.apellido1,
    clientes.apellido2,
    ofertas.precio,
    ofertas.vueloDirectoSalida,
    ofertas.vueloDirectoRegreso,
    ofertas.fechaSalida,
    ofertas.fechaRegreso,
    ofertas.origen,
    ofertas.destino,
    ofertas.aerolineaSalida,
    ofertas.aerolineaRegreso
  FROM clientes,ofertas
  WHERE
    clientes.dni = _dni AND
    ofertas.id_cliente = clientes.id AND
    ofertas.id_oferta_vuelo = _id_oferta_vuelo;
END //
DELIMITER ;