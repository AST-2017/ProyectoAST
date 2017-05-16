

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.axiom.soap.SOAPHeader;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.handlers.AbstractHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HandlerBanco extends AbstractHandler {

	private static final Log log = LogFactory.getLog(HandlerBanco.class);
	private final static String usuario = "ast";
	private final static String contra = "ast";
	private final static String bd = "banco";
	/*use banco;
	
	create table credenciales(
	  token char(10),
      cuenta int unique,
      primary key (cuenta)
    );
    
    insert into credenciales (token, cuenta) values ('qwerty',87654321);
    insert into credenciales (token, cuenta) values ('asdfgh',98446488);
	*/

    public InvocationResponse invoke(MessageContext msgContext) throws AxisFault {
		SOAPHeader header = msgContext.getEnvelope().getHeader();
		String tokenCuenta = header.getFirstElement().getText();
		String token = tokenCuenta.split("-")[0];
		String cuenta = tokenCuenta.split("-")[1];
		log.info("Token: "+token+", Cuenta: "+cuenta);

		// Comprobar credenciales
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection("jdbc:mysql://localhost/" + bd + "?" + "user=" + usuario + "&password=" + contra);
			stmt = conn.createStatement();

			String query = "select * from credenciales where token='" + token + "' and cuenta='" + cuenta + "'";
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				log.info("Credenciales correctos");
				return InvocationResponse.CONTINUE;
			}
			log.info("Credenciales incorrectos");
			return InvocationResponse.ABORT;
		} catch (Exception e) {
			log.info("Credenciales incorrectos");
			return InvocationResponse.ABORT;
		}
    }
}