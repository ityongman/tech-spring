package com.ityongman.entity.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * 用户获得奖励响应数据
 */
@Getter
@Setter
public class CodeRewardVO {
    /**
     * 获得的用户id
     */
    private Long userId ;

    /**
     * 获得奖励时的时间戳
     */
    private Long getTimeStamp ;

    /**
     * 获得的奖励数量
     */
    private Integer rewardCount ;
}
