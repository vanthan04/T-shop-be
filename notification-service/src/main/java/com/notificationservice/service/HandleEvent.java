package com.notificationservice.service;

import com.notificationservice.dto.OrderItem;
import com.notificationservice.event.consumer.payment.PaymentStatusEvent;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Service
@AllArgsConstructor
@Slf4j
public class HandleEvent {

    @Autowired
    private final JavaMailSender mailSender;

    private static final Locale VIETNAM = new Locale("vi", "VN");

    @KafkaListener(topics = "payment-success", groupId = "notificationGroup")
    public void sendEmailSuccess(PaymentStatusEvent event) throws MessagingException {
        log.info("Đã nhận sự kiện thanh toán thành công: {}", event);

        String to = event.getEmail();
        if (to == null || to.isEmpty()) {
            log.warn("Không tìm thấy email người nhận cho đơn hàng {}", event.getOrderId());
            return;
        }

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(VIETNAM);
        currencyFormat.setMaximumFractionDigits(0);
        String formattedTotal = currencyFormat.format(event.getTotalAmount());

        String htmlContent = buildSuccessEmailContent(String.valueOf(event.getOrderId()), event.getItems(), formattedTotal, currencyFormat);
        sendEmail(to, "Xác nhận thanh toán thành công", htmlContent);
    }

    @KafkaListener(topics = "payment-failed", groupId = "notificationGroup")
    public void sendEmailFailed(PaymentStatusEvent event) throws MessagingException {
        log.info("Đã nhận sự kiện thanh toán thất bại: {}", event);

        String to = event.getEmail();
        if (to == null || to.isEmpty()) {
            log.warn("Không tìm thấy email người nhận cho đơn hàng {}", event.getOrderId());
            return;
        }

        String htmlContent = buildFailedEmailContent(String.valueOf(event.getOrderId()));
        sendEmail(to, "Thanh toán thất bại - Đơn hàng " + event.getOrderId(), htmlContent);
    }

    private void sendEmail(String to, String subject, String htmlContent) throws MessagingException {
        String from = "tranvanthan170704@gmail.com";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }

    private String buildSuccessEmailContent(String orderId, List<OrderItem> items, String formattedTotal, NumberFormat currencyFormat) {
        StringBuilder itemsHtml = new StringBuilder();

        for (OrderItem item : items) {
            itemsHtml.append("<tr>")
                    .append("<td>").append(item.getProductName()).append("</td>")
                    .append("<td align='right'>").append(item.getQuantity()).append("</td>")
                    .append("<td align='right'>").append(currencyFormat.format(item.getPrice())).append("</td>")
                    .append("</tr>");
        }

        return "<p><b>Xin chào Quý khách</b>,</p>" +
                "<p>Chúng tôi xin xác nhận rằng đơn hàng <b>" + orderId + "</b> của Quý khách đã được <span style='color:green;'>thanh toán thành công</span>.</p>" +
                "<p>Dưới đây là thông tin chi tiết đơn hàng:</p>" +
                "<table border='1' cellpadding='8' cellspacing='0' style='border-collapse:collapse; width: 100%; font-family: Arial, sans-serif; font-size: 14px;'>" +
                "<thead style='background-color:#f2f2f2;'>" +
                "<tr>" +
                "<th align='left'>Tên sản phẩm</th>" +
                "<th align='right'>Số lượng</th>" +
                "<th align='right'>Đơn giá</th>" +
                "</tr>" +
                "</thead>" +
                "<tbody>" +
                itemsHtml +
                "</tbody>" +
                "</table>" +
                "<div style='margin-top: 10px; text-align: right; font-weight: bold; font-size: 16px;'>Tổng tiền: " + formattedTotal + "</div>" +
                "<br><p>Cảm ơn Quý khách đã mua sắm tại cửa hàng của chúng tôi.<br>Chúc Quý khách một ngày tốt lành!</p>";
    }

    private String buildFailedEmailContent(String orderId) {
        return "<p><b>Xin chào Quý khách</b>,</p>" +
                "<p>Chúng tôi rất tiếc vì thanh toán cho đơn hàng <b>" + orderId + "</b> đã <span style='color:red;'>không thành công</span>.</p>" +
                "<p>Nguyên nhân có thể do:</p>" +
                "<ul>" +
                "<li>Thẻ hoặc tài khoản thanh toán bị từ chối</li>" +
                "<li>Kết nối mạng không ổn định trong quá trình thanh toán</li>" +
                "<li>Hệ thống ngân hàng đang bảo trì</li>" +
                "</ul>" +
                "<p>Quý khách vui lòng thử lại hoặc chọn phương thức thanh toán khác.</p>" +
                "<p>Nếu cần hỗ trợ, xin liên hệ bộ phận chăm sóc khách hàng của chúng tôi.</p>" +
                "<br><p>Trân trọng,<br>Đội ngũ hỗ trợ</p>";
    }
}
