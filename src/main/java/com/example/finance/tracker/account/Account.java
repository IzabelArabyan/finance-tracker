package com.example.finance.tracker.account;

import com.example.finance.tracker.user.User;
import jakarta.persistence.*;
import jakarta.persistence.Version;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @Version
    private Long version;

    protected Account() {
    }

    public Account(Long id, String name, BigDecimal balance, Instant createdAt) {
        this(id, name, balance, createdAt, null);
    }
    public Account(Long id, String name, BigDecimal balance, Instant createdAt,  User owner) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.createdAt = createdAt;
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public User getOwner() {
        return owner;
    }

    public Long getVersion() {
        return version;
    }
}