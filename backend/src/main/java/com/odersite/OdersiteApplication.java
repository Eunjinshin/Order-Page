package com.odersite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class OdersiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(OdersiteApplication.class, args);
    }
}
