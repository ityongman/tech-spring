package com.ityongman.service;

import com.ityongman.entity.dto.GenerateCodeDTO;

public interface ExchangeCodeService {
    /**
     * 生成红包兑换码接口
     * @param code
     * @return
     */
    String generateCode(GenerateCodeDTO code) ;
}
