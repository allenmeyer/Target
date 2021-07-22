package com.target.restservice;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import com.target.repo.product.Product;
import com.target.repo.ProductRepoManager;

import static com.target.util.Constants.VALUE;
import static com.target.util.Constants.CURRENCY_CODE;;

@RestController
public class APIController {
    private ProductRepoManager productRepoManager;
    private static final Logger log = LogManager.getLogger(APIController.class);

    @Autowired
    public APIController(final ProductRepoManager productRepoManager) {
        this.productRepoManager = productRepoManager;
    }

    @GetMapping(value = "/products/{id}")
    public Object getObject(@PathVariable("id") final String productId) {
        Object result = productRepoManager.getObject(productId);
        if (result != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            return new ResponseEntity<>(result, headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(value = "/products/{id}")
    public Object putObject(@PathVariable("id") final String productId, @RequestBody Product productInfo) {
        if (productInfo == null || productInfo.getId() == null || productInfo.getCurrent_price() == null
             || !productInfo.getCurrent_price().containsKey(VALUE) || !productInfo.getCurrent_price().containsKey(CURRENCY_CODE)) {
            log.info(String.format("Bad request, productInfo: %s", productInfo.toString()));
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Object result = productRepoManager.putObject(productId, productInfo);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(result, headers, HttpStatus.OK);
    }
}