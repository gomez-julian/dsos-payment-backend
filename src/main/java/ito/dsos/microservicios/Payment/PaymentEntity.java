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

    /*
    Referencia: Algun folio, recibo, orden de pago o identificador para el pago
     */
    @Column(name = "reference_id", unique = true, length = 10)
    private String referenceID;

    /*
    Fecha del pago, se calcula automáticamente
     */
    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "sale_id")
    private Integer saleID;

    /*
        Pendiente, Parcial, Cancelada, Devuelta, Completada, etc.
        TODO: Validar el dato enviado sea válido
         */
    @Column(name = "payment_status", length = 20)
    private String paymentStatus;

    /*
    Momento en el que el pago se completó adecuadamente
     */
    @Column(name = "positive_payment_date")
    private LocalDateTime positivePaymentDate;

    /*
    Monto de pago

     */
    @Column(name = "payment_amount", nullable = false)
    private Double paymentAmount;

    /*
    EL LOG GUARDA INFORMACIÓN ACERCA DEL ULTIMO CAMBIO OCURRIDO
     */
    @Column(name = "log")
    private String log;

    /*
        Si fue pago en efectivo, debe ser un String que solo diga EFECTIVO
        Si fue pago con tarjeta, debe ser un String con el numero de la tarjeta de la forma
        0000 0000 0000 0000
        NOTA: No se debe guardar la fecha de expiracion o el CVV, ya que solo es para validar el pago
         */
    @Column(name = "payment_method", length = 19, updatable = false)
    private String paymentMethod;

    /*
    Si se elimina un pago, en vez de borrarlo de la base, de enciende este atributo, y ya no se
    mostrará cuando se hagan peticiones, para que quede registro de la info
     */
    @Column(name = "status_delete")
    private Boolean statusDelete;

    /*
    EL UUID ES EL VERIFICADOR DEL PAGO
     */
    @Column(name = "uuid", length = 50)
    private String uuid;

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

    public PaymentEntity(String paymentStatus, String log) {
        this.paymentStatus = paymentStatus;
        this.log = log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public Boolean getStatusDelete() {
        return statusDelete;
    }

    public void setPaymentID(Long paymentID) {
        this.paymentID = paymentID;
    }

    public void setPaymentAmount(Double paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentEntity() {

    }

    public PaymentEntity(Integer saleID, Double paymentAmount, String paymentMethod) {
        this.saleID = saleID;
        this.paymentAmount = paymentAmount;
        this.paymentMethod = paymentMethod;
    }

    public PaymentEntity(String referenceID,
                         Double paymentAmount,
                         String paymentMethod) {
        this.referenceID = referenceID;
        this.paymentAmount = paymentAmount;
        this.paymentMethod = paymentMethod;
    }

    public PaymentEntity(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public Double getPaymentAmount() {
        return paymentAmount;
    }

    public LocalDateTime getPositivePaymentDate() {
        return positivePaymentDate;
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

    public String getReferenceID() {
        return referenceID;
    }

    public void setReferenceID(String referenceID) {
        this.referenceID = referenceID;
    }

    public Long getPaymentID() {
        return paymentID;
    }

    public void setPositivePaymentDate(LocalDateTime positivePaymentDate) {
        this.positivePaymentDate = positivePaymentDate;
    }

    public void setStatusDelete(Boolean statusDelete) {
        this.statusDelete = statusDelete;
    }
}
