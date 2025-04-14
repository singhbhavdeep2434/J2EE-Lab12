package com.digitaltax.model;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TransactionTaxOfficerId implements Serializable {

    private Long transactionId;
    private Long taxOfficerId;

    public TransactionTaxOfficerId(Long transactionId, Long taxOfficerId) {
        this.transactionId = transactionId;
        this.taxOfficerId = taxOfficerId;
    }

    public TransactionTaxOfficerId() {
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getTaxOfficerId() {
        return taxOfficerId;
    }

    public void setTaxOfficerId(Long taxOfficerId) {
        this.taxOfficerId = taxOfficerId;
    }
// Constructors, getters, setters

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionTaxOfficerId that = (TransactionTaxOfficerId) o;
        return transactionId.equals(that.transactionId) &&
                taxOfficerId.equals(that.taxOfficerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId, taxOfficerId);
    }
}