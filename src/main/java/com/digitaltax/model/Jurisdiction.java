package com.digitaltax.model;
import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "jurisdictions")
public class Jurisdiction {

    @Id
    @Column(name = "code", length = 10)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(name = "tax_rate", precision = 5, scale = 2, nullable = false)
    private Double taxRate;

    @Enumerated(EnumType.STRING)
    @Column(name = "tax_type", nullable = false)
    private TaxType taxType;

    @OneToMany(mappedBy = "jurisdiction")
    private List<Transaction> transactions;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(Double taxRate) {
        this.taxRate = taxRate;
    }

    public TaxType getTaxType() {
        return taxType;
    }

    public void setTaxType(TaxType taxType) {
        this.taxType = taxType;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
// Getters and setters
}