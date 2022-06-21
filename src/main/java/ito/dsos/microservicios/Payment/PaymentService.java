package ito.dsos.microservicios.Payment;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public Optional<PaymentEntity> getById(Long id){
        return paymentRepository.findById(id);
    }

    public List<PaymentEntity> getAll(){
        return paymentRepository.findAll().stream().filter(e -> !e.getStatusDelete()).collect(Collectors.toList());
    }
}
