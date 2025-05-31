package com.se1933g01.steam_clone_backend.entity.transaction;

import java.time.LocalDate;
import java.util.List;

import com.se1933g01.steam_clone_backend.entity.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Transaction {
    @Id
    @Column(name = "TransactionID")
    private Long transactionId;

    @Column(name = "TotalAmount")
    private Double totalAmount;

    @Column(name = "CreatedAt")
    private LocalDate createdAt;

    // ================ Relationships =============
    @ManyToOne
    @JoinColumn(name = "UserID")
    private User user;

    @OneToMany(mappedBy = "transaction")
    private List<TransactionDetail> transactionDetail;

    //================ Getter & Setter =============
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

}
