package com.ityongman.strategy.promotion.impl;

import com.ityongman.strategy.promotion.AbstractPromotion;

public class DiscountPromotion extends AbstractPromotion {

    @Override
    public double getBalance() {
        System.out.println("DiscountPromotion getBalance ...");
        return 200;
    }
}
