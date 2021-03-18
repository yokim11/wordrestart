package com.sufamily.wordrestart.wordrestart.domain.model.command;

/**
 * Created by N1245 on 2015-03-10.
 */
public class AddBasketCommand {
    private String level;
    private int basketNo;
    private int lastSeq;
    private int limit;

    public int getBasketNo() {
        return basketNo;
    }

    public void setBasketNo(int basketNo) {
        this.basketNo = basketNo;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getLastSeq() {
        return lastSeq;
    }

    public void setLastSeq(int lastSeq) {
        this.lastSeq = lastSeq;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
