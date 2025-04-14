package com.digitaltax.model;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "transactions")
public class Transaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date timestamp;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status = TransactionStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jurisdiction_code", nullable = false)
    private Jurisdiction jurisdiction;

    @Embedded
    private TaxMetadata taxMetadata;

    @ManyToMany
    @JoinTable(
            name = "transaction_tax_officer",
            joinColumns = @JoinColumn(name = "transaction_id"),
            inverseJoinColumns = @JoinColumn(name = "tax_officer_id")
    )
    // private Set<TaxOfficer> taxOfficers;
    private Set<TaxOfficer> taxOfficers = new HashSet<>();

    @Transient
    private BigDecimal calculatedTax;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Jurisdiction getJurisdiction() {
        return jurisdiction;
    }

    public void setJurisdiction(Jurisdiction jurisdiction) {
        this.jurisdiction = jurisdiction;
    }

    public TaxMetadata getTaxMetadata() {
        return taxMetadata;
    }

    public void setTaxMetadata(TaxMetadata taxMetadata) {
        this.taxMetadata = taxMetadata;
    }

    public Set<TaxOfficer> getTaxOfficers() {
        return taxOfficers;
    }

    public void setTaxOfficers(Set<TaxOfficer> taxOfficers) {
        this.taxOfficers = taxOfficers;
    }

    public BigDecimal getCalculatedTax() {
        return calculatedTax;
    }

    public void setCalculatedTax(BigDecimal calculatedTax) {
        this.calculatedTax = calculatedTax;
    }

    @PostLoad
    private void calculateTax() {
        if (amount != null && jurisdiction != null && jurisdiction.getTaxRate() != null) {
            calculatedTax = amount.multiply(BigDecimal.valueOf(jurisdiction.getTaxRate()));
        }
    }

    // In Transaction entity
    @PrePersist
    private void setMetadataOnCreate() {
        if (taxMetadata == null) {
            taxMetadata = new TaxMetadata();
        }
        taxMetadata.setCreatedBy("system"); // In real app, get current user
        taxMetadata.setCreatedDate(new Date());
    }

    @PreUpdate
    private void setMetadataOnUpdate() {
        if (taxMetadata == null) {
            taxMetadata = new TaxMetadata();
        }
        taxMetadata.setReviewedBy("system"); // In real app, get current user
        taxMetadata.setReviewedDate(new Date());
    }
    public void addTaxOfficer(TaxOfficer officer) {
        this.taxOfficers.add(officer);
        officer.getTransactions().add(this);
    }
}