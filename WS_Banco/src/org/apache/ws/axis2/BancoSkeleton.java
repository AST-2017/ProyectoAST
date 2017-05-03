/**
 * BancoSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.7.4  Built on : Oct 21, 2016 (10:47:34 BST)
 */
package org.apache.ws.axis2;

import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.engine.ServiceLifeCycle;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.sql.*;
import java.util.Enumeration;

/**
 *  BancoSkeleton java skeleton for the axisService
 */
@SuppressWarnings("Duplicates")
public class BancoSkeleton implements ServiceLifeCycle{
  private final static String usuario = "ast";
  private final static String contra = "ast";
  private final static String bd = "banco";
  private final static Publish sp = new Publish();

  public void startUp(ConfigurationContext context, AxisService service) {
    String servicio = "Banco";
    String interfaceName = "en0";
    String ip="";
    NetworkInterface networkInterface;

    try {
      networkInterface = NetworkInterface.getByName(interfaceName);
      Enumeration<InetAddress> inetAddress = networkInterface.getInetAddresses();
      InetAddress currentAddress;
      while (inetAddress.hasMoreElements()) {
        currentAddress = inetAddress.nextElement();
        if (currentAddress instanceof Inet4Address && !currentAddress.isLoopbackAddress()) {
          ip = currentAddress.toString();
          break;
        }
      }
    } catch (SocketException e) {
      System.out.println(e.getMessage());
    }

    String endpoint = "http:/"+ip+":8081/axis2/services/Banco";
    sp.publish(servicio, endpoint);
  }

  public void shutDown(ConfigurationContext context, AxisService service) {
    sp.unpublish();
  }

    /**
     * Auto generated method signature
     *
     * @param pagar
     * @return pagarResponse
     */
    public org.apache.ws.axis2.PagarResponse pagar(org.apache.ws.axis2.Pagar pagar) {
      int importe = pagar.getImporte();
      int cuentaOrigen = pagar.getCuentaOrigen();
      int cuentaDestino = pagar.getCuentaDestino();
      String destinatario = pagar.getDestinatario();
      String mensaje = pagar.getMensaje();

      Connection conn = null;
      Statement stmt = null;
      ResultSet rso = null, rsd = null;
      int saldoOrigen = -1, saldoDestino = -1;
      Correo mail = new Correo();
      String contenidoMail = "";
      PagarResponse pr = new PagarResponse();

      try {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        conn = DriverManager.getConnection("jdbc:mysql://localhost/" + bd + "?" + "user=" + usuario + "&password=" + contra);
        stmt = conn.createStatement();
      } catch(Exception e){}

      try{
        String query = "select saldo from Banco where cuenta='"+cuentaOrigen+"'";
        rso = stmt.executeQuery(query);
        rso.next();
        saldoOrigen = rso.getInt("saldo");

        query = "select saldo from Banco where cuenta='" + cuentaDestino + "'";
        rsd = stmt.executeQuery(query);
        rsd.next();
        saldoDestino = rsd.getInt("saldo");

        // Comprobar si existen ambas cuentas y si la cuenta origen tiene el importe a pagar
        if (saldoOrigen == -1 || saldoDestino == -1 || importe > saldoOrigen) {
          mail.enviar(destinatario, "Se ha producido un error: La cuenta no es valida o no tiene saldo suficiente.");
          pr.set_return(false);
          return pr;
        } else {// Realizar transaccion
          conn.setAutoCommit(false);
          String update = "UPDATE banco SET saldo = saldo - '" + importe + "' where cuenta=" + cuentaOrigen;
          stmt.executeUpdate(update);
          update = "UPDATE banco SET saldo = saldo + '" + importe + "' where cuenta=" + cuentaDestino;
          stmt.executeUpdate(update);
          conn.commit();
        }
        rso.close();
        rsd.close();
        stmt.close();
        conn.close();
        mail.enviar(destinatario, mensaje);
        pr.set_return(true);
        return pr;
      } catch (SQLException e) {
        mail.enviar(destinatario, "Se ha producido un error y no se ha podido realizar el pago.");
        pr.set_return(false);
        return pr;
      }
    }
}
