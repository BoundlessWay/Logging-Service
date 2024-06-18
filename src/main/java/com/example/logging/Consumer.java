package com.example.logging;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import javax.net.ssl.SSLSocketFactory;

public class Consumer {

    private final static String QUEUE_NAME = "logs";

    public void receiveMessagesFromRabbitMQ() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();

        factory.setUri("amqps://krgczsus:lZvR3IMF41W6uOaqofB4BnCWfALEr6mf@shrimp.rmq.cloudamqp.com/krgczsus");
        
        SSLSocketFactory sslSocketFactory = CustomTrustManager.createSSLSocketFactory();
        factory.setSocketFactory(sslSocketFactory);
        
        Connection connection = null;
        
        try {
            connection = factory.newConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println("Received log: '" + message + "'");
            Logger mongoDBLogger = new Logger();
            mongoDBLogger.logToMongoDB(message);
        };

        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
        });
    }
}

