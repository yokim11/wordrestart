package com.sufamily.wordrestart.wordrestart.domain.model;

/**
 * Created by N1245 on 2015-03-06.
 */
public class LevelWordInfo {
    private long levelTotalCount;
    private long basketCount1;
    private long basketCount2;
    private long basketCount3;
    private long basketCount4;
    private long basketCount5;

    public long getLevelTotalCount() {
        return levelTotalCount;
    }

    public void setLevelTotalCount(long levelTotalCount) {
        this.levelTotalCount = levelTotalCount;
    }

    public long getBasketCount1() {
        return basketCount1;
    }

    public void setBasketCount1(long basketCount1) {
        this.basketCount1 = basketCount1;
    }

    public long getBasketCount2() {
        return basketCount2;
    }

    public void setBasketCount2(long basketCount2) {
        this.basketCount2 = basketCount2;
    }

    public long getBasketCount3() {
        return basketCount3;
    }

    public void setBasketCount3(long basketCount3) {
        this.basketCount3 = basketCount3;
    }

    public long getBasketCount4() {
        return basketCount4;
    }

    public void setBasketCount4(long basketCount4) {
        this.basketCount4 = basketCount4;
    }

    public long getBasketCount5() {
        return basketCount5;
    }

    public void setBasketCount5(long basketCount5) {
        this.basketCount5 = basketCount5;
    }

    public long getTotal() {
        return (this.basketCount1 + this.basketCount2 + this.basketCount3 + this.basketCount4 + this.basketCount5);
    }
    public long getWorkingTotal() {
        return (this.basketCount1 + this.basketCount2 + this.basketCount3 + this.basketCount4);
    }

}
