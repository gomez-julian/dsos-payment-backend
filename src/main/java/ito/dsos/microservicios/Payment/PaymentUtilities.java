package ito.dsos.microservicios.Payment;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.UUID;

public class PaymentUtilities {
    /*
    LA EXPRESIÓN REGULAR QUE VALIDA QUE LOS DATOS DE UNA TARJETA SEA DE UNA DE LAS SIGUIENTES MANERAS
    0000 0000 0000 0000
    0000-0000-0000-0000
    0000000000000000
     */
    private static final String regex = "^(([\\d]{4}[-|\\s]){3}([\\d]{4}){1})|[\\d]{16}$";
    /*
    OBJETO PARA VALIDAR LA EXPRESIÓN REGULAR
     */
    private static final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);

    /*
    METODO QUE SE UTILIZA PARA VALIDAR UNA TARJETA DE CREDITO
     */
    public static boolean verifyCard(String paymentMethod){
        final Matcher matcher = pattern.matcher(paymentMethod);
        /*
        SI LO QUE SE MANDO ES LA CADENA "EFECTIVO" TAMBIÉN ES VALIDO Y SE REGRESA TRUE
        SI LA CADENA NO ES EFECTIVO PERO SI CUMPLE LA EXPRESION REGULAR TAMBIEN REGRESA TRUE
        EN CUALQUIERA DE LOS CASOS AMBAS CADENAS DEBEN SER MENOS DE 20 CARACTERES
         */
        if(paymentMethod.equals("Efectivo") || matcher.find())
            return paymentMethod.length() < 20;
        return false;
    }

    /*
    METODO PARA MANDAR LOS RESPONSE DE ERROR, Y SOLO SE MANDA EL MENSAJE ESPECÍFICO
     */
    public static Map<String, Object> errorResponse(String message){
        Map<String, Object> map = new HashMap<>();
        map.put("message", "ERROR: " + message);
        map.put("status", "INCORRECT");
        return map;
    }

    /*
    METODO PARA GENERAR EL UUID, Y SE REGRESA LA CADENA, ESTE SE UTILIZA PARA EL CAMPO UUID
     */
    public static String generateUUID4(){
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
