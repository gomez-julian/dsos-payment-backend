package ito.dsos.microservicios.Payment;

import ito.dsos.microservicios.Auth.RestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static ito.dsos.microservicios.Payment.PaymentUtilities.errorResponse;

@CrossOrigin
@RequestMapping(path = "/api/payment")
@RestController
public class PaymentController {

    private final PaymentService paymentService;
    private final RestService auth;

    @Autowired
    public PaymentController(PaymentService paymentService, RestService restService) {
        this.paymentService = paymentService;
        this.auth = restService;
    }

    @Description("Historial de pagos")
    @GetMapping("/records")
    public ResponseEntity<Object> getAll() {
        try {
            return new ResponseEntity<>(paymentService.getAll(), HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> map = errorResponse(e.getClass().toString());
            return new ResponseEntity<>(map, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @Description("Pago específico")
    @GetMapping("/records/{id}")
    public ResponseEntity<Object> getOne(@PathVariable String id) {
        try {
            Optional<PaymentEntity> payment = paymentService.getById(Long.parseLong(id));
            if (!payment.isPresent())
                return new ResponseEntity<>(errorResponse("No existe un pago con ese ID"), HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(payment, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> map = errorResponse(e.getClass().toString());
            return new ResponseEntity<>(map, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @Description("Realizar un pago")
    @PostMapping("/pay")
    public ResponseEntity<Object> postPayment(@RequestBody PaymentEntity payment,
                                              @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            if (token == null)
                return new ResponseEntity<>(errorResponse("Favor enviar JWT en Headers como Authorization"), HttpStatus.UNAUTHORIZED);
            if (!auth.verifyToken(token))
                return new ResponseEntity<>(errorResponse("JWT Inválido o vencido"), HttpStatus.UNAUTHORIZED);
            if (payment == null)
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            if (payment.getPaymentAmount() <= 0)
                return new ResponseEntity<>(errorResponse("La cantidad a pagar debe ser mayor a 0"),
                        HttpStatus.BAD_REQUEST);
            if (!PaymentUtilities.verifyCard(payment.getPaymentMethod())) {
                String message = "El formato de la cadena debe coincidir con la forma '0000-0000-0000-0000', '0000 0000 0000 0000', '0000000000000000' o Efectivo";
                return new ResponseEntity<>(errorResponse(message), HttpStatus.BAD_REQUEST);
            }
            payment.setPaymentStatus("Pending");
            LocalDateTime date = LocalDateTime.now();
            payment.setPaymentDate(date);
            payment.setLog("Created correctly: " + date);
            payment.setStatusDelete(false);
            payment.setUuid(PaymentUtilities.generateUUID4());
            return new ResponseEntity<>(paymentService.save(payment), HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            Map<String, Object> map = errorResponse("Ya existe esa referencia");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(errorResponse("JWT Inválido o vencido"), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            Map<String, Object> map = errorResponse(e.getClass().toString());
            return new ResponseEntity<>(map, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @Description("Actualizar ciertos datos de un pago")
    @Transactional
    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updatePayment(@RequestBody PaymentEntity payment,
                                                @PathVariable String id,
                                                @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            if (token == null)
                return new ResponseEntity<>(errorResponse("Favor enviar JWT en Headers como Authorization"), HttpStatus.UNAUTHORIZED);
            if (!auth.verifyToken(token))
                return new ResponseEntity<>(errorResponse("JWT Inválido o vencido"), HttpStatus.UNAUTHORIZED);
            PaymentEntity paymentEntity = paymentService.getById(Long.parseLong(id))
                    .orElseThrow(() -> new IllegalStateException("Error al obtener la compra."));
            String paymentMethod = payment.getPaymentMethod();
            if (paymentMethod != null && PaymentUtilities.verifyCard(paymentMethod))
                paymentEntity.setPaymentMethod(paymentMethod);
            if (payment.getPaymentAmount() > 0)
                paymentEntity.setPaymentAmount(payment.getPaymentAmount());
            if (payment.getReferenceID() != null)
                paymentEntity.setReferenceID(payment.getReferenceID());
            paymentEntity.setLog("Modified: " + LocalDateTime.now());
            return new ResponseEntity<>(paymentService.save(paymentEntity), HttpStatus.ACCEPTED);
        } catch (IllegalStateException e) {
            Map<String, Object> map = errorResponse("No existe un pago con ese ID");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        } catch (DataIntegrityViolationException e) {
            Map<String, Object> map = errorResponse("Ya existe esa referencia");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(errorResponse("JWT Inválido o vencido"), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            Map<String, Object> map = errorResponse(e.getClass().toString());
            return new ResponseEntity<>(map, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @Description("Confirmar un pago")
    @Transactional
    @PutMapping("/confirm/{id}")
    public ResponseEntity<Object> putPayment(@PathVariable String id,
                                             @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            if (token == null)
                return new ResponseEntity<>(errorResponse("Favor enviar JWT en Headers como Authorization"), HttpStatus.UNAUTHORIZED);
            if (!auth.verifyToken(token))
                return new ResponseEntity<>(errorResponse("JWT Inválido o vencido"), HttpStatus.UNAUTHORIZED);
            PaymentEntity payment = paymentService.getById(Long.parseLong(id))
                    .orElseThrow(() -> new IllegalStateException("Error al obtener la compra"));
            payment.setPaymentStatus("Completed");
            LocalDateTime date = LocalDateTime.now();
            payment.setLog("Payment completed: " + date);
            payment.setPositivePaymentDate(date);
            return new ResponseEntity<>(paymentService.save(payment), HttpStatus.ACCEPTED);
        } catch (IllegalStateException e) {
            Map<String, Object> map = errorResponse("No existe un pago con ese ID");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(errorResponse("JWT Inválido o vencido"), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            Map<String, Object> map = errorResponse(e.getClass().toString());
            return new ResponseEntity<>(map, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @Description("Cancelar un pago")
    @Transactional
    @PutMapping("/cancel/{id}")
    public ResponseEntity<Object> calcelPayment(@PathVariable String id,
                                                @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            if (token == null)
                return new ResponseEntity<>(errorResponse("Favor enviar JWT en Headers como Authorization"), HttpStatus.UNAUTHORIZED);
            if (!auth.verifyToken(token))
                return new ResponseEntity<>(errorResponse("JWT Inválido o vencido"), HttpStatus.UNAUTHORIZED);
            PaymentEntity payment = paymentService.getById(Long.parseLong(id))
                    .orElseThrow(() -> new IllegalStateException("Error al obtener la compra"));
            payment.setPaymentStatus("Canceled");
            LocalDateTime date = LocalDateTime.now();
            payment.setLog("Canceled: " + date);
            payment.setPositivePaymentDate(null);
            return new ResponseEntity<>(paymentService.save(payment), HttpStatus.ACCEPTED);
        } catch (IllegalStateException e) {
            Map<String, Object> map = errorResponse("No existe un pago con ese ID");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(errorResponse("JWT Inválido o vencido"), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            Map<String, Object> map = errorResponse(e.getClass().toString());
            return new ResponseEntity<>(map, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }


    @Description("Eliminar un pago")
    @CrossOrigin
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deletePayment(@PathVariable String id,
                                                @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            if (token == null)
                return new ResponseEntity<>(errorResponse("Favor enviar JWT en Headers como Authorization"), HttpStatus.UNAUTHORIZED);
            if (!auth.verifyToken(token))
                return new ResponseEntity<>(errorResponse("JWT Inválido o vencido"), HttpStatus.UNAUTHORIZED);
            PaymentEntity payment = paymentService.getById(Long.parseLong(id))
                    .orElseThrow(() -> new IllegalStateException("Error al obtener la compra"));
            paymentService.delete(Long.parseLong(id));
            payment.setPaymentStatus("Deleted");
            payment.setStatusDelete(true);
            return new ResponseEntity<>(payment, HttpStatus.ACCEPTED);
        } catch (IllegalStateException e) {
            Map<String, Object> map = errorResponse("No existe un pago con ese ID");
            return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(errorResponse("JWT Inválido o vencido"), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            Map<String, Object> map = errorResponse(e.getClass().toString());
            return new ResponseEntity<>(map, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
}
