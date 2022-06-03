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
    @Column(name = "reference_id", unique = true, length = 10, nullable = false)
    private String referenceID;

    /*
    Codigo postal para el registro del pago
     */
    @Column(name = "address_cp", length = 5)
    private String address_cp;

    /*
    Fecha del pago, se calcula automáticamente
    TODO: Cambiar la hora del servidor porque tiene una equivocada
     */
    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

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
    TODO: Validar que no sea negativo, 0, etc.
     */
    @Column(name = "payment_amount", nullable = false)
    private Double paymentAmount;

    /*
    Si fue pago en efectivo, debe ser un String que solo diga EFECTIVO
    Si fue pago con tarjeta, debe ser un String con el numero de la tarjeta de la forma
    0000 0000 0000 0000
    TODO: Validar que la entrada sea una de estas dos opciones
    NOTA: No se debe guardar la fecha de expiracion o el CVV, ya que solo es para validar el pago
     */
    @Column(name = "payment_method", length = 16, updatable = false)
    private String paymentMethod;

    /*
    Si se elimina un pago, en vez de borrarlo de la base, de enciende este atributo, y ya no se
    mostrará cuando se hagan peticiones, para que quede registro de la info
    TODO: Filtrar que no se muestre ningun registro con este atributo en true
     */
    @Column(name = "status_delete")
    private Boolean statusDelete;

    public Boolean getStatusDelete() {
        return statusDelete;
    }

    public void setPaymentID(Long paymentID) {
        this.paymentID = paymentID;
    }

    public void setAddress_cp(String address_cp) {
        this.address_cp = address_cp;
    }

    public void setPaymentAmount(Double paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentEntity() {

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

    public String getAddress_cp() {
        return address_cp;
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
