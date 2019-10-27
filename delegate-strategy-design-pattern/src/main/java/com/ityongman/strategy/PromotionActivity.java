package com.ityongman.strategy;

import com.ityongman.strategy.entity.Order;
import com.ityongman.strategy.promotion.AbstractPromotion;
import com.ityongman.strategy.promotion.IPromotion;

/**
 * 通过代理模式对活动进行处理, 并达到对目标方法的增强作用
 */
public class PromotionActivity {
    private AbstractPromotion promotion ;

    public PromotionActivity(AbstractPromotion promotion){
        this.promotion = promotion ;
    }


    void pay(Order order) {
        System.out.println("PromotionActivity pay ...");
        promotion.pay(order);
    }
}
