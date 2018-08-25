package com.iotbackend.app.envc;

import org.springframework.context.annotation.Configuration;

import com.alibaba.boot.hsf.annotation.HSFConsumer;
import com.iotbackend.commons.envc.AsyncEchoService;
import com.iotbackend.commons.envc.service.AlarmRPCService;
import com.iotbackend.commons.envc.service.AnnounceRPCService;
import com.iotbackend.commons.envc.service.EchoService;
import com.iotbackend.commons.envc.service.FunctionsRPCService;
import com.iotbackend.commons.envc.service.MQttSubscriberService;
import com.iotbackend.commons.envc.service.SlavelistRPCService;

/**
 * Created by leonel on 2018/05/29.
 */
@Configuration
public class HsfConfig {

    @HSFConsumer(clientTimeout = 3000, serviceVersion = "1.0.0")
    private EchoService echoService;
    
    @HSFConsumer(clientTimeout = 3000, serviceVersion = "1.0.0")
    private MQttSubscriberService mqttService;

    @HSFConsumer(clientTimeout = 3000, serviceVersion = "1.0.0")
    private AnnounceRPCService announceService;
    
    @HSFConsumer(clientTimeout = 3000, serviceVersion = "1.0.0", futureMethods = "future")
    private AsyncEchoService asyncEchoService;
    
    @HSFConsumer(clientTimeout = 3000, serviceVersion = "1.0.0")
    private FunctionsRPCService functionsService;
    
    @HSFConsumer(clientTimeout = 3000, serviceVersion = "1.0.0")
    private AlarmRPCService alarmService;
    
    @HSFConsumer(clientTimeout = 3000, serviceVersion = "1.0.0")
    private SlavelistRPCService slavelistService;
}
