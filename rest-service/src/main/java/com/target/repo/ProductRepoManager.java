package com.target.repo;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.target.repo.product.Product;
import com.mongodb.client.MongoCollection;

import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Map;

import static com.target.util.Constants.MONGO_DB_NAME;
import static com.target.util.Constants.MONGO_DB_PRODUCT_COLLECTION_NAME;
import static com.target.util.Constants.PRODUCT_ID;
import static com.target.util.Constants.CURRENT_PRICE;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

@Component
public class ProductRepoManager {
    
    private static final Logger log = LogManager.getLogger(ProductRepoManager.class);

    private final MongoClient mongoClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ProductRepoManager() {
        // by default creates a connection with mongodb://localhost:27017
        this.mongoClient = MongoClients.create();
    }

    // For unit tests
    public ProductRepoManager(final MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public Object getObject(String productId) {
        final MongoCollection<Document> coll = mongoClient.getDatabase(MONGO_DB_NAME).getCollection(MONGO_DB_PRODUCT_COLLECTION_NAME);
        Bson filter = eq(PRODUCT_ID, productId);

        final Document result = coll.find(filter).first();
        log.info(String.format("Searched for item with id: %s, found: %s", productId, result));
        return result;
    }

    public Object putObject(String productId, Product productInfo) {
        final MongoCollection<Document> coll = mongoClient.getDatabase(MONGO_DB_NAME).getCollection(MONGO_DB_PRODUCT_COLLECTION_NAME);
        Bson filter = eq(PRODUCT_ID, productId);

        final Document doc = new Document(objectMapper.convertValue(productInfo, new TypeReference<Map<String, Object>>() {}));
        final Object prices = doc.get(CURRENT_PRICE);
        Bson update = set(CURRENT_PRICE, prices);

        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
        options.returnDocument(ReturnDocument.AFTER);
        final Document result = coll.findOneAndUpdate(filter, update, options);

        if (result == null) {
            log.info(String.format("Could not find document with id: %s, so added document: %s", productId, productInfo));
            coll.insertOne(doc);
            return doc;
        }
        log.info(String.format("Found document with id: %s, returning document: %s", productId, result));
        return result;
    }

}