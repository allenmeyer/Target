package com.target.repo.product;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

import static com.target.util.Constants.VALUE;
import static com.target.util.Constants.CURRENCY_CODE;

@Getter
@NoArgsConstructor
public class Product {
    private String id;
    private String name;
    private Map<String, Object> current_price;

    public Product(String id, String name, double value, String currencyCode) {
        this.id = id;
        this.name = name;
        this.current_price = new HashMap<>();
        this.current_price.put(VALUE, value);
        this.current_price.put(CURRENCY_CODE, currencyCode);
    }

    @Override
    public String toString() {
        return String.format("id:%s,name:%s,current_price:{value:%s,currency_code:%s}", 
            this.id, this.name, this.current_price.get(VALUE), this.current_price.get(CURRENCY_CODE));
    }
}
