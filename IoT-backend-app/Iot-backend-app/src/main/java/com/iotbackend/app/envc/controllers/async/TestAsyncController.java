package com.iotbackend.app.envc.controllers.async;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iotbackend.app.envc.controllers.SimpleController;
import com.iotbackend.commons.envc.AsyncEchoService;
import com.taobao.hsf.tbremoting.invoke.CallbackInvocationContext;
import com.taobao.hsf.tbremoting.invoke.HSFFuture;
import com.taobao.hsf.tbremoting.invoke.HSFResponseFuture;

/**
 * Created by yizhan on 2017/12/12.
 */
@RestController
public class TestAsyncController {
	
	private static final Logger logger = LoggerFactory.getLogger(SimpleController.class);

    @Autowired
    private AsyncEchoService asyncEchoService;


    @RequestMapping(value = "/hsf-future/{str}", method = RequestMethod.GET)
    public String testFuture(@PathVariable String str) {
    	logger.debug("it's being logged");
        String str1 = asyncEchoService.future(str);
        String str2;
        try {
            HSFFuture hsfFuture = HSFResponseFuture.getFuture();
            str2 = (String) hsfFuture.getResponse(3000);
        } catch (Throwable t) {
            t.printStackTrace();
            str2 = "future-exception";
        }
        return str1 + "  " + str2;
    }


    @RequestMapping(value = "/hsf-future-list/{str}", method = RequestMethod.GET)
    public String testFutureList(@PathVariable String str) {
    	
        try {

            int num = Integer.parseInt(str);

            List<String> params = new ArrayList<String>();
            for (int i = 1; i <= num; i++) {
                params.add(i + "");
            }

            List<HSFFuture> hsfFutures = new ArrayList<HSFFuture>();

            for (String param : params) {
                asyncEchoService.future(param);
                hsfFutures.add(HSFResponseFuture.getFuture());
            }


            ArrayList<String> results = new ArrayList<String>();

            for (HSFFuture hsfFuture : hsfFutures) {
                results.add((String) hsfFuture.getResponse(3000));
            }

            return Arrays.toString(results.toArray());

        } catch (Throwable t) {
            return "exception";
        }
    }


    @RequestMapping(value = "/hsf-callback/{str}", method = RequestMethod.GET)
    public String testCallback(@PathVariable String str) {

        String timestamp = System.currentTimeMillis() + "";

        CallbackInvocationContext.setContext(timestamp);
        String str1 = asyncEchoService.callback(str);
        CallbackInvocationContext.setContext(null);

        return str1 + "  " + timestamp;
    }
}
