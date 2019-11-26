package com.ityongman.ctr;

import com.ityongman.annotation.MyAutowired;
import com.ityongman.annotation.MyController;
import com.ityongman.annotation.MyRequestMapping;
import com.ityongman.annotation.MyRequestParam;
import com.ityongman.service.ITradeService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@MyController
public class TradeController {

    @MyAutowired
    private ITradeService tradeService ;

    @MyRequestMapping("/query")
    public void query(HttpServletRequest req, HttpServletResponse resp,
                      @MyRequestParam("tid") String tid) {
        String orderInfo = tradeService.query(tid);

        try {
            resp.getWriter().write(orderInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @MyRequestMapping("/add")
    public void add(HttpServletRequest req, HttpServletResponse resp, @MyRequestParam("a") Integer a , @MyRequestParam("b") Integer b) {
        Integer result = a + b ;
        try {
           // resp.getWriter().write(result.intValue()); //Writes a single character
            resp.getWriter().write(a + "+" +b + "=" + result); //Writes a string
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
