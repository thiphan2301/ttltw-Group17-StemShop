package vn.edu.nlu.fit.ltwebstemshopteam22cuoiki.util;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.Properties;

public class EmailUtil {
        // xác thực email
    public static void sendVerifyEmail(String toEmail, String verifyLink) {

        final String fromEmail = "thipv52@gmail.com";
        final String password = "ibym uosw rize kbjh"; // mật khẩu ứng dụng

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail)
            );
            message.setSubject("Xác thực tài khoản StemShop");
            message.setText("Click vào link sau để xác thực tài khoản:\n" + verifyLink);

            Transport.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // gửi mật khẩu mới
    public static void sendResetPasswordEmail(String toEmail, String newPassword) {
        final String fromEmail = "thipv52@gmail.com";
        final String password = "ibym uosw rize kbjh";

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Cấp lại mật khẩu StemShop");

            message.setText(
                    "Mật khẩu mới của bạn là: " + newPassword +
                            "\nVui lòng đăng nhập và đổi mật khẩu ngay."
            );

            Transport.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // gửi email xác nhận đã gửi liên hệ
    public static void sendContactSuccessEmail(String toEmail, String fullName, String subject) {

        final String fromEmail = "thipv52@gmail.com";
        final String password = "ibym uosw rize kbjh"; // mật khẩu ứng dụng Gmail

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(toEmail)
            );

            message.setSubject("Xác nhận đã gửi liên hệ | StemShop");

            message.setText(
                    "Xin chào " + fullName + ",\n\n" +
                            "Chúng tôi đã nhận được liên hệ của bạn với chủ đề:\n" +
                            "\"" + subject + "\"\n\n" +
                            "Đội ngũ StemShop sẽ phản hồi bạn trong thời gian sớm nhất.\n\n" +
                            "Cảm ơn bạn đã quan tâm và tin tưởng StemShop \n\n" +
                            "Trân trọng,\n" +
                            "StemShop Team"
            );

            Transport.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

