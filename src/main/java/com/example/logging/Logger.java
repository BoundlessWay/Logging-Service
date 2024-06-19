package com.example.logging;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.MongoException;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import org.bson.Document;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class Logger {

    @Value("${mongodb.uri}")
    private String connectionString;

    @SuppressWarnings("unchecked")
    public void logToMongoDB(String logMessage) {
        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();

        try (MongoClient mongoClient = MongoClients.create(settings)) {
        	MongoDatabase database = mongoClient.getDatabase("event-ticket");
            JSONObject jsonMessage = (JSONObject) JSONValue.parse(logMessage);
            MongoCollection<Document> collection = database.getCollection("user-logging");

            if (jsonMessage == null) {
                jsonMessage = new JSONObject();
                jsonMessage.put("Json-string", "invalid");
                jsonMessage.put("content", logMessage);
            } else {
                jsonMessage.put("Json-string", "valid");
                String typeLogging = (String) jsonMessage.get("type-logging");

                if (typeLogging != null && typeLogging.equals("system")) {
                    collection = database.getCollection("sys-logging");
                } else {
                    collection = database.getCollection("user-logging");
                }
            }

            Document logDocument = Document.parse(jsonMessage.toString());
            collection.insertOne(logDocument);
            System.out.println("Logged message to MongoDB: " + logMessage);

        } catch (MongoException e) {
            e.printStackTrace();
        }
    }
}
