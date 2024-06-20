package com.example.logging;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeoutException;


@Service
public class Consumer {

    private final static String QUEUE_NAME = "logs";

    @Value("${rabbitmq.uri}")
    private String rabbitMqUri;
    
    @Autowired
    private Logger mongoDBLogger;
    
    @Autowired
    private Producer producer;

    public void receiveMessagesFromRabbitMQ() throws IOException, TimeoutException, KeyManagementException, NoSuchAlgorithmException, URISyntaxException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(rabbitMqUri);
        
        try {
        	Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            
     
            String typeLogging = "system";
            String timestamp = Instant.now().toString();
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
    
    private void handleConnectionFailure(Exception e) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException {
        
        String typeLogging = "system";
        String timestamp = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(Instant.now());
        System.out.println(timestamp);
        String logger = this.getClass().getSimpleName();
        String level = "Error";
        String path = "Logging-Service#Consumer.java#receiveMessagesFromRabbitMQ()";
        String content = "Failed to connect to Rabbit MQ: " + e.getMessage();
        
        producer.sendMessageToRabbitMQ(typeLogging, timestamp, logger, level, path, content);
        
    }
}
