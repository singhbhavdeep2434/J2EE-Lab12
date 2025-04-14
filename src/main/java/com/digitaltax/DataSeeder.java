package com.digitaltax;

import com.digitaltax.model.*;
import com.digitaltax.repository.TransactionRepository;
import com.digitaltax.service.TaxComplianceService;
import org.h2.tools.Server;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;

public class DataSeeder {

    private static Server h2ConsoleServer;

    public static void main(String[] args) {
        try {
            // Start H2 Console (accessible at http://localhost:8082)
            startH2Console();

            // Initialize JPA
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("DigitalTaxPU");
            EntityManager em = emf.createEntityManager();

            try {
                seedDatabase(em);
                testServices(em);
            } finally {
                Thread.sleep(60000);
                em.close();
                emf.close();
                stopH2Console();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void seedDatabase(EntityManager em) {
        em.getTransaction().begin();
        // Clear existing data (add this)
        em.createQuery("DELETE FROM Transaction").executeUpdate();
        em.createQuery("DELETE FROM TaxOfficer").executeUpdate();
        em.createQuery("DELETE FROM Company").executeUpdate();
        em.createQuery("DELETE FROM Jurisdiction").executeUpdate();
        // Create jurisdictions
        Jurisdiction uk = new Jurisdiction();
        uk.setCode("UK");
        uk.setName("United Kingdom");
        uk.setTaxRate(0.20);
        uk.setTaxType(TaxType.VAT);
        em.persist(uk);

        Jurisdiction ca = new Jurisdiction();
        ca.setCode("CA");
        ca.setName("Canada");
        ca.setTaxRate(0.05);
        ca.setTaxType(TaxType.GST);
        em.persist(ca);

        // Create companies
        Company company1 = new Company();
        company1.setName("Digital Services Inc.");
        company1.setVatNumber("GB123456789");
        company1.setCountry("UK");
        company1.setIndustry("IT Services");
        em.persist(company1);

        Company company2 = new Company();
        company2.setName("Global Tech Ltd.");
        company2.setVatNumber("CA987654321");
        company2.setCountry("Canada");
        company2.setIndustry("Software");
        em.persist(company2);

        // Create tax officers
        TaxOfficer officer1 = new TaxOfficer();
        officer1.setName("John Smith");
        officer1.setEmail("john.smith@tax.gov");
        officer1.setRegion("EMEA");
        em.persist(officer1);

        TaxOfficer officer2 = new TaxOfficer();
        officer2.setName("Sarah Johnson");
        officer2.setEmail("sarah.johnson@tax.gov");
        officer2.setRegion("NA");
        em.persist(officer2);

        // Create transactions
        Transaction tx1 = new Transaction();
        tx1.setTimestamp(new Date());
        tx1.setAmount(new BigDecimal("1000.00"));
        tx1.setCurrency("GBP");
        tx1.setCompany(company1);
        tx1.setJurisdiction(uk);
        em.persist(tx1);

        Transaction tx2 = new Transaction();
        tx2.setTimestamp(new Date());
        tx2.setAmount(new BigDecimal("500.00"));
        tx2.setCurrency("CAD");
        tx2.setCompany(company2);
        tx2.setJurisdiction(ca);
        em.persist(tx2);

        // Properly handle bidirectional relationship
        tx1.addTaxOfficer(officer1);  // Using helper method
        tx2.addTaxOfficer(officer2);  // Using helper method

        em.getTransaction().commit();
    }

    private static void testServices(EntityManager em) {
        TransactionRepository txRepo = new TransactionRepository();
        txRepo.setEntityManager(em);

        TaxComplianceService service = new TaxComplianceService();
        service.setEntityManager(em);

        // List transactions for company1
        System.out.println("\nTransactions for company1:");
        txRepo.findTransactionsByCompany(1L).forEach(tx ->
                System.out.println(tx.getId() + ": " + tx.getAmount()));

        // Flag invalid transactions
        txRepo.flagInvalidTransactions();

        // Get flagged transactions
        System.out.println("\nFlagged transactions in UK:");
        service.getFlaggedTransactionsByJurisdiction("UK").forEach(tx ->
                System.out.println(tx.getId() + ": " + tx.getStatus()));

        // Company ranking by transaction value
        System.out.println("\nCompanies by total transaction value:");
        service.getCompaniesByTotalTransactionValue().forEach(row ->
                System.out.println(row[0] + ": " + row[1]));

        // Jurisdiction summary
        System.out.println("\nJurisdiction summary:");
        service.getJurisdictionSummary().forEach(row ->
                System.out.println(row[0] + ": " + row[1] + " transactions, " + row[2] + " tax collected"));
    }

    private static void startH2Console() throws SQLException {
        h2ConsoleServer = Server.createWebServer(
                "-web", "-webAllowOthers", "-webPort", "8082").start();
        System.out.println("H2 Console available at http://localhost:8082");
        System.out.println("JDBC URL: jdbc:h2:mem:digital_tax");
        System.out.println("Username: sa");
        System.out.println("Password: (empty)");
    }

    private static void stopH2Console() {
        if (h2ConsoleServer != null) {
            h2ConsoleServer.stop();
            System.out.println("H2 Console stopped");
        }
    }
}