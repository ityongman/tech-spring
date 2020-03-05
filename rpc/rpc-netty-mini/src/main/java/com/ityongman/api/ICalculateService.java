package com.ityongman.api;

/**
 * @Author shedunze
 * @Date 2020-03-02 15:31
 * @Description 提供计算相关的服务
 */
public interface ICalculateService {
    int add(int a, int b);

    int sub(int a, int b);

    int mult(int a, int b);

    int div(int a , int b);
}
