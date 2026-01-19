package edu.miu.mongodemo.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

/**
 * MongoDB Configuration
 * Creates a capped collection for Person entities
 */
@Configuration
public class MongoConfig implements CommandLineRunner {

    @Autowired
    private MongoClient mongoClient;

    @Override
    public void run(String... args) throws Exception {
        // Check if persons collection exists
        String collectionName = "persons";
        MongoDatabase database = mongoClient.getDatabase("contentdb");
        
        boolean collectionExists = database.listCollectionNames()
                .into(new ArrayList<>())
                .contains(collectionName);

        if (!collectionExists) {
            // Create capped collection
            // Size: 10MB, Max documents: 1000
            // Capped collections are fixed-size collections that automatically 
            // overwrite oldest documents when the size limit is reached
            Document createCollectionCmd = new Document("create", collectionName)
                    .append("capped", true)
                    .append("size", 10 * 1024 * 1024) // 10MB in bytes
                    .append("max", 1000); // Maximum 1000 documents
            
            database.runCommand(createCollectionCmd);
            System.out.println("Created capped collection: " + collectionName);
        } else {
            // Check if it's already capped
            Document collStats = database.runCommand(
                new Document("collStats", collectionName)
            );
            
            Boolean isCapped = collStats.getBoolean("capped");
            if (isCapped == null || !isCapped) {
                System.out.println("WARNING: Collection 'persons' exists but is not capped.");
                System.out.println("Dropping existing collection and recreating as capped...");
                
                // Drop the existing non-capped collection
                database.getCollection(collectionName).drop();
                
                // Create new capped collection
                Document createCollectionCmd = new Document("create", collectionName)
                        .append("capped", true)
                        .append("size", 10 * 1024 * 1024) // 10MB in bytes
                        .append("max", 1000); // Maximum 1000 documents
                
                database.runCommand(createCollectionCmd);
                System.out.println("Successfully recreated 'persons' collection as capped.");
            } else {
                System.out.println("Collection 'persons' is already capped.");
            }
        }
    }
}

