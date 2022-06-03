package ito.dsos.microservicios.Payment;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    private final Log LOG= LogFactory.getLog(PaymentService.class);
    private PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository){
        this.paymentRepository = paymentRepository;
    }

    public PaymentEntity save(PaymentEntity newPayment){
        return paymentRepository.save(newPayment);
    }

    public void delete(Long id){
        paymentRepository.deleteById(id);
    }

    public PaymentEntity update(PaymentEntity paymentUpdate, Long id){
        PaymentEntity paymentEntity = paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("El producto no existe"));
        paymentEntity.setPaymentStatus(paymentUpdate.getPaymentStatus());
        paymentEntity.setPositivePaymentDate(paymentUpdate.getPositivePaymentDate());
        return paymentRepository.save(paymentEntity);
    }

    public Optional<PaymentEntity> getById(Long id){
        return paymentRepository.findById(id);
    }

    public List<PaymentEntity> getAll(){
        return paymentRepository.findAll();
    }

    public PaymentEntity updateAll(PaymentEntity payment, Long id){
        payment.setPaymentID(id);
        return paymentRepository.save(payment);
    }
}
