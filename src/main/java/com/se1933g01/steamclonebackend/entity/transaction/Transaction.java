package com.se1933g01.steamclonebackend.entity.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.se1933g01.steamclonebackend.entity.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author: Phan Son
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TransactionID")
    private Long transactionId;

    @Column(name = "TotalAmount")
    private BigDecimal totalAmount;

    @Column(name = "CreatedAt")
    private LocalDate createdAt;

    // ================ Relationships =============
    @ManyToOne
    @JoinColumn(name = "UserID")
    private User user;


    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL)
    private List<TransactionDetail> transactionDetail;

    //================ Getter & Setter =============
    

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
