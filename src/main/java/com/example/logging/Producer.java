package com.example.logging;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

@Service
public class Producer {

    private final static String QUEUE_NAME = "logs";

    @Value("${rabbitmq.uri}")
    private String rabbitMqUri;

    @SuppressWarnings("unchecked")
	public void sendMessageToRabbitMQ(String typeLogging, String timestamp, String logger, String level, String path, String content) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setUri(rabbitMqUri);
            
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);

            JSONObject logJson = new JSONObject();
            logJson.put("type-logging", typeLogging);
            logJson.put("timestamp", timestamp);
            logJson.put("logger", logger);
            logJson.put("level", level);
            logJson.put("path", path);
            logJson.put("content", content);
            
            AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                    .deliveryMode(2) // persistent
                    .build();

            channel.basicPublish("", QUEUE_NAME, properties, logJson.toString().getBytes("UTF-8"));
            System.out.println("Sent log: " + logJson.toString());

            channel.close();
            connection.close();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
