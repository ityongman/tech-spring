package com.ityongman.delegate;

import com.ityongman.delegate.httpReq.DispatcherServlet;

public class DispatcherServletTest {
    public static void main(String[] args) {
        DispatcherServlet dispatcher = new DispatcherServlet();

        String ret = dispatcher.doDispatcher("queryOrderById", "10086");
        System.out.println(ret);
    }
}
