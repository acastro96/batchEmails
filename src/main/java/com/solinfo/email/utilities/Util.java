package com.solinfo.email.utilities;

import com.solinfo.email.model.RegistroServiceRequest;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Util {

    public String getElementXml(String tagName, Element element) {
        NodeList list = element.getElementsByTagName(tagName);
        if (list != null && list.getLength() > 0) {
            NodeList subList = list.item(0).getChildNodes();

            if (subList != null && subList.getLength() > 0) {
                return subList.item(0).getNodeValue();
            }
        }

        return null;
    }

    public String getAttributeElementXml(String tagName, Element element, String nameAttribute) {

        NodeList list = element.getElementsByTagName(tagName);
        if (list != null && list.getLength() > 0) {
            NodeList subList = list.item(0).getChildNodes();

            if (subList != null && subList.getLength() > 0) {
                Element parent = (Element) subList.item(0).getParentNode();
                return parent.getAttribute(nameAttribute);
            }
        }

        return null;
    }

    public String getCharacterDataFromElement(String tagName, Element e) {

        NodeList list = e.getElementsByTagName(tagName);
        String data;
        if (list != null && list.getLength() > 0) {
            NodeList subList = list.item(0).getChildNodes();
            if (subList != null && subList.getLength() > 0) {

                if(subList.item(0) instanceof CharacterData){
                    CharacterData child = (CharacterData) subList.item(0);
                    data = child.getData();

                    if(data != null && data.trim().length() > 0)
                        return child.getData();
                }
            }
        }
        return "";
    }

    public void POSTRequest(RegistroServiceRequest registroServiceRequest) throws IOException {

        String body = "{\n" +
                "\"nit_compania\": \""+ (registroServiceRequest.getNitCompania()!=null?registroServiceRequest.getNitCompania():"") +"\",\r\n" +
                "\"nit_proveedor\": \""+ (registroServiceRequest.getNitProveedor()!=null?registroServiceRequest.getNitProveedor():"") +"\",\r\n" +
                "\"nFactura\": \""+ (registroServiceRequest.getNumFactura()!=null?registroServiceRequest.getNumFactura():"") +"\",\r\n" +
                "\"fecha\": \""+ (registroServiceRequest.getFechaFactura()!=null?registroServiceRequest.getFechaFactura():"") +"\",\r\n" +
                "\"factura_base64\": \""+ (registroServiceRequest.getFacturaB64()!=null?registroServiceRequest.getFacturaB64():"") +"\",\r\n" +
                "\"contrato\": \""+ (registroServiceRequest.getContrato()!=null?registroServiceRequest.getContrato():"") +"\",\r\n" +
                "\"email_proveedor\": \""+ (registroServiceRequest.getEmailProveedor()!=null?registroServiceRequest.getEmailProveedor():"") +"\",\r\n" +
                "\"asunto\": \""+ (registroServiceRequest.getAsunto()!=null?registroServiceRequest.getAsunto():"") +"\",\r\n" +
                "\"cufe\": \""+ (registroServiceRequest.getCufe()!=null?registroServiceRequest.getCufe():"") +"\",\r\n" +
                "\"url_api\": \""+ (registroServiceRequest.getUrlApi()!=null?registroServiceRequest.getUrlApi():"") +"\",\r\n" +
                "\"seqno\": \""+ (registroServiceRequest.getNumeroSecuencia()!=0?registroServiceRequest.getNumeroSecuencia():"0") +"\",\r\n" +
                "\"nombreArchivo\": \""+ (registroServiceRequest.getNombreArchivo()!=null?registroServiceRequest.getNombreArchivo():"") +"\",\r\n" +
                "\"estado_lectura\": "+ (registroServiceRequest.getEstadoLectura()!=0?registroServiceRequest.getEstadoLectura():"0") +",\r\n" +
                "\"fuente_xml\": \""+ (registroServiceRequest.getFuenteXml()!=null?registroServiceRequest.getFuenteXml():"") +"\"\r" +
                "\n}";


        System.out.println(body);
        URL obj = new URL("http://aprper.siifweb.com:9090/siifweb/factura_electronica/servicio_registro");
        HttpURLConnection postConnection = (HttpURLConnection) obj.openConnection();
        postConnection.setRequestMethod("POST");
        postConnection.setRequestProperty("Content-Type", "application/json");


        postConnection.setDoOutput(true);
        OutputStream os = postConnection.getOutputStream();
        os.write(body.getBytes());
        os.flush();
        os.close();


        int responseCode = postConnection.getResponseCode();
        System.out.println("POST Response Code :  " + responseCode);
        System.out.println("POST Response Message : " + postConnection.getResponseMessage());

        if (responseCode == HttpURLConnection.HTTP_OK) { //success
            BufferedReader in = new BufferedReader(new InputStreamReader(postConnection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in .readLine()) != null) {
                response.append(inputLine);
            } in .close();

            // print result
            System.out.println(response.toString());
        } else {
            System.out.println("POST NOT WORKED");
        }
    }

}
