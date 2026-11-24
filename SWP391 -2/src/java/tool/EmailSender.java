package tool;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Sends the "your account was created" email. Uses javax.mail (not
 * jakarta.mail) because that's the mail jar already registered on this
 * project's classpath (lib/lib_mail/javax.mail-1.6.2.jar) - the two are the
 * same API under a different package name, so nothing else about this class
 * changes if you later migrate the whole project to jakarta.mail.
 *
 * SMTP credentials are hardcoded here the same way DBContext hardcodes its DB
 * credentials - fill in your own values below before this will actually send
 * anything.
 *
 * If you use Gmail: you cannot use your normal Gmail password. You must:
 *  1. Turn on 2-Step Verification on the Google account you want to send from.
 *  2. Create an "App Password" at https://myaccount.google.com/apppasswords
 *  3. Put that 16-character app password in SMTP_PASSWORD below (not your real password).
 */
public class EmailSender {
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String SMTP_USERNAME = "illreturnlmao@gmail.com";
    private static final String SMTP_PASSWORD = "xndhplwcjmqpxtra";
    private static final String FROM_NAME = "COMPANY NAME Tuyển dụng";

    public static boolean sendActivationEmail(String toEmail, String toName,
            String tempPassword, String activationLink) {

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_USERNAME, SMTP_PASSWORD);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SMTP_USERNAME, FROM_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Tài khoản của bạn đã được tạo - SWP391");
            message.setContent(buildHtmlBody(toName, tempPassword, activationLink), "text/html; charset=UTF-8");

            Transport.send(message);
            return true;
        } catch (Exception e) {
            System.err.println("EmailSender.sendActivationEmail error: " + e.getMessage());
            return false;
        }
    }

    private static String buildHtmlBody(String toName, String tempPassword, String activationLink) {
        return "<div style=\"font-family:Arial,sans-serif;max-width:520px;margin:0 auto;padding:24px;\">"
            + "<h2 style=\"color:#0f172a;\">Chào " + escape(toName) + ",</h2>"
            + "<p style=\"color:#334155;font-size:15px;line-height:1.6;\">"
            + "Quản trị viên vừa tạo cho bạn một tài khoản nhân viên trên hệ thống. "
            + "Vui lòng dùng thông tin đăng nhập tạm thời bên dưới và kích hoạt tài khoản để bắt đầu sử dụng."
            + "</p>"
            + "<div style=\"background:#f1f5f9;border-radius:12px;padding:16px 20px;margin:20px 0;\">"
            + "<p style=\"margin:4px 0;color:#0f172a;\"><b>Mật khẩu tạm thời:</b> "
            + "<span style=\"font-family:monospace;font-size:16px;background:#e2e8f0;padding:2px 8px;border-radius:6px;\">"
            + escape(tempPassword) + "</span></p>"
            + "</div>"
            + "<div style=\"text-align:center;margin:28px 0;\">"
            + "<a href=\"" + activationLink + "\" "
            + "style=\"background:linear-gradient(135deg,#2563eb,#14b8a6);color:#fff;text-decoration:none;"
            + "font-weight:bold;padding:14px 32px;border-radius:10px;display:inline-block;\">"
            + "Kích hoạt tài khoản</a>"
            + "</div>"
            + "<p style=\"color:#64748b;font-size:13px;line-height:1.6;\">"
            + "Liên kết kích hoạt sẽ hết hạn sau 3 ngày và chỉ dùng được một lần. Sau khi kích hoạt, "
            + "bạn có thể đăng nhập bằng email này và mật khẩu tạm thời ở trên. "
            + "Vì lý do bảo mật, hãy đổi mật khẩu ngay sau lần đăng nhập đầu tiên."
            + "</p>"
            + "<p style=\"color:#94a3b8;font-size:12px;margin-top:24px;\">"
            + "Nếu bạn không mong đợi email này, vui lòng bỏ qua hoặc liên hệ bộ phận nhân sự."
            + "</p>"
            + "</div>";
    }

    private static String escape(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}