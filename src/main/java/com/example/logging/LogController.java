package com.example.logging;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class LogController {

    @Value("${mongodb.uri}")
    private String connectionString;

    @GetMapping("/user")
    public String getUserLogs(Model model) {
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            MongoDatabase database = mongoClient.getDatabase("event-ticket");
            MongoCollection<Document> collection = database.getCollection("user-logging");

            MongoCursor<Document> cursor = collection.find().iterator();
            List<Map<String, Object>> logs = new ArrayList<>();
            
            while (cursor.hasNext()) {
            	Document doc = cursor.next();
                logs.add(doc);
            }

            model.addAttribute("logs", logs);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("logs", "Error retrieving logs from database");
        }

        return "logs"; 
    }
    
    @GetMapping("/system")
    public String getSystemLogs(Model model) {
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            MongoDatabase database = mongoClient.getDatabase("event-ticket");
            MongoCollection<Document> collection = database.getCollection("sys-logging");

            MongoCursor<Document> cursor = collection.find().iterator();
            List<Map<String, Object>> logs = new ArrayList<>();
            
            while (cursor.hasNext()) {
            	Document doc = cursor.next();
                logs.add(doc);
            }

            model.addAttribute("logs", logs);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("logs", "Error retrieving logs from database");
        }

        return "logs"; 
    }
    
}
