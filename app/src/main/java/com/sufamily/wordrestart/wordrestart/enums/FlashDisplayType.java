package com.sufamily.wordrestart.wordrestart.enums;

/**
 * Created by N1245 on 2015-02-26.
 */
public enum FlashDisplayType {
    ONE("One", 1),
    TWO("Two", 2),
    THREE("Three", 3);

    private String stringValue;
    private int intValue;
    private FlashDisplayType(String toString, int value) {
        stringValue = toString;
        intValue = value;
    }

    @Override
    public String toString() {
        return stringValue;
    }
}
