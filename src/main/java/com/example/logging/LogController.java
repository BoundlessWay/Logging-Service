package com.example.logging;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
//import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
public class LogController {

	private final String connectionString = "mongodb+srv://voducloi236:BzawYXJnTbzilFmS@cluster0.ghohcz9.mongodb.net/event-ticket?retryWrites=true&w=majority&appName=Cluster0";

	@GetMapping("/")
    public String getAllLogs(Model model) {
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            MongoDatabase database = mongoClient.getDatabase("event-ticket");
            
            MongoCollection<Document> userCollection = database.getCollection("user-logging");
            List<Document> userLogs = userCollection.find().into(new ArrayList<>());

 
            MongoCollection<Document> systemCollection = database.getCollection("sys-logging");
            List<Document> systemLogs = systemCollection.find().into(new ArrayList<>());

            List<Map<String, Object>> combinedLogs = new ArrayList<>();
            combinedLogs.addAll(userLogs);
            combinedLogs.addAll(systemLogs);
            
            combinedLogs.sort((log1, log2) -> {
                String timestamp1 = log1.get("timestamp").toString();
                String timestamp2 = log2.get("timestamp").toString();
                return timestamp2.compareTo(timestamp1); 
            });

            model.addAttribute("logs", combinedLogs);
            model.addAttribute("logType", "All Logs");
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("logs", "Error retrieving logs from database");
            model.addAttribute("logType", "Error");
        }

        return "logs"; 
    }
	
    @GetMapping("/user")
    public String getUserLogs(Model model) {
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            MongoDatabase database = mongoClient.getDatabase("event-ticket");
            MongoCollection<Document> collection = database.getCollection("user-logging");
            
            String today = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).toLocalDate().toString();
            Document query = new Document("timestamp", new Document("$regex", "^" + today));
            List<Document> documents = collection.find(query).into(new ArrayList<>());

            List<Map<String, Object>> logs = new ArrayList<>();
            
            for (Document doc : documents) {
                logs.add(doc);
            }
            
            Collections.reverse(logs);

            model.addAttribute("logs", logs);
            model.addAttribute("logType", "User Logs");
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("logs", "Error retrieving logs from database");
            model.addAttribute("logType", "Error");
        }

        return "logs"; 
    }
    
    @GetMapping("/system")
    public String getSystemLogs(Model model) {
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            MongoDatabase database = mongoClient.getDatabase("event-ticket");
            MongoCollection<Document> collection = database.getCollection("sys-logging");
            
            String today = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).toLocalDate().toString();
            Document query = new Document("timestamp", new Document("$regex", "^" + today));
            List<Document> documents = collection.find(query).into(new ArrayList<>());
            
            List<Map<String, Object>> logs = new ArrayList<>();
            
            for (Document doc : documents) {
                logs.add(doc);
            }
            
            Collections.reverse(logs);

            model.addAttribute("logs", logs);
            model.addAttribute("logType", "System Logs");
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("logs", "Error retrieving logs from database");
            model.addAttribute("logType", "Error");
        }

        return "logs"; 
    }
    
}
