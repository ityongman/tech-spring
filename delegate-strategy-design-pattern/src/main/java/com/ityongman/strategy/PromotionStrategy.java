package com.ityongman.strategy;

import com.ityongman.strategy.promotion.AbstractPromotion;
import com.ityongman.strategy.promotion.IPromotion;
import com.ityongman.strategy.promotion.impl.CouponPromotion;
import com.ityongman.strategy.promotion.impl.DiscountPromotion;
import com.ityongman.strategy.promotion.impl.NoPromotion;

import java.util.HashMap;
import java.util.Map;

/**
 * 策略模式
 * 1. 通过工厂方式创建促销活动
 * 2. 通过单例模式(容器式), 保证所有活动唯一, 不可变
 */
public class PromotionStrategy {
    //1. 构造方法私有
    private PromotionStrategy() {}

    //2. 单例容器
    private final static Map<String, AbstractPromotion> promotions = new HashMap<>();

    static {
        promotions.put(PromotionStrategyKey.COUPON, new CouponPromotion());
        promotions.put(PromotionStrategyKey.DISCOUNT, new DiscountPromotion());
        promotions.put(PromotionStrategyKey.NO, new NoPromotion());
    }

    //3. 全局访问接口
    public static AbstractPromotion getPromotion(String promotionMethod) {
        AbstractPromotion promotion = promotions.get(promotionMethod);

        return promotion == null ? promotions.get(PromotionStrategyKey.NO) : promotion ;
    }


    // 为了方便, 这里使用String
    public class PromotionStrategyKey{
        public static final String COUPON = "COUPON";
        public static final String DISCOUNT = "DISCOUNT";
        public static final String NO = "NO";
    }
}
