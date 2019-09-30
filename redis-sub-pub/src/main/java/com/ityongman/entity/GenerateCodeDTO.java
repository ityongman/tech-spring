package com.ityongman.entity;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 红包兑换码, 类似秒杀、抢红包功能
 */
@Data
public class GenerateCodeDTO {
    /**
     * 可供抢的商品总数量
     */
    @NotNull(message = "totalNumber can't be null")
    @Min(value = 1, message = "totalNumber may not be negative or zero")
    private Integer totalNumber ;

//    /**
//     * 每个人可以抢的数量
//     */
//    @NotNull(message = "sigleNumber can't be null")
//    @Min(value = 1, message = "sigleNumber may not be negative or zero")
//    private Integer sigleNumber ;

    /**
     * 红包总共可以被抢的次数
     */
    @NotNull(message = "totalRobbedTimes can't be null")
    @Min(value = 1, message = "totalRobbedTimes may not be negative or zero")
    private Integer totalRobbedTimes ;

    /**
     * 单个人可以被抢的次数, 默认 1
     */
    @NotNull(message = "sigleRobbedTimes can't be null")
    @Min(value = 1, message = "sigleRobbedTimes may not be negative or zero")
    private Integer sigleRobbedTimes = 1;

    /**
     * 商品可以被强的开始时间 时间戳
     */
    @NotNull(message = "startTime can't be null")
    @Min(value = 1, message = "startTime may not be negative or zero")
    private Long startTime ;

    /**
     * 商品可以被强的结束时间 时间戳
     */
    @NotNull(message = "stopTime can't be null")
    @Min(value = 1, message = "stopTime may not be negative or zero")
    private Long stopTime ;

    /**
     * 商品奖励规则, 0 - 平均 ， 1 - 随机
     */
    @NotNull(message = "type can't be null")
    private Integer rewardType ;

    /**
     * 号码类型, 用于扩展, 该号码是给谁用的
     */
    private Integer codeType ;
}
