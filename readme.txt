ARQUITECTURAS Y SERVICIOS TELEMÁTICOS - PROYECTO C - CRUSO 2016/207

Rubén Pérez Vaz
Sara Sánchez Piñeiro
Sara Berezo Loza
Marta Blanco Caamaño



-------------------- INSTRUCCIONES --------------------


CLIENTE
Los clientes, generados en Eclipse y exportandos como 'runnable .jar', se ejecutan desde una terminal: 'java -jar cliente ruta-fichero-uddi.xml'.

SERVICIOS
Los servicios se compilarán y construirán (fase build) mediante la herramienta Apache ANT. Estos se desplegarán en Axis2, introduciéndolos (servicio.aar) en la carpeta 'Services' de Axis2 en Tomcat. 

HANDLER (BANCO)
Guardar el módulo ModuloBanco.mar en el directorio Tomcat/webapps/axis2/WEB-INF/modules y modificar el fichero axis2.xml (Tomcat 8.5/webapps/axis2/WEB-INF/conf) añadiendo en InFlow,  después de la fase RMPhase (lugar del fichero donde el usuaio puede añadir sus propias fases) nuestra propia fase (<phase name="bancoPhase"/>).

UDDI
Cada equipo en el que se haya uno de los servicios o un cliente contará con el archivo uddi.xml (usado para publicar y buscar servicios). Este está en una ruta local (por lo que en caso de cambiarlo de directorio habría que modificar las clases de publicación y búsqueda de servicios) y en él se modificará la IP, introduciendo aquella desde la cual se ejecuta jUDDI y el puerto.


-------------------- OTROS ASPECTOS A TENER EN CUENTA --------------------


BASES DE DATOS
Tanto el Orquestador como el servicio Banco, hacen consultas a una base de datos. Por ello, hay que tener en cuenta que para conseguir el correcto funcionamiento del conjunto, hay que crear dichas bases de datos localmente en la misma máquina que los servicios nombrados.

IP
Cada servicio captura la IP de su máquina localmente. Hay que prestar atención ya que en Windows, capturará la primera IPv4 que encuentre y, en caso de tener una máquina virtual, la que obtendrá será esta. Por ello, deshabilitaremos la conexión de red de la máquina virtual para las pruebas.

LIBRERÍAS
En el proyecto usamos las librerías de Tomcat, Axis2 además de otras librerías externas de jackson JSON, jUDDI, MySQL y UDDI.
Todas ellas se insertan tanto en la carpeta 'lib' de nuestro Tomcat como en la carpeca 'lib' de Axis2.