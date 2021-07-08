package com.solinfo.email.processing;

import com.solinfo.email.model.RegistroServiceRequest;
import com.solinfo.email.utilities.Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.Base64;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.mail.*;
import javax.mail.search.FlagTerm;

public class EmailProcessor implements Tasklet, StepExecutionListener {

    private String emailProv = "";
    private String asunto = "";
    private final Util util = new Util();

    /**
     * Initialize the state of the listener with the {@link StepExecution} from
     * the current scope.
     *
     * @param stepExecution instance of {@link StepExecution}.
     */
    @Override
    public void beforeStep(StepExecution stepExecution) {

    }

    /**
     * Given the current context in the form of a step contribution, do whatever
     * is necessary to process this unit inside a transaction. Implementations
     * return {@link RepeatStatus#FINISHED} if finished. If not they return
     * {@link RepeatStatus#CONTINUABLE}. On failure throws an exception.
     *
     * @param contribution mutable state to be passed back to update the current
     *                     step execution
     * @param chunkContext attributes shared between invocations but not between
     *                     restarts
     * @return an {@link RepeatStatus} indicating whether processing is
     * continuable. Returning {@code null} is interpreted as {@link RepeatStatus#FINISHED}
     * @throws Exception thrown if error occurs during execution.
     */
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        String host = "imap.googlemail.com";
        String mailStoreType = "pop3";
        String username = "email@gmail.com";
        String password = "******";

        fetch(host, mailStoreType, username, password);

