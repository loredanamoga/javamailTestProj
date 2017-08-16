import com.sun.org.apache.xpath.internal.SourceTree;

import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.util.Properties;


public class MailTest {

    private String saveDirectory;

    public void mailReader(String host, String user, String pass) throws IOException, MessagingException {
        try {
//          Properties properties = System.getProperties();
            Properties properties = new Properties();
            properties.put("mail.imap.starttls.enable", "true");
            properties.put("mail.imap.host", host);
            properties.put("mail.imap.port", "465");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.required", "true");

//          java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            Session mailSession = Session.getDefaultInstance(properties);

            Store store = mailSession.getStore("imaps");
            store.connect(host, user, pass);

            Folder inboxFolder = store.getFolder("INBOX");
            inboxFolder.open(Folder.READ_ONLY);

            Message[] messages = inboxFolder.getMessages();
            System.out.println("messages.length---" + messages.length);

            for (int i = 0, n = messages.length; i < n; i++) {
                Message message = messages[i];
                System.out.println("---------------------------------");
                System.out.println("Email Number " + (i + 1));
                System.out.println("Subject: " + message.getSubject());
                System.out.println("From: " + message.getFrom()[0]);
                System.out.println("Text: " + getTextFromMessage(message));
                downloadEmailAttachments(message.getContentType(), message, saveDirectory);

                }

            //close the store and folder objects
            inboxFolder.close(true);
            store.close();

//            mailSession.setDebug(sessionDebug);
//            Message message = new MimeMessage(mailSession);
//            message.setFrom(new InternetAddress(from));
//            InternetAddress address = new InternetAddress(to);
//            message.setRecipient(Message.RecipientType.TO, address);
//            message.setSubject(subject);
//            message.setSentDate(new Date());
//            message.setText(messageText);
//
//
//            Transport transport = mailSession.getTransport("smtp");
//            transport.connect(host, user, pass);
//            transport.sendMessage(message, message.getAllRecipients());
//            transport.close();
//            System.out.println("Message sent successfully");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private static String getTextFromMessage(Message message) throws MessagingException, IOException {
        String result = "";
        if (message.isMimeType("text/plain")) {
            result = message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            result = getTextFromMimeMultipart(mimeMultipart);
        }
        return result;
    }

    private static String getTextFromMimeMultipart(
            MimeMultipart mimeMultipart) throws MessagingException, IOException {
        String result = "";
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result = result + "\n" + bodyPart.getContent();
                break; // without break same text appears twice in my tests
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                result = result + getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
            }
        }
        return result;
    }

    public static void downloadEmailAttachments(String contentType, Message message, String saveDirectory) throws MessagingException, IOException {

        String messageContent = "";

        // store attachment file name, separated by comma
        String attachFiles = "";

        if (contentType.contains("multipart")) {
            // content may contain attachments
            Multipart multiPart = (Multipart) message.getContent();
            int numberOfParts = multiPart.getCount();
            for (int partCount = 0; partCount < numberOfParts; partCount++) {
                MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                    // this part is attachment
                    String fileName = part.getFileName();
                    attachFiles += fileName + ", ";
                    part.saveFile(saveDirectory + File.separator + fileName);
                } else {
                    // this part may be the message content
                    messageContent = part.getContent().toString();
                }
            }

            if (attachFiles.length() > 1) {
                attachFiles = attachFiles.substring(0, attachFiles.length() - 2);
            }
        } else if (contentType.contains("text/plain")
                || contentType.contains("text/html")) {
            Object content = message.getContent();
            if (content != null) {
                messageContent = content.toString();
            }
        }
        System.out.println("Message: " + messageContent);
        System.out.println("Attachments: " + attachFiles);
    }


    public  void setSaveDirectory(String dir) {
        this.saveDirectory = dir;
    }


}
