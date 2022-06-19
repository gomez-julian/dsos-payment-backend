package ito.dsos.microservicios.Payment;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
public class PaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "payment_id")
    private Long paymentID;

    @Column(name = "reference_id", unique = true, length = 10)
    private String referenceID;

    @Column(name = "sale_id")
    private Integer saleID;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "positive_payment_date")
    private LocalDateTime positivePaymentDate;

    @Column(name = "payment_status", length = 20)
    private String paymentStatus;

    @Column(name = "payment_amount", nullable = false)
    private Double paymentAmount;

    @Column(name = "payment_method", length = 19, updatable = false)
    private String paymentMethod;

    @Column(name = "uuid", length = 50)
    private String uuid;

    @Column(name = "status_delete")
    private Boolean statusDelete;

    @Column(name = "log")
    private String log;

    public PaymentEntity(String paymentStatus, String log) {
        this.paymentStatus = paymentStatus;
        this.log = log;
    }

    public PaymentEntity() {

    }

    public PaymentEntity(Integer saleID, Double paymentAmount, String paymentMethod) {
        this.saleID = saleID;
        this.paymentAmount = paymentAmount;
        this.paymentMethod = paymentMethod;
    }

    public PaymentEntity(String referenceID, Double paymentAmount, String paymentMethod) {
        this.referenceID = referenceID;
        this.paymentAmount = paymentAmount;
        this.paymentMethod = paymentMethod;
    }

    public PaymentEntity(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Integer getSaleID() {
        return saleID;
    }

    public void setSaleID(Integer saleID) {
        this.saleID = saleID;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public Boolean getStatusDelete() {
        return statusDelete;
    }

    public void setStatusDelete(Boolean statusDelete) {
        this.statusDelete = statusDelete;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Double getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(Double paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public LocalDateTime getPositivePaymentDate() {
        return positivePaymentDate;
    }

    public void setPositivePaymentDate(LocalDateTime positivePaymentDate) {
        this.positivePaymentDate = positivePaymentDate;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getReferenceID() {
        return referenceID;
    }

    public void setReferenceID(String referenceID) {
        this.referenceID = referenceID;
    }

    public Long getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(Long paymentID) {
        this.paymentID = paymentID;
    }
}
