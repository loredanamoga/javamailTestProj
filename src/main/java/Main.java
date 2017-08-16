import javax.mail.MessagingException;
import java.io.IOException;

/**
 * Created by loredanamoga on 8/16/2017.
 */
public class Main {
    public static void main(String args[]) throws IOException, MessagingException {


        MailTest mailTest = new MailTest();
        String saveDirectory = "E:/Attachment";
        String host = "imap.gmail.com";
        String user = "testmailevo@gmail.com";
        String pass = "qwerty1995";
        String to = "testmailevo@gmail.com";
        String from = "testmailevo@gmail.com";
        String subject = "test mail ";
        String messageText = "Winter is comming !";
        boolean sessionDebug = false;

        mailTest.setSaveDirectory(saveDirectory);
        mailTest.mailReader(host, user, pass);


    }
}
