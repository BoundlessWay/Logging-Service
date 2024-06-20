package com.example.logging;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

@SpringBootApplication
public class LoggingServiceApplication implements CommandLineRunner {
	
	@Autowired
    private Consumer consumer;

	public static void main(String[] args) {
		SpringApplication.run(LoggingServiceApplication.class, args);
	}
	
	@Override
    public void run(String... args) throws Exception {
        consumer.receiveMessagesFromRabbitMQ();
    }

}
