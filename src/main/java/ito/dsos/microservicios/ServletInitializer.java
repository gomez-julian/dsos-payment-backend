package ito.dsos.microservicios;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(MicroserviciosApplication.class);
	}

}
/*
<dependency>
			<groupId>org.postgresql</groupId>
			<artifactId>postgresql</artifactId>
			<scope>runtime</scope>
		</dependency>
		spring.datasource.url=jdbc:postgresql://ec2-52-86-177-34.compute-1.amazonaws.com:5432/dcv6dp9bl9t1e7
spring.datasource.username=hxfbkjwvtxpfbl
spring.datasource.password=559ba42d831c7f0819a9b5cd06a9644171818f2a51997edb1fb01b4e9d0fdd16
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
 */
