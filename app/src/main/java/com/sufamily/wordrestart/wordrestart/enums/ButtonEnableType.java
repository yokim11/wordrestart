package com.sufamily.wordrestart.wordrestart.enums;

/**
 * Created by N1245 on 2015-03-13.
 */
public enum ButtonEnableType {
    DISABLE(false),
    ENABLE(true);

    private boolean value;
    private ButtonEnableType(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }
}
