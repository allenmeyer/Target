package com.target.repo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.target.repo.product.Product;

import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import static com.target.util.Constants.MONGO_DB_NAME;
import static com.target.util.Constants.MONGO_DB_PRODUCT_COLLECTION_NAME;
import static com.target.util.Constants.PRODUCT_ID;
import static com.target.util.Constants.VALUE;
import static com.target.util.Constants.USD;
import static com.target.util.Constants.CURRENCY_CODE;
import static com.target.util.Constants.CURRENT_PRICE;
import static com.target.util.TestConstants.TEST_ID;
import static com.target.util.TestConstants.TEST_NAME;
import static com.target.util.TestConstants.TEST_VALUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

@TestInstance(Lifecycle.PER_CLASS)
public class ProductRepoManagerTests {

    private MongoClient mockMongoClient;
    private MongoCollection<Document> mockMongoCollection;
    private MongoDatabase mockMongoDatabase;
    private ProductRepoManager mockProductRepoManager;

    private Product testProduct;
    private ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    public void init() {
        mockMongoClient = mock(MongoClient.class);
        mockMongoDatabase = mock(MongoDatabase.class);
        mockMongoCollection = mock(MongoCollection.class);
        when(mockMongoClient.getDatabase(MONGO_DB_NAME)).thenReturn(mockMongoDatabase);
        when(mockMongoDatabase.getCollection(MONGO_DB_PRODUCT_COLLECTION_NAME)).thenReturn(mockMongoCollection);
        mockProductRepoManager = new ProductRepoManager(mockMongoClient);
    }

    @BeforeEach
    public void resetTestProduct() {
        testProduct = new Product(TEST_ID, TEST_NAME, TEST_VALUE, USD);
    }

    @Test
    public void testGetObject() throws Exception {
        final Bson filter = eq(PRODUCT_ID, TEST_ID);

        FindIterable iterable = mock(FindIterable.class);

        when(mockMongoCollection.find(filter)).thenReturn(iterable);
        when(iterable.first()).thenReturn(new Document(mapper.convertValue(testProduct, new TypeReference<Map<String, Object>>() {})));

        Object ret = mockProductRepoManager.putObject(TEST_ID, testProduct);
        Map<String, Object> retMap = mapper.convertValue(ret, new TypeReference<Map<String, Object>>() {});
        assertEquals(TEST_ID, retMap.get(PRODUCT_ID).toString());
        assertEquals(10.00, ((Map) retMap.get(CURRENT_PRICE)).get(VALUE));
        assertEquals(USD, ((Map) retMap.get(CURRENT_PRICE)).get(CURRENCY_CODE));
    }
    
    @Test
    public void testPutObject_objectNotExists() throws Exception {
        final Bson filter = eq(PRODUCT_ID, TEST_ID);
        
        final Bson update = set(CURRENT_PRICE, testProduct.getCurrent_price());

        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
        options.returnDocument(ReturnDocument.AFTER);

        when(mockMongoCollection.findOneAndUpdate(filter, update, options)).thenReturn(null);
        when(mockMongoCollection.insertOne(
            new Document(mapper.convertValue(testProduct, new TypeReference<Map<String, Object>>() {}))))
            .thenReturn(null);

        Object ret = mockProductRepoManager.putObject(TEST_ID, testProduct);
        Map<String, Object> retMap = mapper.convertValue(ret, new TypeReference<Map<String, Object>>() {});
        assertEquals(TEST_ID, retMap.get(PRODUCT_ID).toString());
        assertEquals(10.00, ((Map) retMap.get(CURRENT_PRICE)).get(VALUE));
        assertEquals(USD, ((Map) retMap.get(CURRENT_PRICE)).get(CURRENCY_CODE));
    }

    @Test
    public void testPutObject_objectExists() throws Exception {
        Product newPricedProduct = new Product(TEST_ID, TEST_NAME, 15.00, USD);

        final Bson filter = eq(PRODUCT_ID, TEST_ID);

        final Bson update = set(CURRENT_PRICE, newPricedProduct.getCurrent_price());

        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
        options.returnDocument(ReturnDocument.AFTER);


        when(mockMongoCollection.findOneAndUpdate(filter, update, options))
            .thenReturn(new Document(mapper.convertValue(newPricedProduct, new TypeReference<Map<String, Object>>() {})));

        Object ret = mockProductRepoManager.putObject(TEST_ID, newPricedProduct);
        Map<String, Object> retMap = mapper.convertValue(ret, new TypeReference<Map<String, Object>>() {});
        Map<String, Object> currPrice = mapper.convertValue(retMap.get(CURRENT_PRICE), new TypeReference<Map<String, Object>>() {});
        assertEquals(TEST_ID, retMap.get(PRODUCT_ID).toString());
        assertEquals(15.00, currPrice.get(VALUE));
        assertEquals(USD, currPrice.get(CURRENCY_CODE));
    }
}
