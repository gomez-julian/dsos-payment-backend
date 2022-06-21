package ito.dsos.microservicios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@SpringBootApplication
public class MicroserviciosApplication {

	@PostConstruct
	void started() {
		TimeZone.setDefault(TimeZone.getTimeZone("America/Mexico_City"));
	}


		public static void main(String[] args) {
		SpringApplication.run(MicroserviciosApplication.class, args);
	}

}
