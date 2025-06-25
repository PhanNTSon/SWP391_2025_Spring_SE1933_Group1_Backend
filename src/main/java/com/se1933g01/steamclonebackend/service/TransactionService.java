package com.se1933g01.steamclonebackend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.se1933g01.steamclonebackend.entity.transaction.Transaction;
import com.se1933g01.steamclonebackend.entity.transaction.TransactionDetail;
import com.se1933g01.steamclonebackend.entity.user.User;
import com.se1933g01.steamclonebackend.repository.TransactionRepo;
import com.se1933g01.steamclonebackend.repository.UserRepo;

import jakarta.persistence.EntityNotFoundException;
@Service
public class TransactionService {

    private final UserRepo userRepo;
    private final TransactionRepo transactionRepo;

    public TransactionService(UserRepo userRepo, TransactionRepo transactionRepo) {
        this.userRepo = userRepo;
        this.transactionRepo = transactionRepo;
    }
    private final String USER_NOT_FOUND_MSG = "User not found";


        /**
         * Show transactions of a user.
         * 
         * @param userId the ID of the user
         * @return List of transactions associated with the user
         * @throws EntityNotFoundException if the user is not found
         */
        public List<Transaction> getTransactions(Long userId) {
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MSG));
            return transactionRepo.findByUser(user);
        }

       /**
     * Get detailed information of a transaction for a user based on game ID.
     * 
     * @param userId the ID of the user
     * @param gameId the ID of the game in the transaction
     * @return TransactionDetail of the transaction, or null if not found
     * @throws EntityNotFoundException if the user is not found
     */
    public TransactionDetail getTransactionDetailByTransactionId(Long userId, Long transactionId) {
        List<Transaction> listTransaction = getTransactions(userId);
        for(Transaction transaction : listTransaction) {
            TransactionDetail detail = transaction.getTransactionDetail().get(0);
            if(transaction.getTransactionId().equals(transactionId)) {
                return detail;
            }
        }
        return null;
    }
}
