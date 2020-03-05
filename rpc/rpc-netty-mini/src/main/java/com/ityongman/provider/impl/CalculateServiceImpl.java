package com.ityongman.provider.impl;

import com.ityongman.api.ICalculateService;

/**
 * @Author shedunze
 * @Date 2020-03-02 15:41
 * @Description 计算实现类
 */
public class CalculateServiceImpl implements ICalculateService {
    @Override
    public int add(int a, int b) {
        return a + b;
    }

    @Override
    public int sub(int a, int b) {
        return a - b;
    }

    @Override
    public int mult(int a, int b) {
        return a * b;
    }

    @Override
    public int div(int a, int b) {
        return a / b;
    }
}
