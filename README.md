# Group Members: Bhavdep Singh, Sourav Modak, Hitesh Jha, Abdul Mubeen

# Cross-Border Digital Tax Compliance System

A JPA/Hibernate application for managing international digital tax transactions.

## 🛠️ Technologies
- Java 11+
- JPA 2.2
- Hibernate 5.6
- H2 Database (Embedded)
- Maven

## 📦 Setup

### Prerequisites
1. **JDK 21** ([Download](https://adoptium.net/))
2. **Maven** ([Install Guide](https://maven.apache.org/install.html))
3. **IntelliJ IDEA** (Recommended) or Eclipse

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/singhbhavdeep2434/J2EE-Lab12

   cd J2EE-Lab12





Open project in IntelliJ/Eclipse
Run com.digitaltax.DataSeeder as a Java application

  

Business Logic Explanation
Tax Calculation
@Transient
private BigDecimal calculatedTax;

@PostLoad
private void calculateTax() {
    this.calculatedTax = amount.multiply(BigDecimal.valueOf(jurisdiction.getTaxRate()));
}
•	Formula: amount × taxRate
•	Example: £1000 × 0.20 (UK VAT) = £200 tax

Transaction Flagging
public void flagInvalidTransactions() {
    List<Transaction> pendingTransactions = // JPQL query
    for (Transaction t : pendingTransactions) {
        BigDecimal expectedTax = t.getAmount() * t.getJurisdiction().getTaxRate();
        if (!t.getCalculatedTax().equals(expectedTax)) {
            t.setStatus(TransactionStatus.FLAGGED);
        }
    }
}
•	Rule: Transactions are flagged if calculated tax ≠ expected tax.
•	Status Flow: PENDING → COMPLIANT or FLAGGED.


Audit Metadata
@Embedded
private TaxMetadata taxMetadata;

@PrePersist
private void setCreatedMetadata() {
    taxMetadata.setCreatedBy("system");
    taxMetadata.setCreatedDate(new Date());
}
•	Automatically tracks who created/reviewed transactions and when.



