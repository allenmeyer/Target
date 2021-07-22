package com.target.restservice;

import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.target.repo.product.Product;
import com.target.repo.ProductRepoManager;

import org.bson.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.target.util.Constants.PRODUCT_ID;
import static com.target.util.Constants.NAME;
import static com.target.util.Constants.USD;
import static com.target.util.TestConstants.TEST_ID;
import static com.target.util.TestConstants.TEST_NAME;
import static com.target.util.TestConstants.VALUE_JSON_PATH;
import static com.target.util.TestConstants.CURRENCY_CODE_JSON_PATH;
import static com.target.util.TestConstants.FAKE_ID;
import static com.target.util.TestConstants.TEST_VALUE;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@WebMvcTest(APIController.class)
@TestInstance(Lifecycle.PER_CLASS)
class RestServiceApplicationTests {

	@Autowired
	private MockMvc mockMvc;


	private ObjectMapper mapper;

	@MockBean
	private ProductRepoManager mockProductRepoManager;

	private Product testProduct;

	@BeforeAll
	public void init() {
		mapper = new ObjectMapper();
		testProduct = new Product(TEST_ID, TEST_NAME, TEST_VALUE, USD);
	}

	@Test
	public void testGetObject() throws Exception {
		Mockito.when(mockProductRepoManager.getObject(TEST_ID))
			.thenReturn(new Document(mapper.convertValue(testProduct, new TypeReference<Map<String, Object>>() {})));

		mockMvc.perform(MockMvcRequestBuilders
			.get(String.format("/products/%s", TEST_ID)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath(PRODUCT_ID, is(TEST_ID)))
			.andExpect(jsonPath(NAME, is(TEST_NAME)))
			.andExpect(jsonPath(VALUE_JSON_PATH, is(Double.valueOf(10.00))))
			.andExpect(jsonPath(CURRENCY_CODE_JSON_PATH, is(USD)));
	}

	@Test
	public void testGetObject_NotExists() throws Exception {
		Mockito.when(mockProductRepoManager.getObject(FAKE_ID)).thenReturn(null);

		mockMvc.perform(MockMvcRequestBuilders
			.get(String.format("/products/%s", FAKE_ID)))
			.andExpect(status().isNotFound());
	}

	@Test
	public void testPutObject() throws Exception {
		Mockito.when(mockProductRepoManager.putObject(eq(TEST_ID), any(Product.class)))
			.thenReturn(new Document(mapper.convertValue(testProduct, new TypeReference<Map<String, Object>>() {})));

		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/products/{id}", TEST_ID)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsString(testProduct));

		mockMvc.perform(mockRequest)
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath(PRODUCT_ID, is(TEST_ID)))
			.andExpect(jsonPath(NAME, is(TEST_NAME)))
			.andExpect(jsonPath(VALUE_JSON_PATH, is(Double.valueOf(10.00))))
			.andExpect(jsonPath(CURRENCY_CODE_JSON_PATH, is(USD)));
	}

	@Test
	public void testPutObject_badRequest() throws Exception {
		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put(String.format("/products/%s", TEST_ID))
		.contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON);

		mockMvc.perform(mockRequest)
			.andExpect(status().isBadRequest());
	}
}
