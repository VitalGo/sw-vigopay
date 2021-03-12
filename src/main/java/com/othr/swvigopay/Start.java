package com.othr.swvigopay;

import com.othr.swvigopay.exceptions.NegativeAmountExceptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Start {

	public static void main(String[] args) throws NegativeAmountExceptions {

		SpringApplication.run(Start.class, args);
	}
}
