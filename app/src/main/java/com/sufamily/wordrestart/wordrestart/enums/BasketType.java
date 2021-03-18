package com.sufamily.wordrestart.wordrestart.enums;

/**
 * Created by N1245 on 2015-03-11.
 */
public enum BasketType {
    BASKET_NO1("BASKET_NO1", 1),
    BASKET_NO2("BASKET_NO2", 2),
    BASKET_NO3("BASKET_NO3", 3),
    BASKET_NO4("BASKET_NO4", 4),
    COMPLETED_BASKET("COMPLETED_BASKET", 5);

    private String stringValue;
    private int intValue;
    private BasketType(String toString, int value) {
        this.stringValue = toString;
        this.intValue = value;
    }

    @Override
    public String toString() {
        return stringValue;
    }

    public int getIntValue() {
        return intValue;
    }
}
