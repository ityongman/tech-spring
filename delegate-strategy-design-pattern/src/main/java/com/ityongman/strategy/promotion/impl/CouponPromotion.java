package com.ityongman.strategy.promotion.impl;

import com.ityongman.strategy.promotion.AbstractPromotion;

public class CouponPromotion extends AbstractPromotion {
    @Override
    public double getBalance() {
        System.out.println("CouponPromotion getBalance ...");
        return 250;
    }
}