        return RepeatStatus.FINISHED;

    }

    /**
     * Give a listener a chance to modify the exit status from a step. The value
     * returned will be combined with the normal exit status using
     * {@link ExitStatus#and(ExitStatus)}.
     * <p>
     * Called after execution of step's processing logic (both successful or
     * failed). Throwing exception in this method has no effect, it will only be
     * logged.
     *
     * @param stepExecution {@link StepExecution} instance.
     * @return an {@link ExitStatus} to combine with the normal value. Return
     * {@code null} to leave the old value unchanged.
     */
    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return ExitStatus.COMPLETED;
    }

    public void fetch(String pop3Host, String storeType, String user, String password) {
        try {
            Session session = Session.getDefaultInstance(new Properties());
            Store store = session.getStore("imaps");
            store.connect("imap.googlemail.com", 993, user, password);
            Folder inbox = store.getFolder( "INBOX" );
            inbox.open( Folder.READ_WRITE );

            // retrieve the unread messages from the input folder
            Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));

            for (int i = 0; i < messages.length; i++) {
                Message message = messages[i];
                emailProv = ""; asunto = "";
                writePart(message);

            }

            inbox.close(false);
            store.close();

        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writePart(Part p) throws Exception {

        RegistroServiceRequest registroServiceRequest = new RegistroServiceRequest();
        String nitSender = null, nitReciever = null, billDate = null,
                billNumber = null, url = "", contract = null, nomArchivo = null,
                nitDigitSender = null, nitDigitReciever = null;

        if (p instanceof Message){

            Message m = (Message) p;
            Address[] a;

            // FROM
            if ((a = m.getFrom()) != null) {
                for (Address address : a) {
                    emailProv += address.toString();
                }
            }
            // TO
            if ((a = m.getRecipients(Message.RecipientType.TO)) != null) {
                for (int j = 0; j < a.length; j++)
                    System.out.println("TO: " + a[j].toString());
            }
            // SUBJECT
            if (m.getSubject() != null){
                asunto += m.getSubject();
            }

        }

        if (p.isMimeType("multipart/*")) {

            Multipart mp = (Multipart) p.getContent();
            int count = mp.getCount();

            for (int j = 0; j < count; j++)
                writePart(mp.getBodyPart(j));

        } else {
            Object o = p.getContent();

            if (o instanceof InputStream) {

                InputStream is = (InputStream) o;

                ZipInputStream stream = new ZipInputStream(is);
                Base64.Encoder encoder = Base64.getEncoder();

                boolean d = false, x = false;

                try {
                    ZipEntry entry = stream.getNextEntry();
                    if(entry != null){
                        try {
                            while (entry != null) {

                                ByteArrayOutputStream out = new ByteArrayOutputStream();
                                byte[] buf = new byte[10240];
                                int n;

                                while ((n = stream.read(buf, 0, buf.length)) != -1) {
                                    out.write(buf, 0, n);
                                }
                                out.flush();
                                byte[] stByte =  out.toByteArray();

                                if(entry.getName().contains(".pdf") && !d){
                                    System.out.println();System.out.println();System.out.println();
                                    System.out.println(entry.getName());
                                    registroServiceRequest.setFacturaB64(encoder.encodeToString(stByte));
                                    d = true;
                                }

                                if(entry.getName().contains(".xml") && !x){

                                    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
                                    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
                                    Document document = docBuilder.parse(new ByteArrayInputStream(stByte));

                                    registroServiceRequest.setFuenteXml(encoder.encodeToString(stByte));

                                    Element root = document.getDocumentElement();

                                    NodeList sender = root.getElementsByTagName("cac:SenderParty");
                                    Node send = sender.item(0);
                                    Element nodeSender = (Element) send;

                                    NodeList reciever = root.getElementsByTagName("cac:ReceiverParty");
                                    Node recieve = reciever.item(0);
                                    Element nodeReciever = (Element) recieve;

                                    nitSender = util.getElementXml("cbc:CompanyID",nodeSender);
                                    nitReciever = util.getElementXml("cbc:CompanyID",nodeReciever);

                                    nitDigitReciever = util.getAttributeElementXml("cbc:CompanyID", nodeReciever, "schemeID");
                                    nitDigitSender = util.getAttributeElementXml("cbc:CompanyID", nodeSender, "schemeID");

                                    billDate = util.getElementXml("cbc:IssueDate",root);
                                    billNumber = util.getElementXml("cbc:ParentDocumentID",root);

                                    NodeList attachment = root.getElementsByTagName("cac:Attachment");
                                    Node externalReference = attachment.item(0);
                                    Element nodeExternalRef = (Element) externalReference;

                                    String data = util.getCharacterDataFromElement("cbc:Description",nodeExternalRef);

                                    docBuilder = docBuilderFactory.newDocumentBuilder();
                                    Document cDataDocument = docBuilder.parse(new InputSource(new StringReader(data.trim())));
                                    Element cDataElement = cDataDocument.getDocumentElement();

                                    url = util.getElementXml("sts:QRCode",cDataElement);

//                                  url = qrCode.split("CUFE")[1];

                                    nomArchivo = entry.getName();
                                    x = true;
                                }
                                stream.closeEntry();
                                entry = stream.getNextEntry();
                            }

                        }catch (IOException e){
                            e.printStackTrace();
                        }

                        if(d && x){
                            registroServiceRequest.setEstadoLectura(0);
                        }else if(d){//Solo pdf
                            registroServiceRequest.setEstadoLectura(1);
                        }else if(x){//Solo xml
                            registroServiceRequest.setEstadoLectura(2);
                        }else{//Ninguno de los dos
                            registroServiceRequest.setEstadoLectura(3);
                        }

                        registroServiceRequest.setNitProveedor(nitSender.concat(nitDigitSender));
                        registroServiceRequest.setNitCompania(nitReciever.concat(nitDigitReciever));
                        registroServiceRequest.setFechaFactura(billDate);
                        registroServiceRequest.setNumFactura(billNumber);
                        registroServiceRequest.setCufe(url.replace("\n"," ").replace("\r"," "));
                        registroServiceRequest.setUrlApi("https://url.com");
                        registroServiceRequest.setNombreArchivo(nomArchivo);

                        String[] values= StringUtils.substringsBetween(emailProv,"<",">");
                        emailProv = "";
                        for (String val : values){
                            emailProv += val.concat(" ");
                        }

                        registroServiceRequest.setEmailProveedor(emailProv);
                        registroServiceRequest.setAsunto(asunto);

                        util.POSTRequest(registroServiceRequest);

                    }


                }catch (Exception e){
                    e.printStackTrace();
                }finally
                {
                    stream.close();
                }
            }
        }

    }


}
