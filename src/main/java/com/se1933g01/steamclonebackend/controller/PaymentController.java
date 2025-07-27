package com.se1933g01.steamclonebackend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.se1933g01.steamclonebackend.config.VNPayConfig;
import com.se1933g01.steamclonebackend.entity.user.CustomUserDetail;
import com.se1933g01.steamclonebackend.service.PaymentService;

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
            @RequestParam("amount") double amountUsd, // THÊM 2: Nhận số tiền là USD, có thể là số lẻ
            @RequestParam(required = false) String bankCode,
            @RequestParam(required = false, defaultValue = "vn") String language,
            @AuthenticationPrincipal CustomUserDetail principal,
            HttpServletRequest request) throws UnsupportedEncodingException {

        // THÊM 3: Logic chuyển đổi ngoại tệ
        // Using a fixed exchange rate of 24,500 VND per USD
        long amountVND = (long) (amountUsd * 24500);

        String vnp_TxnRef = String.valueOf(System.currentTimeMillis());
        long amountForVNPay = amountVND * 100; // VNPay yêu cầu số tiền * 100

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", tmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amountForVNPay));
        vnp_Params.put("vnp_CurrCode", "VND"); // Luôn gửi đi là VND
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo",
                "Nap " + amountUsd + " USD vao vi cho user: " + principal.getUser().getUserId());
        // ... các tham số khác giữ nguyên ...
        vnp_Params.put("vnp_OrderType", "other");
        if (language != null && !language.isEmpty()) {
            vnp_Params.put("vnp_Locale", language);
        } else {
            vnp_Params.put("vnp_Locale", "vn");
        }
        vnp_Params.put("vnp_ReturnUrl", returnUrl);
        vnp_Params.put("vnp_IpAddr", "127.0.0.1");
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
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
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

        logger.info("Generated VNPay URL for {} USD ({} VND): {}", amountUsd, amountVND, paymentUrl);

        return ResponseEntity.ok(Map.of("paymentUrl", paymentUrl));
    }
}