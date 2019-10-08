package com.ityongman.service.impl;

import com.alibaba.fastjson.JSON;
import com.ityongman.common.CodeException;
import com.ityongman.common.CodeUtil;
import com.ityongman.common.ErrorCode;
import com.ityongman.entity.dto.GenerateCodeDTO;
import com.ityongman.service.ExchangeCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


@Service
public class ExchangeCodeServiceImpl implements ExchangeCodeService {
    private Logger logger = LoggerFactory.getLogger(ExchangeCodeServiceImpl.class);

    @Autowired
    private RedisTemplate redisTemplate ;

    private static int HALF_HOUR = 30 * 60 * 1000 ; // 半小时
    /**
     * 生成兑换码
     * @param code
     * @return
     */
    @Override
    public String generateCode(GenerateCodeDTO code) {
        /**
         * 如果想用aop可以参考下面的连接
         * @link
         */
        logger.info("ExchangeCodeServiceImpl generateCode params = {}", JSON.toJSONString(code));
        //1. 校验参数
        validateCodeParams(code);
        //2. 生成code码
        String generateCode = CodeUtil.generateCode(code.getCodeType()) ;
        //3. 存入redis中
        String codeKey = "code_" + code.getCodeType() + "_" + generateCode ;
        String codeValue = JSON.toJSONString(code);
        redisTemplate.opsForValue().set(codeKey, codeValue);
        redisTemplate.expire(codeKey,1 * 24 * 60 * 60, TimeUnit.SECONDS); // TODO 这里用一天来表示, 有需要可以自己添加逻辑

        return generateCode;
    }

    /**
     * 校验入参, 在service校验, 如果存在Manger层, 可以在manager层校验
     * @param code
     * @return
     */
    private void validateCodeParams(GenerateCodeDTO code) {
        /**
         * 1. 活动开始时间需要 小于 结束时间,
         * 2. 间隔最好不要小于 30 mins
         * 3. 活动开始时间不能小于当前时间
         * 4. 活动结束时间不能小于当前时间
         */
        if (code.getStartTime().longValue() >= code.getStopTime().longValue()) {
            throw new CodeException(ErrorCode.PARAM_ERR, "活动开始时间需要小于结束时间");
        }
        if(code.getStartTime().longValue() + HALF_HOUR > code.getStopTime()) {
            throw new CodeException(ErrorCode.PARAM_ERR, "活动开始时间和结束时间间隔不能小于30mins");
        }
        if(code.getStartTime().longValue() <= System.currentTimeMillis()
                || code.getStopTime() <= System.currentTimeMillis()) {
            throw new CodeException(ErrorCode.PARAM_ERR, "活动开始时间不能小于当前时间, 或活动结束时间不能小于当前时间");
        }
    }
}
