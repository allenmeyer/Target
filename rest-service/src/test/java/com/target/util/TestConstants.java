package com.target.util;

import static com.target.util.Constants.VALUE;
import static com.target.util.Constants.CURRENCY_CODE;
import static com.target.util.Constants.CURRENT_PRICE;

public class TestConstants {
    public static final String TEST_ID = "1234";
    public static final String TEST_NAME = "books";
    public static final Double TEST_VALUE = 10.00;
    public static final String FAKE_ID = "0000";

    public static final String VALUE_JSON_PATH = String.format("%s.%s", CURRENT_PRICE, VALUE);
	public static final String CURRENCY_CODE_JSON_PATH = String.format("%s.%s", CURRENT_PRICE, CURRENCY_CODE);
}
