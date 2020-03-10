package com;

import com.ityongman.starter.configuration.AddressProperties;
import com.ityongman.starter.format.AddressProcessor;

/**
 * @Author shedunze
 * @Date 2020-03-09 16:58
 * @Description
 */
public class AddressFormat {
    /**
     * 已经配置的地址信息
     */
    private AddressProperties addressProperties ;
    /**
     * 序列化方式
     */
    private AddressProcessor addressProcessor ;

    public AddressFormat(AddressProperties addressProperties , AddressProcessor addressProcessor) {
        this.addressProperties = addressProperties ;
        this.addressProcessor = addressProcessor ;
    }

    public <T> String doFormat(T obj) {
        StringBuilder sb = new StringBuilder() ;

        sb.append("format begin:").append("<br>");
        sb.append("exist address config: ").append(addressProcessor.format(addressProperties.getAddress())).append("<br>");
        sb.append("requet address :").append(addressProcessor.format(obj)).append("<br>");
        sb.append(System.currentTimeMillis()).append("<br>");

        return sb.toString() ;
    }
}
