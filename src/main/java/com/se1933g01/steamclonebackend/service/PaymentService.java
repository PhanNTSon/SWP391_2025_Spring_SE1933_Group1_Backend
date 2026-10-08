package com.se1933g01.steamclonebackend.service;

import jakarta.persistence.EntityNotFoundException;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.se1933g01.steamclonebackend.config.VNPayConfig;
import com.se1933g01.steamclonebackend.entity.user.User;
import com.se1933g01.steamclonebackend.repository.UserRepo;

@Service
public class PaymentService {

    private final UserRepo userRepo;

    public PaymentService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Transactional
    public void updateUserWallet(Long userId, BigDecimal amount) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setWalletBalance(user.getWalletBalance().add(amount));
        userRepo.save(user);
    }

    @Transactional
    public String processIpn(Map<String, String> vnp_Params) {
        final String vnp_HashSecret = "YOUR_VNPAY_HASH_SECRET";

        try {
            String vnp_SecureHash = vnp_Params.get("vnp_SecureHash");
            vnp_Params.remove("vnp_SecureHash");
            vnp_Params.remove("vnp_SecureHashType");

            String signValue = VNPayConfig.hashAllFields(vnp_Params, vnp_HashSecret);

            if (!signValue.equals(vnp_SecureHash)) {
                // RspCode: 97 - Chữ ký không hợp lệ
                return "{\"RspCode\":\"97\",\"Message\":\"Invalid Checksum\"}";
            }

            vnp_Params.get("vnp_TxnRef");
            String vnp_ResponseCode = vnp_Params.get("vnp_ResponseCode");
            // long vnp_Amount = Long.parseLong(vnp_Params.get("vnp_Amount")) / 100;

            // Logic kiểm tra nghiệp vụ
            // 1. Kiểm tra xem giao dịch (vnp_TxnRef) có tồn tại trong hệ thống của bạn
            // không
            // Transaction transaction =
            // transactionRepository.findByTxnRef(vnp_TxnRef).orElse(null);
            // if (transaction == null) {
            // return "{\"RspCode\":\"01\",\"Message\":\"Order not Found\"}";
            // }

            // 2. Kiểm tra số tiền có khớp không
            // if (transaction.getAmount() != vnp_Amount) {
            // return "{\"RspCode\":\"04\",\"Message\":\"Invalid Amount\"}";
            // }

            // 3. Kiểm tra xem giao dịch đã được xác nhận trước đó chưa
            // if (transaction.getStatus().equals("Completed") ||
            // transaction.getStatus().equals("Failed")) {
            // return "{\"RspCode\":\"02\",\"Message\":\"Order already confirmed\"}";
            // }

            // 4. Cập nhật trạng thái giao dịch và ví tiền của người dùng
            if ("00".equals(vnp_ResponseCode)) {
                // Giao dịch thành công
                // transaction.setStatus("Completed");
                // User user = transaction.getUser();
                // user.setWalletBalance(user.getWalletBalance().add(BigDecimal.valueOf(vnp_Amount)));
                // userRepository.save(user);
            } else {
                // Giao dịch thất bại
                // transaction.setStatus("Failed");
            }
            // transactionRepository.save(transaction);

            // RspCode: 00 - Xác nhận thành công
            return "{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}";

        } catch (Exception e) {
            return "{\"RspCode\":\"99\",\"Message\":\"Unknown error\"}";
        }
    } 
}
