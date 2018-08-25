package com.iotbackend.producer.envc.service.impl;

import com.alibaba.boot.hsf.annotation.HSFProvider;
import com.iotbackend.commons.envc.service.EchoService;

/**
 * Created by Leonel on 2017/12/11.
 */
@HSFProvider(serviceInterface = com.iotbackend.commons.envc.service.EchoService.class, serviceVersion = "1.0.0")
public class EchoServiceImpl implements EchoService {
    @Override
    public String echo(String string) {
        return string;
    }
}
