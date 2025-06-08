package com.se1933g01.steam_clone_backend.entity.transaction;

import java.time.LocalDate;
import java.util.List;

import com.se1933g01.steam_clone_backend.entity.game.Game;
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

    @ManyToOne
    @JoinColumn(name = "GameID")
    private Game game;

    @OneToMany(mappedBy = "transaction")
    private List<TransactionDetail> transactionDetail;

    //================ Getter & Setter =============
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
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

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public List<TransactionDetail> getTransactionDetail() {
        return transactionDetail;
    }

    public void setTransactionDetail(List<TransactionDetail> transactionDetail) {
        this.transactionDetail = transactionDetail;
    }

}