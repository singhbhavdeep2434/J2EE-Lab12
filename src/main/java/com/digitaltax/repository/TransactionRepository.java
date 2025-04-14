package com.digitaltax.repository;
import com.digitaltax.model.Transaction;
import com.digitaltax.model.TransactionStatus;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Transactional
public class TransactionRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Transaction createTransaction(Transaction transaction) {
        entityManager.persist(transaction);
        return transaction;
    }

    public List<Transaction> findTransactionsByCompany(Long companyId) {
        TypedQuery<Transaction> query = entityManager.createQuery(
                "SELECT t FROM Transaction t WHERE t.company.id = :companyId", Transaction.class);
        query.setParameter("companyId", companyId);
        return query.getResultList();
    }

    public void flagInvalidTransactions() {
        // Flag transactions where calculated tax doesn't match expected tax
        List<Transaction> transactions = entityManager.createQuery(
                        "SELECT t FROM Transaction t WHERE t.status = 'PENDING'", Transaction.class)
                .getResultList();

        for (Transaction t : transactions) {
            BigDecimal expectedTax = t.getAmount().multiply(
                    BigDecimal.valueOf(t.getJurisdiction().getTaxRate()));
            BigDecimal calculatedTax = t.getCalculatedTax();

            if (calculatedTax == null ||
                    calculatedTax.compareTo(expectedTax) != 0) {
                t.setStatus(TransactionStatus.FLAGGED);
                entityManager.merge(t);
            } else {
                t.setStatus(TransactionStatus.COMPLIANT);
                entityManager.merge(t);
            }
        }
    }
}