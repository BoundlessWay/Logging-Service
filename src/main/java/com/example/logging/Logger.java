package com.example.logging;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;

import org.bson.Document;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.stereotype.Service;

@Service
public class Logger {

    private final String connectionString = "mongodb+srv://voducloi236:BzawYXJnTbzilFmS@cluster0.ghohcz9.mongodb.net/event-ticket?retryWrites=true&w=majority&appName=Cluster0";
    
    private MongoClient mongoClient;
    private MongoDatabase database;
    
    public Logger() {
        connectToMongoDB();
    }
    
    private void connectToMongoDB() {
        if (mongoClient == null) {
            ServerApi serverApi = ServerApi.builder()
                    .version(ServerApiVersion.V1)
                    .build();

            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(connectionString))
                    .serverApi(serverApi)
                    .build();

            try {
                mongoClient = MongoClients.create(settings);
                database = mongoClient.getDatabase("event-ticket");
                System.out.println("Connected to MongoDB successfully.");
            } catch (MongoException e) {
                e.printStackTrace();
                System.err.println("Failed to connect to MongoDB: " + e.getMessage());
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    public void logToMongoDB(String logMessage)  {
    	if (mongoClient == null || database == null) {
            System.err.println("MongoDB connection is not established.");
            return;
        }

        try  {
            JSONObject jsonMessage = (JSONObject) JSONValue.parse(logMessage);
            MongoCollection<Document> collection = database.getCollection("user-logging");

            if (jsonMessage == null) {
                jsonMessage = new JSONObject();
                jsonMessage.put("Json-string", "invalid");
                jsonMessage.put("content", logMessage);
            } else {
                jsonMessage.put("Json-string", "valid");
                String type = (String) jsonMessage.get("type-logging");

                if (type != null && type.equals("system")) {
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
