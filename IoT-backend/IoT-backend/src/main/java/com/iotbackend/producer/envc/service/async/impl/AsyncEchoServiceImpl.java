package com.iotbackend.producer.envc.service.async.impl;

import com.alibaba.boot.hsf.annotation.HSFProvider;
import com.iotbackend.commons.envc.AsyncEchoService;

@HSFProvider(serviceInterface = AsyncEchoService.class, serviceVersion = "1.0.0")
public class AsyncEchoServiceImpl implements AsyncEchoService {
    @Override
    public String future(String string) {
        return string;
    }

    @Override
    public String callback(String string) {
        return string;
    }
}
