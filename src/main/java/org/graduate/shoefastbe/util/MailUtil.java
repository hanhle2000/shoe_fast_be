package org.graduate.shoefastbe.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.graduate.shoefastbe.entity.Order;
import org.graduate.shoefastbe.entity.Voucher;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Date;
import java.util.Properties;
@AllArgsConstructor
@Getter
@Setter
public class MailUtil {
    private static final String email = "pphuc9122002@gmail.com";

    private static final String password = "efxxykccrzktdmmv";

    public static void sendEmailOrder(Order order) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, password);
            }
        });
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(email, false));

        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(order.getEmail()));
        StringBuilder sb = new StringBuilder()
                .append("Đơn hàng từ cửa hàng ShoeFast " ).append("<br/>")
                .append("Tổng tiền: " + order.getTotal()).append("Vnd").append("<br/>")
                .append("Ngày tạo: " + order.getCreateDate()).append("<br/>")
                .append("Người nhận:" + order.getFullName()).append("<br/>")
                .append("SDT: " + order.getPhone()).append("<br/>")
                .append("Địa chỉ: " + order.getAddress()).append("<br/>")
                .append("Theo dõi trạng thái đơn hàng tại đây: ")
                        .append("http://localhost:3000/order/detail/");
        msg.setSubject("Cửa hàng giày ShoeFast thông báo");
        msg.setContent(sb.toString(), "text/html; charset=utf-8");
        msg.setSentDate(new Date());
        Transport.send(msg);
    }
    public static void sendEmail(Voucher voucher, Order order) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, password);
            }
        });
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress("phucpt.ptit.edu.vn", false));

        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(order.getEmail()));
        StringBuilder sb = new StringBuilder()
                .append("Bạn nhận được voucher giảm giá cho lần sử dụng tiếp theo: " + voucher.getCode()).append("<br/>")
                .append("Số lần sử dụng: " + voucher.getCount()).append("<br/>")
                .append("Hạn sử dụng: " + voucher.getExpireDate()).append("<br/>")
                .append("Giảm giá: " + voucher.getDiscount() + " %").append("<br/>");
        msg.setSubject("Cửa hàng giày ShoeFast thông báo");
        msg.setContent(sb.toString(), "text/html; charset=utf-8");
        msg.setSentDate(new Date());
        Transport.send(msg);
    }

    public static void sendmailForgotPassword(String receive, String password) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("pphuc9122002@gmail.com", "efxxykccrzktdmmv");
            }
        });
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(email, false));

        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receive));
        msg.setContent("New Pasword: " + password, "text/html");
        msg.setSentDate(new Date());
        StringBuilder sb = new StringBuilder()
                .append("New Pasword: " + password).append("<br/>")
                .append("Đổi mật khẩu tại đây: ")
                .append("http://localhost:3000/change-password");
        msg.setSubject("Cửa hàng giày ShoeFast thông báo");
        msg.setContent(sb.toString(), "text/html; charset=utf-8");
        msg.setSentDate(new Date());
        Transport.send(msg);
    }
}

