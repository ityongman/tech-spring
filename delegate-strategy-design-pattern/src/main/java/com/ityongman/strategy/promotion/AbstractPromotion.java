package com.ityongman.strategy.promotion;

import com.ityongman.strategy.entity.Order;

public abstract class AbstractPromotion implements IPromotion{

    public void pay(Order order){
        double balance = getBalance();
        double amount = order.getAmount();

        if(balance < amount){
            System.out.println("account balance not enough");
        } else {
            System.out.println("account balance enough, balance = "+balance+" , amount = " + amount);
        }
    }
}
