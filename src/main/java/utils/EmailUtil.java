package utils;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.*;
import javax.mail.internet.MimeMultipart;

public class EmailUtil {

    // The exact subject of the KYC dispatch email sent by the LMS
    public static final String KYC_EMAIL_SUBJECT =
            "Action Required: Complete Your KYC for Your Vehicle Loan with Alphaware";

    // The subject of the Sanction dispatch email
    public static final String SANCTION_EMAIL_SUBJECT =
            "Congratulations! Your Loan Has Been Sanctioned – Approve to Proceed";

    // The subject of the Processing Fee / Razorpay dispatch email.
    // Actual email subject: "Requesting payment of INR 1416.00 (via Razorpay)"
    public static final String PROCESSING_FEE_EMAIL_SUBJECT =
            "Razorpay"; // matches both "via Razorpay" and "Requesting payment ... Razorpay"

    /**
     * Convenience method for Gmail — connects to imap.gmail.com.
     * NOTE: Gmail requires an App Password (NOT your regular password).
     * Generate one at https://myaccount.google.com/apppasswords
     *
     * @param username  Gmail address (e.g. user@gmail.com)
     * @param password  Gmail App Password (16 chars, e.g. "abcd efgh ijkl mnop")
     * @return The extracted KYC URL, or null if not found
     */
    public static String getKycLinkFromGmail(String username, String password) throws Exception {
        return getKycLinkFromEmail("imap.gmail.com", username, password, KYC_EMAIL_SUBJECT);
    }

    /**
     * Fetch the Sanction Letter URL from Gmail inbox.
     *
     * @param username Gmail address
     * @param password Gmail App Password
     * @return Sanction letter link
     */
    public static String getSanctionLinkFromGmail(String username, String password) throws Exception {
        return getKycLinkFromEmail("imap.gmail.com", username, password, SANCTION_EMAIL_SUBJECT);
    }

    /**
     * Fetch the Processing Fee / Razorpay payment link from Gmail (checks INBOX + Spam).
     * Matches emails whose subject contains "Razorpay" or "payment" (case-insensitive).
     * Actual email subject: "Requesting payment of INR 1416.00 (via Razorpay)"
     *
     * @param username Gmail address
     * @param password Gmail App Password
     * @return Razorpay payment link, or null if not found
     */
    public static String getProcessingFeeLinkFromGmail(String username, String password) throws Exception {
        // Try the Razorpay subject keyword first
        String link = getKycLinkFromEmail("imap.gmail.com", username, password, "Razorpay");
        if (link == null) {
            // Fallback: search by "payment" keyword
            link = getKycLinkFromEmail("imap.gmail.com", username, password, "payment");
        }
        return link;
    }

    public static String getDealerLinkFromGmail(String username, String password) throws Exception {
        // Look for an email with "Finance confirmation" in the subject
        return getKycLinkFromEmail("imap.gmail.com", username, password, "Finance confirmation");
    }

    /**
     * Connects to the IMAP server and retrieves the KYC verification link from the most recent emails.
     * Matches on the exact subject string {@link #KYC_EMAIL_SUBJECT}.
     * Searches up to 30 of the newest messages.
     *
     * @param host      IMAP host name (e.g. imap.gmail.com, imap.hostinger.com)
     * @param username  IMAP login username
     * @param password  IMAP login password (use App Password for Gmail)
     * @return The extracted KYC URL, or null if not found
     */
    public static String getKycLinkFromEmail(String host, String username, String password) throws Exception {
        return getKycLinkFromEmail(host, username, password, KYC_EMAIL_SUBJECT);
    }

