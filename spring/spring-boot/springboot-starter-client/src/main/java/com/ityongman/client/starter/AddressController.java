package com.ityongman.client.starter;

import com.AddressFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author shedunze
 * @Date 2020-03-10 09:01
 * @Description
 */
@RestController
public class AddressController {

    @Autowired
    private AddressFormat addressFormat ;

    @GetMapping("/address")
    public String getAddress() {
        Address address = new Address() ;

        address.setCountry("中国");
        address.setProvice("江苏");
        address.setCity("苏州");
        address.setOrg("移动");

        return addressFormat.doFormat(address) ;
    }
}
