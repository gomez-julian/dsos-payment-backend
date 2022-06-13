package ito.dsos.microservicios.Payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static ito.dsos.microservicios.Payment.PaymentUtilities.errorResponse;

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
    NOTA: PARA EVITAR QUE CUALQUIER ENDPOINT FALLE SI NO PASARAN LAS VALIDACIONES, SE MANEJA TODOS
    ENTRO DE UN TRY CATCH, EN EL CUAL EL CATCH REGRESA EN UN JSON EL MENSAJE COMPLETO DE
    LA EXCEPTION, POR SI LLEGA A FALLAR PUEDAN NOTIFICARLO
    try {
            ...
        }catch (Exception e){
            Map<String, Object> map = errorResponse(e.getClass().toString());
            return new ResponseEntity<>(map,HttpStatus.UNPROCESSABLE_ENTITY);
        }
     */

    @GetMapping("/records")
    public ResponseEntity<Object>  getAll(){
        try {
            /*
            REGRESA UNA LISTA CON TODAS LAS ENTIDADES
             */
            return new ResponseEntity<>(paymentService.getAll(),HttpStatus.OK);
        }catch (Exception e){
            Map<String, Object> map = errorResponse(e.getClass().toString());
            return new ResponseEntity<>(map,HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @GetMapping("/records/{id}")
    public ResponseEntity<Object> getOne(@PathVariable String id){
        try {
            /*
            OBTIENE LA ENTIDAD POR ID, PERO CON UN OBJETO OPCIONAL
            SI EL OBJETO EXISTE isPresent() SALE TRUE, Y EN CASO CONTRARIO
            SE MANDA UN ERROR 404
             */
            Optional<PaymentEntity> payment = paymentService.getById(Long.parseLong(id));
            if(!payment.isPresent())
                return new ResponseEntity<>(errorResponse("No existe un pago con ese ID"),HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(payment,HttpStatus.OK);
        }catch (Exception e){
            Map<String, Object> map = errorResponse(e.getClass().toString());
            return new ResponseEntity<>(map,HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @PostMapping("/pay")
    public ResponseEntity<Object> postPayment(@RequestBody PaymentEntity payment){
        try {
            /*
            LO PRIMERO QUE SE PREGUNTA ES SI SE MANDÓ DENTRO DE LA PETICIÓN,
            EN CASO CONTRARIO, REGRESA UN NO CONTENT
             */
            if (payment == null)
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            if(payment.getPaymentAmount() <= 0)
                /*
                VALIDA SI LA CANTIDAD INGRESADA ES MAYOR A 0
                 */
                return new ResponseEntity<>(errorResponse("La cantidad a pagar debe ser mayor a 0"),
                                            HttpStatus.BAD_REQUEST);
                /*
                VERIFICA SI LA TARJETA DE CRÉDITO CUMPLE CON EL FORMATO ADECUADO
                (REVISAR LA CLASE UTILITIES)
                 */
            if(!PaymentUtilities.verifyCard(payment.getPaymentMethod())) {
                /*
                SI NO CUPLE CON EL FORMATO REGRESA UN 404
                 */
                String message = "El formato de la cadena debe coincidir con la forma '0000-0000-0000-0000', '0000 0000 0000 0000', '0000000000000000' o Efectivo";
                return new ResponseEntity<>(errorResponse(message),HttpStatus.BAD_REQUEST);
            }
            /*
            SI PASA TODAS LAS VALIDACIONES ENTONCES EMPIEZA A TERMINAR DE CONFIGURAR EL OBJETO
            1. COLOCA EL ESTADO DEL PAGO EN PENDIENTE
            2. COLOCA LA FECHA Y HORA ACTUAL COMO FECHA DE PAGO
            3. EL LOG COLOCA QUE SE HA CREADO CORRECTAMENTE
            4. EL STATUS DELETE A FALSO
            5. GENERA EL UUID4 Y LO AGREGA A LOS ATRIBUTOS DEL PAGO
            (VER UTILITIES)
             */
            payment.setPaymentStatus("Pending");
            LocalDateTime date = LocalDateTime.now();
            payment.setPaymentDate(date);
            payment.setLog("Created correctly: " + date);
            payment.setStatusDelete(false);
            payment.setUuid(PaymentUtilities.generateUUID4());
            /*
            FINALMETE SE GUARDA EL OBJETO Y SE RETORNA CON UN 201
             */
            return new ResponseEntity<>(paymentService.save(payment), HttpStatus.CREATED);
        }catch (DataIntegrityViolationException e){
            /*
            AL MOMENTO DE CREAR UN OBJETO PUEDE QUE YA EXISTA ESA REFERENCIA, POR LO QUE
            SE CACHA LA EXCEPTION DE ESE TIPO Y SE MANDA UN 404 INDICANDO EL PROBLEMA
             */
            Map<String, Object> map = errorResponse("Ya existe esa referencia");
            return new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);
        }
        catch (Exception e){
            Map<String, Object> map = errorResponse(e.getClass().toString());
            return new ResponseEntity<>(map,HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @Transactional
    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updatePayment(@RequestBody PaymentEntity payment,
                                                       @PathVariable String id){
        try{
            /*
            LA MODIFICACION SOLO SE PUEDE A HACER A LOS CAMPOS DE REFERENCIA, METODO Y MONTO,
            POR LO QUE SOLO SE PUEDEN VALIDAR Y CAMBIAR ESOS VALORES
            PRIMERO SE INTENTA OBTENER EL PAGO Y DE LO CONTRARIO SE RETORNA UNA EXCEPTION QUE SE
            CACHA MAS ABAJO
             */
            PaymentEntity paymentEntity = paymentService.getById(Long.parseLong(id))
                    .orElseThrow(() -> new IllegalStateException("Error al obtener la compra."));
            /*
            VERIFICA SI SE MANDO EL VALOR EN EL PUT Y LO VUELVE A VERIFICAR CON EL METODO EL POST
            SI PASA LA VERIFICACION ENTONCES SE CAMBIA AL NUEVO VALOR
             */
            String paymentMethod = payment.getPaymentMethod();
            if(paymentMethod != null && PaymentUtilities.verifyCard(paymentMethod))
                paymentEntity.setPaymentMethod(paymentMethod);
            /*
            SE VERIFICA QUE LA CANTIDAD INGRESADA TAMBIÉN SEA MAYOR A 0
             */
            if(payment.getPaymentAmount() > 0)
                paymentEntity.setPaymentAmount(payment.getPaymentAmount());
            /*
            SE VERIFICA SI LA REFERENCIA QUE SE MANO NO ES VACÍA
             */
            if(payment.getReferenceID() != null)
                paymentEntity.setReferenceID(payment.getReferenceID());
            /*
            SE CAMBIA EL LOG PARA INDICAR QUE FUE MODIFICADA
             */
            paymentEntity.setLog("Modified: " + LocalDateTime.now());
            /*
            SE REGRESA LA ENTIDAD QUE FUE MODIFICADA
             */
            return new ResponseEntity<>(paymentService.save(paymentEntity), HttpStatus.ACCEPTED);
        }catch (IllegalStateException e) {
            /*
            SI NO SE ENCONTRÓ EL PAGO MÁS ARRIBA, ENTONCES SE MANDA EL ERROR
             */
            Map<String, Object> map = errorResponse("No existe un pago con ese ID");
            return new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);
        }catch (DataIntegrityViolationException e){
            /*
            SI VUELVE A EXISIR UNA REFERENCIA IGUAL, SE MANDA ESE ERROR
             */
            Map<String, Object> map = errorResponse("Ya existe esa referencia");
            return new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            /*
            SE CACHA CUALQUIER OTRO ERROR QUE SE HAYA GENERADO
             */
            Map<String, Object> map = errorResponse(e.getClass().toString());
            return new ResponseEntity<>(map,HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @Transactional
    @PutMapping("/confirm/{id}")
    public ResponseEntity<Object> putPayment(@PathVariable String id){
        try{
            /*
            SE RECUPERA EL PAGO POR EL ID, Y DE LO CONTRARIO MANDA UNA EXCEPTION
             */
            PaymentEntity payment = paymentService.getById(Long.parseLong(id))
                    .orElseThrow(() -> new IllegalStateException("Error al obtener la compra"));
            /*
            SI SE RECUPERA CORRECTAMENTE, EMPIEZA A REALIZAR LA CONFIRMACION
            1. SE CAMBIA EL ESTADO DEL PAGO A COMPLETO
            2. SE CAMBIA EL LOG PARA INDICAR QUE YA FUE COMPLETADO EL PAGO
            3. SE SETEA EL ATRIBUTO DE FECHA DE PAGO COMPLETADO A LA HORA ACTUAL
             */
            payment.setPaymentStatus("Completed");
            LocalDateTime date = LocalDateTime.now();
            payment.setLog("Payment completed: " + date);
            payment.setPositivePaymentDate(date);
            /*
            SE GUARDA LA ENTIDAD Y SE REGRESA
             */
            return new ResponseEntity<>(paymentService.save(payment), HttpStatus.ACCEPTED);
        }catch (IllegalStateException e) {
            /*
            SI NO SE ENCONTRÓ EL PAGO, SE MANDA EL ERROR
             */
            Map<String, Object> map = errorResponse("No existe un pago con ese ID");
            return new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            Map<String, Object> map = errorResponse(e.getClass().toString());
            return new ResponseEntity<>(map,HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }


    @CrossOrigin
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deletePayment(@PathVariable String id){
        try{
            /*
            SE RECUPERA EL PAGO POR ID, Y SI NO SE ENCUENTRA SE MANDA UNA EXCEPTION
             */
            PaymentEntity payment = paymentService.getById(Long.parseLong(id))
                    .orElseThrow(() -> new IllegalStateException("Error al obtener la compra"));
            /*
            SE ELIMINA POR MEDIO DEL SERVICIO
             */
            paymentService.delete(Long.parseLong(id));
            /*
            SE CAMBIA EL ESTADO A DETELED Y EL STATUS DELETE A TRUE
             */
            payment.setPaymentStatus("Deleted");
            payment.setStatusDelete(true);
            /*
            SE REGRESA LOS DATOS DE LA ENTIDAD YA ELIMINADA
             */
            return new ResponseEntity<>(payment, HttpStatus.ACCEPTED);
        }catch (IllegalStateException e) {
            /*
            SI NO SE ENCUENTRA EL PAGO, SE MANDA UN MENSAJE DE ERROR 404
             */
            Map<String, Object> map = errorResponse("No existe un pago con ese ID");
            return new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            Map<String, Object> map = errorResponse(e.getClass().toString());
            return new ResponseEntity<>(map,HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
}
