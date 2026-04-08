package com.se1933g01.steamclonebackend.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.se1933g01.steamclonebackend.entity.game.Game;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.MessagingException;

/**
 * @author Loc Phan
 */

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public String generateOtp() {
        return String.format("%06d", new Random().nextInt(1000000));
    }

    public void sendOtpEmail(String email, String otp, String subject) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText("Your OTP code is: " + otp + "\nIt will expire in 10 minutes.");
        mailSender.send(message);
    }

    public void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Error while sending email: " + e.getMessage());
        }
    }

    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            System.err.println("Error while sending HTML email: " + e.getMessage());
        }
    }

    public void sendRefundInvoiceEmail(String userEmail, String userId, String username,
            String gameTitle, BigDecimal gamePrice) {
        String subject = "Hoá đơn hoàn tiền - " + gameTitle;
        String htmlContent = """
                <!DOCTYPE html>
                <html lang="vi">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Hoá đơn hoàn tiền</title>
                </head>
                <body style="margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f5f5f5;">
                    <div style="max-width: 600px; margin: 0 auto; background-color: white; box-shadow: 0 0 10px rgba(0,0,0,0.1);">

                        <!-- Header -->
                        <div style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 30px; text-align: center;">
                            <h1 style="color: white; margin: 0; font-size: 28px; font-weight: 300;">
                                🎮 GAME STORE
                            </h1>
                            <p style="color: rgba(255,255,255,0.9); margin: 10px 0 0 0; font-size: 14px;">
                                Hoá đơn hoàn tiền
                            </p>
                        </div>

                        <!-- Invoice Info -->
                        <div style="padding: 30px;">
                            <div style="text-align: center; margin-bottom: 30px;">
                                <h2 style="color: #333; margin: 0 0 10px 0; font-size: 24px;">
                                    ✅ Hoàn tiền thành công
                                </h2>
                                <p style="color: #666; margin: 0; font-size: 16px;">
                                    Yêu cầu hoàn tiền của bạn đã được xử lý
                                </p>
                            </div>

                            <!-- Customer Info -->
                            <div style="background-color: #f8f9fa; padding: 20px; border-radius: 8px; margin-bottom: 25px;">
                                <h3 style="color: #495057; margin: 0 0 15px 0; font-size: 18px; border-bottom: 2px solid #dee2e6; padding-bottom: 8px;">
                                    👤 Thông tin khách hàng
                                </h3>
                                <table style="width: 100%%; border-collapse: collapse;">
                                    <tr>
                                        <td style="padding: 8px 0; color: #6c757d; font-weight: 500; width: 120px;">
                                            User ID:
                                        </td>
                                        <td style="padding: 8px 0; color: #333; font-weight: 600;">
                                            #%s
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="padding: 8px 0; color: #6c757d; font-weight: 500;">
                                            Tên tài khoản:
                                        </td>
                                        <td style="padding: 8px 0; color: #333; font-weight: 600;">
                                            %s
                                        </td>
                                    </tr>
                                </table>
                            </div>

                            <!-- Game Info -->
                            <div style="background-color: #fff3cd; border: 1px solid #ffeaa7; padding: 20px; border-radius: 8px; margin-bottom: 25px;">
                                <h3 style="color: #856404; margin: 0 0 15px 0; font-size: 18px; border-bottom: 2px solid #ffeaa7; padding-bottom: 8px;">
                                    🎯 Thông tin sản phẩm hoàn tiền
                                </h3>
                                <table style="width: 100%%; border-collapse: collapse;">
                                    <tr>
                                        <td style="padding: 8px 0; color: #856404; font-weight: 500; width: 120px;">
                                            Tên game:
                                        </td>
                                        <td style="padding: 8px 0; color: #333; font-weight: 600;">
                                            %s
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="padding: 8px 0; color: #856404; font-weight: 500;">
                                            Giá gốc:
                                        </td>
                                        <td style="padding: 8px 0; color: #333; font-weight: 600;">
                                            %,.0f $
                                        </td>
                                    </tr>
                                </table>
                            </div>

                            <!-- Refund Amount -->
                            <div style="background: linear-gradient(135deg, #d4edda 0%%, #c3e6cb 100%%); border: 1px solid #c3e6cb; padding: 25px; border-radius: 8px; text-align: center; margin-bottom: 25px;">
                                <h3 style="color: #155724; margin: 0 0 10px 0; font-size: 16px; text-transform: uppercase; letter-spacing: 1px;">
                                    💰 Số tiền hoàn trả
                                </h3>
                                <div style="font-size: 32px; font-weight: bold; color: #28a745; margin: 10px 0;">
                                    %,.0f $
                                </div>
                                <p style="color: #155724; margin: 0; font-size: 14px;">
                                    Tiền sẽ được hoàn vào tài khoản của bạn trong 3-5 ngày làm việc
                                </p>
                            </div>

                            <!-- Transaction Info -->
                            <div style="background-color: #e7f3ff; border: 1px solid #b8daff; padding: 20px; border-radius: 8px; margin-bottom: 25px;">
                                <h3 style="color: #004085; margin: 0 0 15px 0; font-size: 18px; border-bottom: 2px solid #b8daff; padding-bottom: 8px;">
                                    📋 Chi tiết giao dịch
                                </h3>
                                <table style="width: 100%%; border-collapse: collapse;">
                                    <tr>
                                        <td style="padding: 8px 0; color: #004085; font-weight: 500; width: 150px;">
                                            Mã giao dịch:
                                        </td>
                                        <td style="padding: 8px 0; color: #333; font-weight: 600; font-family: monospace;">
                                            RF-%s-%s
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="padding: 8px 0; color: #004085; font-weight: 500;">
                                            Ngày xử lý:
                                        </td>
                                        <td style="padding: 8px 0; color: #333; font-weight: 600;">
                                            %s
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="padding: 8px 0; color: #004085; font-weight: 500;">
                                            Trạng thái:
                                        </td>
                                        <td style="padding: 8px 0;">
                                            <span style="background-color: #28a745; color: white; padding: 4px 12px; border-radius: 12px; font-size: 12px; font-weight: 600;">
                                                ✓ HOÀN THÀNH
                                            </span>
                                        </td>
                                    </tr>
                                </table>
                            </div>

                            <!-- Support Info -->
                            <div style="background-color: #f8f9fa; padding: 20px; border-radius: 8px; border-left: 4px solid #6f42c1;">
                                <h4 style="color: #6f42c1; margin: 0 0 10px 0; font-size: 16px;">
                                    🛠️ Cần hỗ trợ?
                                </h4>
                                <p style="color: #666; margin: 0 0 15px 0; line-height: 1.6;">
                                    Nếu bạn có bất kỳ câu hỏi nào về giao dịch hoàn tiền này, vui lòng liên hệ với chúng tôi:
                                </p>
                                <p style="margin: 5px 0; color: #333;">
                                    📧 Email: <a href="mailto:5fcl.system@gmail.com" style="color: #6f42c1; text-decoration: none;">5fcl.system@gmail.com</a>
                                </p>
                            </div>
                        </div>

                        <!-- Footer -->
                        <div style="background-color: #343a40; color: white; padding: 25px; text-align: center;">
                            <p style="margin: 0 0 10px 0; font-size: 14px;">
                                Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi!
                            </p>
                            <p style="margin: 0; font-size: 12px; color: #adb5bd;">
                                © 2025 Centurion Store. Tất cả quyền được bảo lưu.
                            </p>
                            <p style="margin: 10px 0 0 0; font-size: 11px; color: #6c757d;">
                                Email này được gửi tự động. Vui lòng không phản hồi trực tiếp.
                            </p>
                        </div>
                    </div>
                </body>
                </html>
                """
                .formatted(
                        userId,
                        username,
                        gameTitle,
                        gamePrice,
                        gamePrice,
                        userId,
                        System.currentTimeMillis(),
                        java.time.LocalDateTime.now()
                                .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));

        sendHtmlEmail(userEmail, subject, htmlContent);
    }

    public void sendPurchaseInvoiceEmail(String userEmail, String userId, String username,
            String gameTitle, BigDecimal gamePrice) {
        String subject = "Hoá đơn mua hàng - " + gameTitle;
        String htmlContent = """
                <!DOCTYPE html>
                <html lang="vi">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Hoá đơn mua hàng</title>
                </head>
                <body style="margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f5f5f5;">
                    <div style="max-width: 600px; margin: 0 auto; background-color: white; box-shadow: 0 0 10px rgba(0,0,0,0.1);">

                        <!-- Header -->
                        <div style="background: linear-gradient(135deg, #4CAF50 0%%, #45a049 100%%); padding: 30px; text-align: center;">
                            <h1 style="color: white; margin: 0; font-size: 28px; font-weight: 300;">
                                🎮 CENTURION STORE
                            </h1>
                            <p style="color: rgba(255,255,255,0.9); margin: 10px 0 0 0; font-size: 14px;">
                                Hoá đơn mua hàng
                            </p>
                        </div>

                        <!-- Invoice Header -->
                        <div style="padding: 30px;">
                            <div style="text-align: center; margin-bottom: 30px;">
                                <h2 style="color: #333; margin: 0 0 10px 0; font-size: 24px;">
                                    🎉 Cảm ơn bạn đã mua hàng!
                                </h2>
                                <p style="color: #666; margin: 0; font-size: 16px;">
                                    Đơn hàng của bạn đã được xử lý thành công
                                </p>
                            </div>

                            <!-- Invoice Number & Date -->
                            <div style="background: linear-gradient(135deg, #e3f2fd 0%%, #bbdefb 100%%); padding: 20px; border-radius: 8px; margin-bottom: 25px; text-align: center;">
                                <div style="display: inline-block; margin: 0 20px;">
                                    <p style="margin: 0; color: #1565c0; font-size: 12px; text-transform: uppercase; letter-spacing: 1px;">
                                        Số hoá đơn
                                    </p>
                                    <p style="margin: 5px 0 0 0; color: #0d47a1; font-size: 18px; font-weight: bold; font-family: monospace;">
                                        INV-%s-%s
                                    </p>
                                </div>
                                <div style="display: inline-block; margin: 0 20px;">
                                    <p style="margin: 0; color: #1565c0; font-size: 12px; text-transform: uppercase; letter-spacing: 1px;">
                                        Ngày mua
                                    </p>
                                    <p style="margin: 5px 0 0 0; color: #0d47a1; font-size: 18px; font-weight: bold;">
                                        %s
                                    </p>
                                </div>
                            </div>

                            <!-- Customer Info -->
                            <div style="background-color: #f8f9fa; padding: 20px; border-radius: 8px; margin-bottom: 25px;">
                                <h3 style="color: #495057; margin: 0 0 15px 0; font-size: 18px; border-bottom: 2px solid #dee2e6; padding-bottom: 8px;">
                                    👤 Thông tin khách hàng
                                </h3>
                                <table style="width: 100%%; border-collapse: collapse;">
                                    <tr>
                                        <td style="padding: 8px 0; color: #6c757d; font-weight: 500; width: 120px;">
                                            User ID:
                                        </td>
                                        <td style="padding: 8px 0; color: #333; font-weight: 600;">
                                            #%s
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="padding: 8px 0; color: #6c757d; font-weight: 500;">
                                            Tên tài khoản:
                                        </td>
                                        <td style="padding: 8px 0; color: #333; font-weight: 600;">
                                            %s
                                        </td>
                                    </tr>
                                </table>
                            </div>

                            <!-- Game Purchase Details -->
                            <div style="background-color: #fff; border: 2px solid #4CAF50; border-radius: 8px; margin-bottom: 25px; overflow: hidden;">
                                <div style="background-color: #4CAF50; padding: 15px; text-align: center;">
                                    <h3 style="color: white; margin: 0; font-size: 18px;">
                                        🎯 Sản phẩm đã mua
                                    </h3>
                                </div>
                                <div style="padding: 20px;">
                                    <table style="width: 100%%; border-collapse: collapse;">
                                        <thead>
                                            <tr style="background-color: #f8f9fa;">
                                                <th style="padding: 12px; text-align: left; color: #495057; font-weight: 600; border-bottom: 2px solid #dee2e6;">
                                                    Tên Game
                                                </th>
                                                <th style="padding: 12px; text-align: right; color: #495057; font-weight: 600; border-bottom: 2px solid #dee2e6; width: 120px;">
                                                    Giá
                                                </th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr>
                                                <td style="padding: 15px 12px; color: #333; font-weight: 600; border-bottom: 1px solid #f0f0f0;">
                                                    🎮 %s
                                                </td>
                                                <td style="padding: 15px 12px; text-align: right; color: #4CAF50; font-weight: bold; font-size: 16px; border-bottom: 1px solid #f0f0f0;">
                                                    %,.0f $
                                                </td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>

                            <!-- Total Amount -->
                            <div style="background: linear-gradient(135deg, #4CAF50 0%%, #45a049 100%%); padding: 25px; border-radius: 8px; text-align: center; margin-bottom: 25px; color: white;">
                                <h3 style="margin: 0 0 10px 0; font-size: 16px; text-transform: uppercase; letter-spacing: 1px; opacity: 0.9;">
                                    💰 Tổng thanh toán
                                </h3>
                                <div style="font-size: 36px; font-weight: bold; margin: 10px 0;">
                                    %,.0f $
                                </div>
                                <p style="margin: 0; font-size: 14px; opacity: 0.9;">
                                    Đã thanh toán thành công
                                </p>
                            </div>

                            <!-- Transaction Info -->
                            <div style="background-color: #e7f3ff; border: 1px solid #b8daff; padding: 20px; border-radius: 8px; margin-bottom: 25px;">
                                <h3 style="color: #004085; margin: 0 0 15px 0; font-size: 18px; border-bottom: 2px solid #b8daff; padding-bottom: 8px;">
                                    📋 Thông tin giao dịch
                                </h3>
                                <table style="width: 100%%; border-collapse: collapse;">
                                    <tr>
                                        <td style="padding: 8px 0; color: #004085; font-weight: 500; width: 150px;">
                                            Mã giao dịch:
                                        </td>
                                        <td style="padding: 8px 0; color: #333; font-weight: 600; font-family: monospace;">
                                            TXN-%s-%s
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="padding: 8px 0; color: #004085; font-weight: 500;">
                                            Phương thức:
                                        </td>
                                        <td style="padding: 8px 0; color: #333; font-weight: 600;">
                                            Thanh toán trực tuyến
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="padding: 8px 0; color: #004085; font-weight: 500;">
                                            Trạng thái:
                                        </td>
                                        <td style="padding: 8px 0;">
                                            <span style="background-color: #28a745; color: white; padding: 4px 12px; border-radius: 12px; font-size: 12px; font-weight: 600;">
                                                ✓ THÀNH CÔNG
                                            </span>
                                        </td>
                                    </tr>
                                </table>
                            </div>

                            <!-- Download Instructions -->
                            <div style="background: linear-gradient(135deg, #ffd54f 0%%, #ffcc02 100%%); padding: 20px; border-radius: 8px; margin-bottom: 25px; text-align: center;">
                                <h3 style="color: #f57f17; margin: 0 0 15px 0; font-size: 18px;">
                                    🚀 Cách tải game
                                </h3>
                                <p style="color: #bf360c; margin: 0 0 15px 0; font-weight: 500;">
                                    Game đã được thêm vào thư viện của bạn
                                </p>
                                <div style="margin: 20px 0;">
                                    <a href="https://hoangvu.io.vn/library" style="background-color: #f57f17; color: white; padding: 12px 25px; text-decoration: none; border-radius: 25px; display: inline-block; font-weight: 600; text-transform: uppercase; letter-spacing: 1px;">
                                        🎮 Vào thư viện game
                                    </a>
                                </div>
                            </div>

                            <!-- Support Info -->
                            <div style="background-color: #f8f9fa; padding: 20px; border-radius: 8px; border-left: 4px solid #6f42c1;">
                                <h4 style="color: #6f42c1; margin: 0 0 10px 0; font-size: 16px;">
                                    🛠️ Cần hỗ trợ?
                                </h4>
                                <p style="color: #666; margin: 0 0 15px 0; line-height: 1.6;">
                                    Nếu bạn có bất kỳ câu hỏi nào về đơn hàng này hoặc cần hỗ trợ tải game:
                                </p>
                                <p style="margin: 5px 0; color: #333;">
                                    📧 Email: <a href="mailto:5fcl.system@gmail.com" style="color: #6f42c1; text-decoration: none;">5fcl.system@gmail.com</a>
                                </p>
                                <p style="margin: 5px 0; color: #333;">
                                    📞 Hotline: <strong>0372292005</strong> (8:00 - 22:00)
                                </p>
                            </div>
                        </div>

                        <!-- Footer -->
                        <div style="background-color: #343a40; color: white; padding: 25px; text-align: center;">
                            <p style="margin: 0 0 10px 0; font-size: 14px;">
                                Cảm ơn bạn đã tin tưởng và lựa chọn Centurion Store!
                            </p>
                            <p style="margin: 0 0 15px 0; font-size: 12px; color: #adb5bd;">
                                Chúc bạn có những giờ phút giải trí tuyệt vời! 🎮
                            </p>
                            <p style="margin: 0; font-size: 12px; color: #adb5bd;">
                                © 2025 Centurion Store. Tất cả quyền được bảo lưu.
                            </p>
                            <p style="margin: 10px 0 0 0; font-size: 11px; color: #6c757d;">
                                Email này được gửi tự động. Vui lòng không phản hồi trực tiếp.
                            </p>
                        </div>
                    </div>
                </body>
                </html>
                """
                .formatted(
                        userId,
                        System.currentTimeMillis(),
                        java.time.LocalDateTime.now()
                                .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        userId,
                        username,
                        gameTitle,
                        gamePrice,
                        gamePrice,
                        userId,
                        System.currentTimeMillis());

        sendHtmlEmail(userEmail, subject, htmlContent);
    }

    // Phiên bản cho nhiều game
    public void sendMultiGameInvoiceEmail(String userEmail, String userId, String username,
            List<Game> games) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Game game : games) {
            totalAmount = totalAmount.add(game.getPrice());
        }
        String subject = "Hoá đơn mua hàng - " + games.size() + " game";

        StringBuilder gameRows = new StringBuilder();
        for (Game game : games) {
            gameRows.append(String.format(
                    """
                            <tr>
                                <td style="padding: 15px 12px; color: #333; font-weight: 600; border-bottom: 1px solid #f0f0f0;">
                                    🎮 %s
                                </td>
                                <td style="padding: 15px 12px; text-align: right; color: #4CAF50; font-weight: bold; font-size: 16px; border-bottom: 1px solid #f0f0f0;">
                                    %,.0f $
                                </td>
                            </tr>
                            """,
                    game.getName(), game.getPrice()));
        }

        String htmlContent = """
                <!DOCTYPE html>
                <html lang="vi">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Hoá đơn mua hàng</title>
                </head>
                <body style="margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f5f5f5;">
                    <!-- Header giống như trên -->
                    <div style="max-width: 600px; margin: 0 auto; background-color: white; box-shadow: 0 0 10px rgba(0,0,0,0.1);">
                        <div style="background: linear-gradient(135deg, #4CAF50 0%%, #45a049 100%%); padding: 30px; text-align: center;">
                            <h1 style="color: white; margin: 0; font-size: 28px; font-weight: 300;">🎮 CENTURION STORE</h1>
                            <p style="color: rgba(255,255,255,0.9); margin: 10px 0 0 0; font-size: 14px;">Hoá đơn mua hàng</p>
                        </div>

                        <div style="padding: 30px;">
                            <!-- Customer info và game table với gameRows -->
                            <div style="background-color: #fff; border: 2px solid #4CAF50; border-radius: 8px; margin-bottom: 25px; overflow: hidden;">
                                <div style="background-color: #4CAF50; padding: 15px; text-align: center;">
                                    <h3 style="color: white; margin: 0; font-size: 18px;">🎯 Danh sách game đã mua (%d sản phẩm)</h3>
                                </div>
                                <div style="padding: 20px;">
                                    <table style="width: 100%%; border-collapse: collapse;">
                                        <thead>
                                            <tr style="background-color: #f8f9fa;">
                                                <th style="padding: 12px; text-align: left; color: #495057; font-weight: 600; border-bottom: 2px solid #dee2e6;">Tên Game</th>
                                                <th style="padding: 12px; text-align: right; color: #495057; font-weight: 600; border-bottom: 2px solid #dee2e6; width: 120px;">Giá</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            %s
                                        </tbody>
                                    </table>
                                </div>
                            </div>

                            <!-- Total amount -->
                            <div style="background: linear-gradient(135deg, #4CAF50 0%%, #45a049 100%%); padding: 25px; border-radius: 8px; text-align: center; margin-bottom: 25px; color: white;">
                                <h3 style="margin: 0 0 10px 0; font-size: 16px; text-transform: uppercase; letter-spacing: 1px; opacity: 0.9;">💰 Tổng thanh toán</h3>
                                <div style="font-size: 36px; font-weight: bold; margin: 10px 0;">%,.0f $</div>
                                <p style="margin: 0; font-size: 14px; opacity: 0.9;">Đã thanh toán thành công</p>
                            </div>
                        </div>
                    </div>
                </body>
                </html>
                """
                .formatted(games.size(), gameRows.toString(), totalAmount);

        sendHtmlEmail(userEmail, subject, htmlContent);
    }

    public void sendBalanceTopupEmail(String userEmail, String userId, String username,
            BigDecimal topupAmount) {
        String subject = "Hoá đơn nạp tiền - " + String.format("%,.0f $", topupAmount);
        String htmlContent = """
                <!DOCTYPE html>
                <html lang="vi">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Hoá đơn nạp tiền</title>
                </head>
                <body style="margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f5f5f5;">
                    <div style="max-width: 600px; margin: 0 auto; background-color: white; box-shadow: 0 0 10px rgba(0,0,0,0.1);">

                        <!-- Header -->
                        <div style="background: linear-gradient(135deg, #FF6B35 0%%, #F7931E 100%%); padding: 30px; text-align: center;">
                            <h1 style="color: white; margin: 0; font-size: 28px; font-weight: 300;">
                                💰 GAME STORE
                            </h1>
                            <p style="color: rgba(255,255,255,0.9); margin: 10px 0 0 0; font-size: 14px;">
                                Hoá đơn nạp tiền vào tài khoản
                            </p>
                        </div>

                        <!-- Success Message -->
                        <div style="padding: 30px;">
                            <div style="text-align: center; margin-bottom: 30px;">
                                <div style="background: linear-gradient(135deg, #4CAF50 0%%, #45a049 100%%); display: inline-block; padding: 15px; border-radius: 50%%; margin-bottom: 15px;">
                                    <span style="font-size: 40px;">✓</span>
                                </div>
                                <h2 style="color: #333; margin: 0 0 10px 0; font-size: 24px;">
                                    Nạp tiền thành công!
                                </h2>
                                <p style="color: #666; margin: 0; font-size: 16px;">
                                    Số tiền đã được cộng vào tài khoản của bạn
                                </p>
                            </div>

                            <!-- Receipt Number & Date -->
                            <div style="background: linear-gradient(135deg, #e8f5e8 0%%, #d4edda 100%%); padding: 20px; border-radius: 8px; margin-bottom: 25px; text-align: center;">
                                <div style="display: inline-block; margin: 0 20px;">
                                    <p style="margin: 0; color: #155724; font-size: 12px; text-transform: uppercase; letter-spacing: 1px;">
                                        Mã giao dịch
                                    </p>
                                    <p style="margin: 5px 0 0 0; color: #0f5132; font-size: 18px; font-weight: bold; font-family: monospace;">
                                        TOP-%s-%s
                                    </p>
                                </div>
                                <div style="display: inline-block; margin: 0 20px;">
                                    <p style="margin: 0; color: #155724; font-size: 12px; text-transform: uppercase; letter-spacing: 1px;">
                                        Thời gian
                                    </p>
                                    <p style="margin: 5px 0 0 0; color: #0f5132; font-size: 18px; font-weight: bold;">
                                        %s
                                    </p>
                                </div>
                            </div>

                            <!-- Customer Info -->
                            <div style="background-color: #f8f9fa; padding: 20px; border-radius: 8px; margin-bottom: 25px;">
                                <h3 style="color: #495057; margin: 0 0 15px 0; font-size: 18px; border-bottom: 2px solid #dee2e6; padding-bottom: 8px;">
                                    👤 Thông tin tài khoản
                                </h3>
                                <table style="width: 100%%; border-collapse: collapse;">
                                    <tr>
                                        <td style="padding: 8px 0; color: #6c757d; font-weight: 500; width: 120px;">
                                            User ID:
                                        </td>
                                        <td style="padding: 8px 0; color: #333; font-weight: 600;">
                                            #%s
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="padding: 8px 0; color: #6c757d; font-weight: 500;">
                                            Tên tài khoản:
                                        </td>
                                        <td style="padding: 8px 0; color: #333; font-weight: 600;">
                                            %s
                                        </td>
                                    </tr>
                                </table>
                            </div>

                            <!-- Topup Details -->
                            <div style="background: linear-gradient(135deg, #fff3cd 0%%, #ffeaa7 100%%); border: 2px solid #FF6B35; border-radius: 8px; margin-bottom: 25px; overflow: hidden;">
                                <div style="background-color: #FF6B35; padding: 15px; text-align: center;">
                                    <h3 style="color: white; margin: 0; font-size: 18px;">
                                        💳 Chi tiết nạp tiền
                                    </h3>
                                </div>
                                <div style="padding: 25px; text-align: center;">
                                    <p style="color: #856404; margin: 0 0 10px 0; font-size: 14px; text-transform: uppercase; letter-spacing: 1px;">
                                        Số tiền nạp
                                    </p>
                                    <div style="font-size: 42px; font-weight: bold; color: #FF6B35; margin: 10px 0 15px 0;">
                                        +%,.0f $
                                    </div>
                                    <p style="color: #856404; margin: 0; font-size: 14px;">
                                        Đã được cộng vào tài khoản
                                    </p>
                                </div>
                            </div>

                            <!-- Transaction Summary -->
                            <div style="background-color: #e7f3ff; border: 1px solid #b8daff; padding: 20px; border-radius: 8px; margin-bottom: 25px;">
                                <h3 style="color: #004085; margin: 0 0 15px 0; font-size: 18px; border-bottom: 2px solid #b8daff; padding-bottom: 8px;">
                                    📊 Tóm tắt giao dịch
                                </h3>
                                <table style="width: 100%%; border-collapse: collapse;">
                                    <tr>
                                        <td style="padding: 8px 0; color: #004085; font-weight: 500; width: 150px;">
                                            Loại giao dịch:
                                        </td>
                                        <td style="padding: 8px 0; color: #333; font-weight: 600;">
                                            Nạp tiền vào tài khoản
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="padding: 8px 0; color: #004085; font-weight: 500;">
                                            Phương thức:
                                        </td>
                                        <td style="padding: 8px 0; color: #333; font-weight: 600;">
                                            Thanh toán trực tuyến
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="padding: 8px 0; color: #004085; font-weight: 500;">
                                            Trạng thái:
                                        </td>
                                        <td style="padding: 8px 0;">
                                            <span style="background-color: #28a745; color: white; padding: 4px 12px; border-radius: 12px; font-size: 12px; font-weight: 600;">
                                                ✓ HOÀN THÀNH
                                            </span>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="padding: 8px 0; color: #004085; font-weight: 500;">
                                            Thời gian xử lý:
                                        </td>
                                        <td style="padding: 8px 0; color: #333; font-weight: 600;">
                                            Tức thì
                                        </td>
                                    </tr>
                                </table>
                            </div>

                            <!-- What's Next -->
                            <div style="background: linear-gradient(135deg, #6f42c1 0%%, #5a67d8 100%%); padding: 20px; border-radius: 8px; margin-bottom: 25px; text-align: center; color: white;">
                                <h3 style="margin: 0 0 15px 0; font-size: 18px;">
                                    🎮 Bây giờ bạn có thể
                                </h3>
                                <p style="margin: 0 0 20px 0; opacity: 0.9; line-height: 1.6;">
                                    Với số dư hiện tại, bạn có thể mua game, DLC hoặc các item trong game
                                </p>
                            </div>

                            <!-- Important Notes -->
                            <div style="background-color: #fff3cd; border: 1px solid #ffeaa7; padding: 20px; border-radius: 8px; margin-bottom: 25px;">
                                <h4 style="color: #856404; margin: 0 0 10px 0; font-size: 16px;">
                                    ⚠️ Lưu ý quan trọng
                                </h4>
                                <ul style="color: #856404; margin: 0; padding-left: 20px; line-height: 1.6;">
                                    <li>Số dư tài khoản không có thời hạn sử dụng</li>
                                    <li>Bạn có thể sử dụng số dư để mua bất kỳ sản phẩm nào trên store</li>
                                    <li>Lưu trữ mã giao dịch để tra cứu khi cần thiết</li>
                                    <li>Liên hệ hỗ trợ nếu có bất kỳ thắc mắc nào</li>
                                </ul>
                            </div>

                            <!-- Support Info -->
                            <div style="background-color: #f8f9fa; padding: 20px; border-radius: 8px; border-left: 4px solid #17a2b8;">
                                <h4 style="color: #17a2b8; margin: 0 0 10px 0; font-size: 16px;">
                                    🛠️ Cần hỗ trợ?
                                </h4>
                                <p style="color: #666; margin: 0 0 15px 0; line-height: 1.6;">
                                    Nếu bạn có thắc mắc về giao dịch nạp tiền hoặc cần hỗ trợ:
                                </p>
                                <p style="margin: 5px 0; color: #333;">
                                    📧 Email: <a href="mailto:5fcl.system@gmail.com" style="color: #17a2b8; text-decoration: none;">5fcl.system@gmail.com</a>
                                </p>
                                <p style="margin: 5px 0; color: #333;">
                                    📞 Hotline: <strong>0372292005</strong> (24/7)
                                </p>
                            </div>
                        </div>

                        <!-- Footer -->
                        <div style="background-color: #343a40; color: white; padding: 25px; text-align: center;">
                            <p style="margin: 0 0 10px 0; font-size: 14px;">
                                Cảm ơn bạn đã tin tưởng Game Store! 💰
                            </p>
                            <p style="margin: 0 0 15px 0; font-size: 12px; color: #adb5bd;">
                                Chúc bạn có những trải nghiệm game tuyệt vời!
                            </p>
                            <p style="margin: 0; font-size: 12px; color: #adb5bd;">
                                © 2025 Game Store. Tất cả quyền được bảo lưu.
                            </p>
                            <p style="margin: 10px 0 0 0; font-size: 11px; color: #6c757d;">
                                Email này được gửi tự động. Vui lòng không phản hồi trực tiếp.
                            </p>
                        </div>
                    </div>
                </body>
                </html>
                """
                .formatted(
                        userId,
                        System.currentTimeMillis(),
                        java.time.LocalDateTime.now()
                                .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                        userId,
                        username,
                        topupAmount);

        sendHtmlEmail(userEmail, subject, htmlContent);
    }
}
