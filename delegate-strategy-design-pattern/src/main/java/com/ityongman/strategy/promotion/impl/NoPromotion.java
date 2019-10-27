package com.ityongman.strategy.promotion.impl;

import com.ityongman.strategy.promotion.AbstractPromotion;

public class NoPromotion extends AbstractPromotion {
    @Override
    public double getBalance() {
        System.out.println("NoPromotion getBalance ...");
        return 100;
    }
}
