package com.sufamily.wordrestart.wordrestart.domain.model;

/**
 * Created by N1245 on 2015-03-10.
 */
public class TestResultInfo {
    private int successCount;
    private int failCount;

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getFailCount() {
        return failCount;
    }

    public void setFailCount(int failCount) {
        this.failCount = failCount;
    }
    public int addSuccessCount() {
        this.successCount += 1;
        return this.successCount;
    }
    public int addFailCount() {
        this.failCount += 1;
        return this.failCount;
    }
}
