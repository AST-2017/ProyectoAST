/**
 * OrquestadorMessageReceiverInOut.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.7.4  Built on : Oct 21, 2016 (10:47:34 BST)
 */
package orquestador;


/**
 *  OrquestadorMessageReceiverInOut message receiver
 */
public class OrquestadorMessageReceiverInOut extends org.apache.axis2.receivers.AbstractInOutMessageReceiver {
    public void invokeBusinessLogic(
        org.apache.axis2.context.MessageContext msgContext,
        org.apache.axis2.context.MessageContext newMsgContext)
        throws org.apache.axis2.AxisFault {
        try {
            // get the implementation class for the Web Service
            Object obj = getTheImplementationObject(msgContext);

            OrquestadorSkeleton skel = (OrquestadorSkeleton) obj;

            //Out Envelop
            org.apache.axiom.soap.SOAPEnvelope envelope = null;

            //Find the axisOperation that has been set by the Dispatch phase.
            org.apache.axis2.description.AxisOperation op = msgContext.getOperationContext()
                                                                      .getAxisOperation();

            if (op == null) {
                throw new org.apache.axis2.AxisFault(
                    "Operation is not located, if this is doclit style the SOAP-ACTION should specified via the SOAP Action to use the RawXMLProvider");
            }

            java.lang.String methodName;

            if ((op.getName() != null) &&
                    ((methodName = org.apache.axis2.util.JavaUtils.xmlNameToJavaIdentifier(
                            op.getName().getLocalPart())) != null)) {
                if ("comprarBillete".equals(methodName)) {
                    orquestador.ComprarBilleteResponse comprarBilleteResponse9 = null;
                    orquestador.ComprarBillete wrappedParam = (orquestador.ComprarBillete) fromOM(msgContext.getEnvelope()
                                                                                                            .getBody()
                                                                                                            .getFirstElement(),
                            orquestador.ComprarBillete.class);

                    comprarBilleteResponse9 = skel.comprarBillete(wrappedParam);

                    envelope = toEnvelope(getSOAPFactory(msgContext),
                            comprarBilleteResponse9, false,
                            new javax.xml.namespace.QName(
                                "http://Orquestador", "comprarBilleteResponse"));
                } else
                 if ("obtenerOfertas".equals(methodName)) {
                    orquestador.ObtenerOfertasResponse obtenerOfertasResponse11 = null;
                    orquestador.ObtenerOfertas wrappedParam = (orquestador.ObtenerOfertas) fromOM(msgContext.getEnvelope()
                                                                                                            .getBody()
                                                                                                            .getFirstElement(),
                            orquestador.ObtenerOfertas.class);

                    obtenerOfertasResponse11 = skel.obtenerOfertas(wrappedParam);

                    envelope = toEnvelope(getSOAPFactory(msgContext),
                            obtenerOfertasResponse11, false,
                            new javax.xml.namespace.QName(
                                "http://Orquestador", "obtenerOfertasResponse"));
                } else {
                    throw new java.lang.RuntimeException("method not found");
                }

                newMsgContext.setEnvelope(envelope);
            }
        } catch (java.lang.Exception e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    //
    private org.apache.axiom.om.OMElement toOM(
        orquestador.ComprarBillete param, boolean optimizeContent)
        throws org.apache.axis2.AxisFault {
        try {
            return param.getOMElement(orquestador.ComprarBillete.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.apache.axiom.om.OMElement toOM(
        orquestador.ComprarBilleteResponse param, boolean optimizeContent)
        throws org.apache.axis2.AxisFault {
        try {
            return param.getOMElement(orquestador.ComprarBilleteResponse.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.apache.axiom.om.OMElement toOM(
        orquestador.ObtenerOfertas param, boolean optimizeContent)
        throws org.apache.axis2.AxisFault {
        try {
            return param.getOMElement(orquestador.ObtenerOfertas.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.apache.axiom.om.OMElement toOM(
        orquestador.ObtenerOfertasResponse param, boolean optimizeContent)
        throws org.apache.axis2.AxisFault {
        try {
            return param.getOMElement(orquestador.ObtenerOfertasResponse.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(
        org.apache.axiom.soap.SOAPFactory factory,
        orquestador.ComprarBilleteResponse param, boolean optimizeContent,
        javax.xml.namespace.QName elementQName)
        throws org.apache.axis2.AxisFault {
        try {
            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

            emptyEnvelope.getBody()
                         .addChild(param.getOMElement(
                    orquestador.ComprarBilleteResponse.MY_QNAME, factory));

            return emptyEnvelope;
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private orquestador.ComprarBilleteResponse wrapcomprarBillete() {
        orquestador.ComprarBilleteResponse wrappedElement = new orquestador.ComprarBilleteResponse();

        return wrappedElement;
    }

    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(
        org.apache.axiom.soap.SOAPFactory factory,
        orquestador.ObtenerOfertasResponse param, boolean optimizeContent,
        javax.xml.namespace.QName elementQName)
        throws org.apache.axis2.AxisFault {
        try {
            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

            emptyEnvelope.getBody()
                         .addChild(param.getOMElement(
                    orquestador.ObtenerOfertasResponse.MY_QNAME, factory));

            return emptyEnvelope;
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private orquestador.ObtenerOfertasResponse wrapobtenerOfertas() {
        orquestador.ObtenerOfertasResponse wrappedElement = new orquestador.ObtenerOfertasResponse();

        return wrappedElement;
    }

    /**
     *  get the default envelope
     */
    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(
        org.apache.axiom.soap.SOAPFactory factory) {
        return factory.getDefaultEnvelope();
    }

    private java.lang.Object fromOM(org.apache.axiom.om.OMElement param,
        java.lang.Class type) throws org.apache.axis2.AxisFault {
        try {
            if (orquestador.ComprarBillete.class.equals(type)) {
                return orquestador.ComprarBillete.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            }

            if (orquestador.ComprarBilleteResponse.class.equals(type)) {
                return orquestador.ComprarBilleteResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            }

            if (orquestador.ObtenerOfertas.class.equals(type)) {
                return orquestador.ObtenerOfertas.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            }

            if (orquestador.ObtenerOfertasResponse.class.equals(type)) {
                return orquestador.ObtenerOfertasResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            }
        } catch (java.lang.Exception e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }

        return null;
    }

    private org.apache.axis2.AxisFault createAxisFault(java.lang.Exception e) {
        org.apache.axis2.AxisFault f;
        Throwable cause = e.getCause();

        if (cause != null) {
            f = new org.apache.axis2.AxisFault(e.getMessage(), cause);
        } else {
            f = new org.apache.axis2.AxisFault(e.getMessage());
        }

        return f;
    }
} //end of class
