package com.example.logging;



public class Main {

    public static void main(String[] args) throws Exception {

        Consumer consumer = new Consumer();
        consumer.receiveMessagesFromRabbitMQ();
    }
}
