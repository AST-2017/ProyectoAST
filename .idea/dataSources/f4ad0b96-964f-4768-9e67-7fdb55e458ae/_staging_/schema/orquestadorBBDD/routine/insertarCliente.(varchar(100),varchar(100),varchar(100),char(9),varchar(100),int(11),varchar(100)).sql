CREATE PROCEDURE insertarCliente(IN `_nombre`    VARCHAR(100),
                                 IN `_apellido1` VARCHAR(100),
                                 IN `_apellido2` VARCHAR(100),
                                 IN `_dni` CHAR(9),
                                 IN `_email` VARCHAR(100),
                                 IN `_telefono`  INT,
                                 IN `_password` VARCHAR(100))
  BEGIN
    IF(SELECT EXISTS(SELECT * FROM clientes WHERE clientes.dni = _dni)) THEN
      SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT ='Ya existe un usuario con ese nombre';
    ELSE
      INSERT INTO clientes(nombre, apellido1, apellido2, dni, email, telefono, password)
      VALUES (_nombre,_apellido1,_apellido2,_dni,_email,_telefono,_password);
    END IF;
END;
