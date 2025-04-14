package com.digitaltax.service;
import com.digitaltax.model.TaxOfficer;
import com.digitaltax.model.Transaction;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@Transactional
public class TaxComplianceService {

    @PersistenceContext
    private EntityManager entityManager;

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    // 1. Retrieve all flagged transactions in a given jurisdiction
    public List<Transaction> getFlaggedTransactionsByJurisdiction(String jurisdictionCode) {
        return entityManager.createQuery(
                        "SELECT t FROM Transaction t " +
                                "WHERE t.status = 'FLAGGED' AND t.jurisdiction.code = :jurisdictionCode",
                        Transaction.class)
                .setParameter("jurisdictionCode", jurisdictionCode)
                .getResultList();
    }

    // 2. List companies with highest total transaction value
    public List<Object[]> getCompaniesByTotalTransactionValue() {
        return entityManager.createQuery(
                        "SELECT c.name, SUM(t.amount) as total " +
                                "FROM Company c JOIN c.transactions t " +
                                "GROUP BY c.id " +
                                "ORDER BY total DESC", Object[].class)
                .getResultList();
    }

    // 3. Fetch TaxOfficers involved in reviewing a transaction
    public List<TaxOfficer> getTaxOfficersForTransaction(Long transactionId) {
        return entityManager.createQuery(
                        "SELECT o FROM TaxOfficer o " +
                                "JOIN o.transactions t " +
                                "WHERE t.id = :transactionId", TaxOfficer.class)
                .setParameter("transactionId", transactionId)
                .getResultList();
    }

    // 4. Generate a jurisdiction summary
    public List<Object[]> getJurisdictionSummary() {
        return entityManager.createQuery(
                        "SELECT j.name, COUNT(t), SUM(t.amount * j.taxRate) " +
                                "FROM Jurisdiction j LEFT JOIN j.transactions t " +
                                "GROUP BY j.code", Object[].class)
                .getResultList();
    }


}