    /**
     * Connects to the IMAP server and retrieves the KYC verification link from the most recent emails.
     * Matches the email whose subject contains {@code targetSubject} (case-insensitive).
     * Searches up to 30 of the newest messages.
     * Supports Gmail (imap.gmail.com), Hostinger (imap.hostinger.com), and other IMAPS providers.
     *
     * @param host          IMAP host name (e.g. imap.gmail.com, imap.hostinger.com)
     * @param username      IMAP login username
     * @param password      IMAP login password (use App Password for Gmail)
     * @param targetSubject Full or partial subject to search for (case-insensitive)
     * @return The extracted KYC URL, or null if not found
     */
    public static String getKycLinkFromEmail(String host, String username, String password,
                                              String targetSubject) throws Exception {
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");
        properties.put("mail.imap.host", host);
        properties.put("mail.imap.port", "993");
        properties.put("mail.imap.ssl.enable", "true");

        // Gmail-specific: enforce TLSv1.2+ and trust Gmail's certificate
        properties.put("mail.imap.ssl.protocols", "TLSv1.2");
        properties.put("mail.imap.ssl.trust", host);
        properties.put("mail.imap.auth.plain.disable", "false");
        properties.put("mail.imap.auth.ntlm.disable", "true");
        properties.put("mail.imap.starttls.enable", "true");

        System.out.println("[INFO] Connecting to IMAP server: " + host + " as " + username);

        Session emailSession = Session.getInstance(properties);
        Store store = emailSession.getStore("imaps");
        store.connect(host, username, password);

        String kycLink = null;
        String[] foldersToTry = {"INBOX", "[Gmail]/Spam", "[Gmail]/All Mail"};

        for (String folderName : foldersToTry) {
            try {
                Folder folder = store.getFolder(folderName);
                if (folder.exists()) {
                    folder.open(Folder.READ_ONLY);
                    int messageCount = folder.getMessageCount();
                    System.out.println("[INFO] Total messages in folder " + folderName + ": " + messageCount);
                    System.out.println("[INFO] Searching for email with subject: \"" + targetSubject + "\" in " + folderName);

                    String targetLower = targetSubject.toLowerCase();

                    // Search from newest to oldest up to 30 emails
                    for (int i = messageCount; i > Math.max(0, messageCount - 30); i--) {
                        Message message = folder.getMessage(i);
                        String subject = message.getSubject();

                        if (subject != null && subject.toLowerCase().contains(targetLower)) {
                            System.out.println("[INFO] Matched email subject: \"" + subject + "\" in folder: " + folderName);
                            String body = getTextFromMessage(message);
                            System.out.println("[DEBUG] Message body length: " + body.length());
                            System.out.println("[DEBUG] Message body snippet: " + (body.length() > 500 ? body.substring(0, 500) : body));
                            kycLink = extractUrl(body);
                            if (kycLink != null) {
                                System.out.println("[INFO] Found KYC/Payment link: " + kycLink);
                                break;
                            } else {
                                System.out.println("[WARN] Matched email has no extractable URL. Continuing search...");
                            }
                        }
                    }
                    folder.close(false);
                } else {
                    System.out.println("[WARN] Folder does not exist: " + folderName);
                }
            } catch (Exception e) {
                System.out.println("[WARN] Error reading folder " + folderName + ": " + e.getMessage());
            }
            if (kycLink != null) {
                break;
            }
        }

        if (kycLink == null) {
            System.out.println("[WARN] No KYC link found for subject: \"" + targetSubject + "\" in any folders.");
        }

        store.close();
        return kycLink;
    }

    private static String getTextFromMessage(Message message) throws Exception {
        if (message.isMimeType("text/plain")) {
            return message.getContent().toString();
        } else if (message.isMimeType("text/html")) {
            return message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            return getTextFromMimeMultipart(mimeMultipart);
        }
        return "";
    }

    private static String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws Exception {
        StringBuilder result = new StringBuilder();
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result.append(bodyPart.getContent());
            } else if (bodyPart.isMimeType("text/html")) {
                result.append(bodyPart.getContent());
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                result.append(getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent()));
            }
        }
        return result.toString();
    }

    private static String extractUrl(String text) {
        // Regex to extract standard http/https URLs
        Pattern pattern = Pattern.compile("https?://[^\\s\"'><]+");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String url = matcher.group();
            String urlLower = url.toLowerCase();
            // Filter URLs that likely match our KYC, sanction, or payment web application
            if (urlLower.contains("kyc")
                    || urlLower.contains("verification")
                    || urlLower.contains("alfinnext")
                    || urlLower.contains("rzp.io")
                    || urlLower.contains("rzp.")
                    || urlLower.contains("razorpay.com")
                    || urlLower.contains("payment-link")
                    || urlLower.contains("plink_")) {
                // Return clean URL (removing any trailing punctuation from sentences)
                while (url.endsWith(".") || url.endsWith(",") || url.endsWith(">")) {
                    url = url.substring(0, url.length() - 1);
                }
                return url;
            }
        }
        return null;
    }
}
