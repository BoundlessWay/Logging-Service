package com.example.logging;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

@Service
public class Consumer {

    private final static String QUEUE_NAME = "logs";

    @Value("${rabbitmq.uri}")
    private String rabbitMqUri;
    
    private final Logger mongoDBLogger;
    
    public Consumer(Logger mongoDBLogger) {
        this.mongoDBLogger = mongoDBLogger;
    }

    @PostConstruct
    public void init() {
        try {
            receiveMessagesFromRabbitMQ();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void receiveMessagesFromRabbitMQ() throws IOException, TimeoutException, KeyManagementException, NoSuchAlgorithmException, URISyntaxException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(rabbitMqUri);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println("Received log: '" + message + "'");
//            Logger mongoDBLogger = new Logger();
            mongoDBLogger.logToMongoDB(message);
        };

        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});
    }
}
