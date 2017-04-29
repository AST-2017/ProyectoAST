use orquestadorBBDD;

set foreign_key_checks = 0;
DROP TABLE IF EXISTS clientes;
DROP TABLE IF EXISTS ofertas;
DROP TABLE IF EXISTS reservas;
DROP TABLE IF EXISTS iata;
set foreign_key_checks = 1;

CREATE TABLE IF NOT EXISTS iata(
  id INTEGER AUTO_INCREMENT PRIMARY KEY,
  ciudad VARCHAR(100) NOT NULL UNIQUE,
  codigoIATA VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE clientes(
  id INTEGER AUTO_INCREMENT PRIMARY KEY ,
  nombre VARCHAR(100) NOT NULL ,
  apellido1 VARCHAR(100) NOT NULL ,
  apellido2 VARCHAR(100) NOT NULL ,
  dni CHAR(9) NOT NULL UNIQUE ,
  email VARCHAR(100) NOT NULL ,
  telefono INTEGER NOT NULL,
  password VARCHAR(100) NOT NULL
)ENGINE=innodb;

CREATE TABLE ofertas(
  id INTEGER AUTO_INCREMENT PRIMARY KEY ,
  id_cliente INTEGER NOT NULL ,
  id_oferta_vuelo INTEGER NOT NULL ,
  precio INTEGER NOT NULL ,
  vueloDirectoSalida BOOLEAN NOT NULL ,
  vueloDirectoRegreso BOOLEAN NOT NULL ,
  fechaSalida VARCHAR(50) NOT NULL ,
  fechaRegreso VARCHAR(50) NOT NULL ,
  origen VARCHAR(100) NOT NULL ,
  destino VARCHAR(100) NOT NULL ,
  codigoIATAOrigen VARCHAR(20) NOT NULL,
  codigoIATADestino VARCHAR(20) NOT NULL,
  aerolineaSalida VARCHAR(100) NOT NULL ,
  aerolineaRegreso VARCHAR(100) NOT NULL,
  FOREIGN KEY (id_cliente) REFERENCES clientes(id) ON DELETE CASCADE
)ENGINE=innodb;

CREATE TABLE reservas(
  id INTEGER AUTO_INCREMENT PRIMARY KEY ,
  id_cliente INTEGER NOT NULL ,
  precio INTEGER NOT NULL ,
  vueloDirectoSalida BOOLEAN NOT NULL ,
  vueloDirectoRegreso BOOLEAN NOT NULL ,
  fechaSalida VARCHAR(50) NOT NULL ,
  fechaRegreso VARCHAR(50) NOT NULL ,
  origen VARCHAR(100) NOT NULL ,
  destino VARCHAR(100) NOT NULL ,
  codigoIATAOrigen VARCHAR(20) NOT NULL,
  codigoIATADestino VARCHAR(20) NOT NULL,
  aerolineaSalida VARCHAR(100) NOT NULL ,
  aerolineaRegreso VARCHAR(100) NOT NULL,
  FOREIGN KEY (id_cliente) REFERENCES clientes(id) ON DELETE CASCADE
)ENGINE=innodb;
