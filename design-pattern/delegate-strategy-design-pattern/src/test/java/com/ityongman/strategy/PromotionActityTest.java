package com.ityongman.strategy;

import com.ityongman.strategy.entity.Order;

public class PromotionActityTest {
    public static void main(String[] args) {
        Order order = new Order();

        order.setAmount(200);
        order.setId("10086");
        order.setOrderId("orderId:1000000");

        PromotionActivity activity =
                new PromotionActivity(PromotionStrategy.getPromotion(PromotionStrategy.PromotionStrategyKey.NO));

        activity.pay(order);
    }
}
