package com.se1933g01.steamclonebackend.controller;

import com.se1933g01.steamclonebackend.config.VNPayConfig;
import com.se1933g01.steamclonebackend.entity.user.CustomUserDetail;
import com.se1933g01.steamclonebackend.service.PaymentService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Value("${vnpay.tmnCode}")
    private String tmnCode;

    @Value("${vnpay.hashSecret}")
    private String hashSecret;

    @Value("${vnpay.url}")
    private String vnpayUrl;

    @Value("${vnpay.returnUrl}")
    private String returnUrl;

    @GetMapping("/vnpay-ipn")
    @PreAuthorize("permitAll()")
    public ResponseEntity<String> handleIpn(
            @RequestParam Map<String, String> allParams) {

        logger.info("IPN call received with params: {}", allParams);
        String result = paymentService.processIpn(allParams);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/create-vnpay-payment")
    public ResponseEntity<?> createPayment(
            // THAY ĐỔI 1: Thêm các RequestParam tùy chọn
            @RequestParam("amount") long amount,
            @RequestParam(required = false) String bankCode,
            @RequestParam(required = false, defaultValue = "vn") String language,
            @AuthenticationPrincipal CustomUserDetail principal,
            HttpServletRequest request) throws UnsupportedEncodingException {

        String vnp_TxnRef = String.valueOf(System.currentTimeMillis());
        long amountInVND = amount * 100;
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", tmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amountInVND));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);

        // THAY ĐỔI 2: Tạo thông tin đơn hàng động
        String orderInfo = "Nap tien cho user " + principal.getUser().getUserId() + " - Ma GD: " + vnp_TxnRef;
        vnp_Params.put("vnp_OrderInfo", orderInfo);

        vnp_Params.put("vnp_OrderType", "other");

        if (language != null && !language.isEmpty()) {
            vnp_Params.put("vnp_Locale", language);
        } else {
            vnp_Params.put("vnp_Locale", "vn");
        }

        vnp_Params.put("vnp_ReturnUrl", returnUrl);
        vnp_Params.put("vnp_IpAddr", request.getRemoteAddr()); // Get client IP address from request

        if (bankCode != null && !bankCode.isEmpty()) {
            vnp_Params.put("vnp_BankCode", bankCode);
        }

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        // Phần tạo hash và query giữ nguyên
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                // Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                // Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayConfig.hmacSHA512(hashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = vnpayUrl + "?" + queryUrl;

        return ResponseEntity.ok(Map.of("paymentUrl", paymentUrl));
    }
}