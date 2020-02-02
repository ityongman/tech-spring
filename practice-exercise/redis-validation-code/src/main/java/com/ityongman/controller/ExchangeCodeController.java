package com.ityongman.controller;

import com.ityongman.common.CodeException;
import com.ityongman.common.Result;
import com.ityongman.entity.dto.GenerateCodeDTO;
import com.ityongman.entity.vo.CodeRewardVO;
import com.ityongman.service.ExchangeCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 兑换码相关控制层
 */
@RestController
@RequestMapping("/exchange/code")
public class ExchangeCodeController {

    @Autowired
    private ExchangeCodeService exchangeCodeService ;

//    @Autowired
//    private RedisTemplate redisTemplate ;

    @RequestMapping(value = "generate", method = RequestMethod.POST)
    public Result<String> generateCode(@RequestBody GenerateCodeDTO code) {
        try {
            return Result.succeedResult(exchangeCodeService.generateCode(code));
        } catch (Exception e) {
            if(e instanceof CodeException) {
                CodeException ce = (CodeException) e;
                return Result.failResult(ce.getErrCode(), ce.getMessage()) ;
            }

            return Result.failResult(-1, "System Erorr !!!");
        }
    }

    @RequestMapping(value = "validate", method = RequestMethod.GET)
    public Result<CodeRewardVO> validate(Long userId, String code) {
        // TODO
        return null ;
    }

}
