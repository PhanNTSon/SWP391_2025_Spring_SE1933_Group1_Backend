package com.se1933g01.steamclonebackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.se1933g01.steamclonebackend.entity.transaction.Transaction;
import com.se1933g01.steamclonebackend.entity.user.User;

import java.util.List;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUser(User user);
    int countByUser(User user);
}
