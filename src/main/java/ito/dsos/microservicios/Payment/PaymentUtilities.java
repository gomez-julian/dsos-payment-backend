package ito.dsos.microservicios.Payment;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.UUID;

public class PaymentUtilities {
    private static final String regex = "^(([\\d]{4}[-|\\s]){3}([\\d]{4}){1})|[\\d]{16}$";
    private static final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);

    public static boolean verifyCard(String paymentMethod){
        final Matcher matcher = pattern.matcher(paymentMethod);
        if(paymentMethod.equals("Efectivo") || matcher.find())
            return paymentMethod.length() < 20;
        return false;
    }

    public static Map<String, Object> errorResponse(String message){
        Map<String, Object> map = new HashMap<>();
        map.put("message", "ERROR: " + message);
        map.put("status", "INCORRECT");
        return map;
    }

    public static String generateUUID4(){
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
