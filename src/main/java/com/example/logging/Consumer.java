package com.example.logging;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeoutException;


@Service
public class Consumer {

    private final static String QUEUE_NAME = "logs";

    private final String rabbitMqUri = "amqps://krgczsus:lZvR3IMF41W6uOaqofB4BnCWfALEr6mf@shrimp.rmq.cloudamqp.com/krgczsus";
    
    private static Logger mongoDBLogger = new Logger();
    
    private static Producer producer = new Producer();
    
    public Consumer() {
    	
    }

    public void receiveMessagesFromRabbitMQ()  {
        ConnectionFactory factory = new ConnectionFactory();
        
        try {
        	factory.setUri(rabbitMqUri);
        	Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            
     
            String typeLogging = "system";
            String timestamp = Instant.now().atZone(ZoneId.of("Asia/Ho_Chi_Minh")).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            String logger = this.getClass().getSimpleName();
            String level = "Info";
            String path = "Logging-Service#Consumer.java#receiveMessagesFromRabbitMQ()";
            String content = "Connect to Rabbit MQ successful";
            
            producer.sendMessageToRabbitMQ(typeLogging, timestamp, logger, level, path, content);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println("Received log: '" + message + "'");
                mongoDBLogger.logToMongoDB(message);
            };

            channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});
        } catch (IOException | TimeoutException | KeyManagementException | NoSuchAlgorithmException | URISyntaxException e) {     
            handleConnectionFailure(e);      
        }
    }
    
    private void handleConnectionFailure(Exception e) {
        
        String typeLogging = "system";
        String timestamp = Instant.now().atZone(ZoneId.of("Asia/Ho_Chi_Minh")).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        System.out.println(timestamp);
        String logger = this.getClass().getSimpleName();
        String level = "Error";
        String path = "Logging-Service#Consumer.java#receiveMessagesFromRabbitMQ()";
        String content = "Failed to connect to Rabbit MQ: " + e.getMessage();
        
        try {
			producer.sendMessageToRabbitMQ(typeLogging, timestamp, logger, level, path, content);
		} catch (KeyManagementException | NoSuchAlgorithmException | URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
    }
}
