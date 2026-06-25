package tests;
import java.util.Properties;
import javax.mail.*;

public class CheckEmails {
    public static void main(String[] args) throws Exception {
        String host = "imap.gmail.com";
        String username = "bharatteja09@gmail.com";
        String password = "oxtlolbsglttfqde";

        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");
        properties.put("mail.imap.host", host);
        properties.put("mail.imap.port", "993");
        properties.put("mail.imap.ssl.enable", "true");
        properties.put("mail.imap.ssl.protocols", "TLSv1.2");
        properties.put("mail.imap.ssl.trust", host);

        Session session = Session.getInstance(properties);
        Store store = session.getStore("imaps");
        store.connect(host, username, password);

        String[] foldersToTry = {"INBOX", "[Gmail]/Spam", "[Gmail]/All Mail"};
        for (String fName : foldersToTry) {
            Folder folder = store.getFolder(fName);
            if (!folder.exists()) continue;
            folder.open(Folder.READ_ONLY);
            int count = folder.getMessageCount();
            System.out.println("Folder: " + fName + " Count: " + count);
            for (int i = count; i > Math.max(0, count - 5); i--) {
                Message message = folder.getMessage(i);
                System.out.println("   - " + message.getSubject() + " (Date: " + message.getReceivedDate() + ")");
            }
            folder.close(false);
        }
        store.close();
    }
}
