/**
 * AeropuertoMessageReceiverInOut.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.7.4  Built on : Oct 21, 2016 (10:47:34 BST)
 */
package aeropuerto;


/**
 *  AeropuertoMessageReceiverInOut message receiver
 */
public class AeropuertoMessageReceiverInOut extends org.apache.axis2.receivers.AbstractInOutMessageReceiver {
    public void invokeBusinessLogic(
        org.apache.axis2.context.MessageContext msgContext,
        org.apache.axis2.context.MessageContext newMsgContext)
        throws org.apache.axis2.AxisFault {
        try {
            // get the implementation class for the Web Service
            Object obj = getTheImplementationObject(msgContext);

            AeropuertoSkeleton skel = (AeropuertoSkeleton) obj;

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
                if ("getInfoAeropuertoDestinoOperation".equals(methodName)) {
                    aeropuerto.GetInfoAeropuertoDestinoResponse getInfoAeropuertoDestinoResponse9 =
                        null;
                    aeropuerto.GetInfoAeropuertoDestino wrappedParam = (aeropuerto.GetInfoAeropuertoDestino) fromOM(msgContext.getEnvelope()
                                                                                                                              .getBody()
                                                                                                                              .getFirstElement(),
                            aeropuerto.GetInfoAeropuertoDestino.class);

                    getInfoAeropuertoDestinoResponse9 = skel.getInfoAeropuertoDestinoOperation(wrappedParam);

                    envelope = toEnvelope(getSOAPFactory(msgContext),
                            getInfoAeropuertoDestinoResponse9, false,
                            new javax.xml.namespace.QName("http://Aeropuerto",
                                "getInfoAeropuertoDestinoResponse"));
                } else
                 if ("getInfoAeropuertoOrigenOperation".equals(methodName)) {
                    aeropuerto.GetInfoAeropuertoOrigenResponse getInfoAeropuertoOrigenResponse11 =
                        null;
                    aeropuerto.GetInfoAeropuertoOrigen wrappedParam = (aeropuerto.GetInfoAeropuertoOrigen) fromOM(msgContext.getEnvelope()
                                                                                                                            .getBody()
                                                                                                                            .getFirstElement(),
                            aeropuerto.GetInfoAeropuertoOrigen.class);

                    getInfoAeropuertoOrigenResponse11 = skel.getInfoAeropuertoOrigenOperation(wrappedParam);

                    envelope = toEnvelope(getSOAPFactory(msgContext),
                            getInfoAeropuertoOrigenResponse11, false,
                            new javax.xml.namespace.QName("http://Aeropuerto",
                                "getInfoAeropuertoOrigenResponse"));
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
        aeropuerto.GetInfoAeropuertoDestino param, boolean optimizeContent)
        throws org.apache.axis2.AxisFault {
        try {
            return param.getOMElement(aeropuerto.GetInfoAeropuertoDestino.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.apache.axiom.om.OMElement toOM(
        aeropuerto.GetInfoAeropuertoDestinoResponse param,
        boolean optimizeContent) throws org.apache.axis2.AxisFault {
        try {
            return param.getOMElement(aeropuerto.GetInfoAeropuertoDestinoResponse.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.apache.axiom.om.OMElement toOM(
        aeropuerto.GetInfoAeropuertoOrigen param, boolean optimizeContent)
        throws org.apache.axis2.AxisFault {
        try {
            return param.getOMElement(aeropuerto.GetInfoAeropuertoOrigen.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.apache.axiom.om.OMElement toOM(
        aeropuerto.GetInfoAeropuertoOrigenResponse param,
        boolean optimizeContent) throws org.apache.axis2.AxisFault {
        try {
            return param.getOMElement(aeropuerto.GetInfoAeropuertoOrigenResponse.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(
        org.apache.axiom.soap.SOAPFactory factory,
        aeropuerto.GetInfoAeropuertoDestinoResponse param,
        boolean optimizeContent, javax.xml.namespace.QName elementQName)
        throws org.apache.axis2.AxisFault {
        try {
            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

            emptyEnvelope.getBody()
                         .addChild(param.getOMElement(
                    aeropuerto.GetInfoAeropuertoDestinoResponse.MY_QNAME,
                    factory));

            return emptyEnvelope;
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private aeropuerto.GetInfoAeropuertoDestinoResponse wrapgetInfoAeropuertoDestinoOperation() {
        aeropuerto.GetInfoAeropuertoDestinoResponse wrappedElement = new aeropuerto.GetInfoAeropuertoDestinoResponse();

        return wrappedElement;
    }

    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(
        org.apache.axiom.soap.SOAPFactory factory,
        aeropuerto.GetInfoAeropuertoOrigenResponse param,
        boolean optimizeContent, javax.xml.namespace.QName elementQName)
        throws org.apache.axis2.AxisFault {
        try {
            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

            emptyEnvelope.getBody()
                         .addChild(param.getOMElement(
                    aeropuerto.GetInfoAeropuertoOrigenResponse.MY_QNAME, factory));

            return emptyEnvelope;
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private aeropuerto.GetInfoAeropuertoOrigenResponse wrapgetInfoAeropuertoOrigenOperation() {
        aeropuerto.GetInfoAeropuertoOrigenResponse wrappedElement = new aeropuerto.GetInfoAeropuertoOrigenResponse();

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
            if (aeropuerto.GetInfoAeropuertoDestino.class.equals(type)) {
                return aeropuerto.GetInfoAeropuertoDestino.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            }

            if (aeropuerto.GetInfoAeropuertoDestinoResponse.class.equals(type)) {
                return aeropuerto.GetInfoAeropuertoDestinoResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            }

            if (aeropuerto.GetInfoAeropuertoOrigen.class.equals(type)) {
                return aeropuerto.GetInfoAeropuertoOrigen.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            }

            if (aeropuerto.GetInfoAeropuertoOrigenResponse.class.equals(type)) {
                return aeropuerto.GetInfoAeropuertoOrigenResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
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
