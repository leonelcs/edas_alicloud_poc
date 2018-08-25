package com.iotbackend.commons.envc;

public interface AsyncEchoService {
    String future(String string);
    String callback(String string);
}
