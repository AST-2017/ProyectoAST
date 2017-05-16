ARQUITECTURAS Y SERVICIOS TELEM�TICOS - PROYECTO C - CRUSO 2016/207

Rub�n P�rez Vaz
Sara S�nchez Pi�eiro
Sara Berezo Loza
Marta Blanco Caama�o



-------------------- INSTRUCCIONES --------------------


CLIENTE
Los clientes, generados en Eclipse y exportandos como 'runnable .jar', se ejecutan desde una terminal: 'java -jar cliente ruta-fichero-uddi.xml'.

SERVICIOS
Los servicios se compilar�n y construir�n (fase build) mediante la herramienta Apache ANT. Estos se desplegar�n en Axis2, introduci�ndolos (servicio.aar) en la carpeta 'Services' de Axis2 en Tomcat. 

HANDLER (BANCO)
Guardar el m�dulo ModuloBanco.mar en el directorio Tomcat/webapps/axis2/WEB-INF/modules y modificar el fichero axis2.xml (Tomcat 8.5/webapps/axis2/WEB-INF/conf) a�adiendo en InFlow,  despu�s de la fase RMPhase (lugar del fichero donde el usuaio puede a�adir sus propias fases) nuestra propia fase (<phase name="bancoPhase"/>).

UDDI
Cada equipo en el que se haya uno de los servicios o un cliente contar� con el archivo uddi.xml (usado para publicar y buscar servicios). Este est� en una ruta local (por lo que en caso de cambiarlo de directorio habr�a que modificar las clases de publicaci�n y b�squeda de servicios) y en �l se modificar� la IP, introduciendo aquella desde la cual se ejecuta jUDDI y el puerto.


-------------------- OTROS ASPECTOS A TENER EN CUENTA --------------------


BASES DE DATOS
Tanto el Orquestador como el servicio Banco, hacen consultas a una base de datos. Por ello, hay que tener en cuenta que para conseguir el correcto funcionamiento del conjunto, hay que crear dichas bases de datos localmente en la misma m�quina que los servicios nombrados.

IP
Cada servicio captura la IP de su m�quina localmente. Hay que prestar atenci�n ya que en Windows, capturar� la primera IPv4 que encuentre y, en caso de tener una m�quina virtual, la que obtendr� ser� esta. Por ello, deshabilitaremos la conexi�n de red de la m�quina virtual para las pruebas.

LIBRER�AS
En el proyecto usamos las librer�as de Tomcat, Axis2 adem�s de otras librer�as externas de jackson JSON, jUDDI, MySQL y UDDI.
Todas ellas se insertan tanto en la carpeta 'lib' de nuestro Tomcat como en la carpeca 'lib' de Axis2.