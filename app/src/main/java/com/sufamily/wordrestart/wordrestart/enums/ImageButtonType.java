package com.sufamily.wordrestart.wordrestart.enums;

/**
 * Created by N1245 on 2015-03-13.
 */
public enum ImageButtonType {
    DISABLE(false),
    ENABLE(true);

    private boolean value;
    private ImageButtonType(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }
}
