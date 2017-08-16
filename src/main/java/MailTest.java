import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.Properties;


public class MailTest {

    public static void main(String args[]) {
        try {
            String host = "imap.gmail.com";
            String user = "testmailevo@gmail.com";
            String pass = "qwerty1995";
            String to = "testmailevo@gmail.com";
            String from = "testmailevo@gmail.com";
            String subject = "test mail ";
            String messageText = "Winter is comming !";
            boolean sessionDebug = false;

//            Properties properties = System.getProperties();
            Properties properties = new Properties();

            properties.put("mail.imap.starttls.enable", "true");
            properties.put("mail.imap.host", host);
            properties.put("mail.imap.port", "465");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.required", "true");

//            java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
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


}
