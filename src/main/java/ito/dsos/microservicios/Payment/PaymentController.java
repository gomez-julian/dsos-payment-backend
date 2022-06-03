package ito.dsos.microservicios.Payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@RequestMapping(path = "/api/payment")
@RestController
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService){
        this.paymentService = paymentService;
    }

    /*
    TODO: Fitrar por statusDelete
     */
    @GetMapping("/records")
    public ResponseEntity<List<PaymentEntity>>  getAll(){
        try {
            return new ResponseEntity<>(paymentService.getAll(),HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
    TODO: Validar y cachar los errores
     */
    @GetMapping("/records/{id}")
    public ResponseEntity<Optional<PaymentEntity>> getOne(@PathVariable String id){
        return new ResponseEntity<>(paymentService.getById(Long.parseLong(id)),HttpStatus.OK);
    }

    @PostMapping("/pay")
    public ResponseEntity<PaymentEntity> postPayment(@RequestBody PaymentEntity payment){
        try {
            if (payment == null)
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            payment.setPaymentStatus("P");
            payment.setPaymentDate(LocalDateTime.now());
            return new ResponseEntity<>(paymentService.save(payment), HttpStatus.CREATED);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    @PutMapping("/update/{id}")
    public ResponseEntity<PaymentEntity> updatePayment(@ModelAttribute PaymentEntity payment,
                                                       @PathVariable String id){
        try{
            PaymentEntity paymentEntity = paymentService.getById(Long.parseLong(id))
                    .orElseThrow(() -> new IllegalStateException("Error al obtener la compra."));
            return new ResponseEntity<>(paymentService.updateAll(payment, Long.parseLong(id)),
                                                                HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /*
    Cambia el status del pago a completado "C"
     */
    @Transactional
    @PutMapping("/confirm/{id}")
    public ResponseEntity<PaymentEntity> putPayment(@PathVariable String id){
        try{
            PaymentEntity payment = paymentService.getById(Long.parseLong(id))
                    .orElseThrow(() -> new IllegalStateException("Error al obtener la compra"));
            payment.setPaymentStatus("C");
            payment.setPositivePaymentDate(LocalDateTime.now());
            return new ResponseEntity<>(paymentService.save(payment), HttpStatus.CREATED);
        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /*
    Cambia el status a eliminado "D"
    TODO: Cambiar el statusDelete a true
     */
    @CrossOrigin
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<PaymentEntity> deletePayment(@PathVariable String id){
        PaymentEntity payment = paymentService.getById(Long.parseLong(id))
                .orElseThrow(() -> new IllegalStateException("Error al obtener la compra"));
        paymentService.delete(Long.parseLong(id));
        payment.setPaymentStatus("D");
        return new ResponseEntity<>(payment, HttpStatus.OK);
    }
}
