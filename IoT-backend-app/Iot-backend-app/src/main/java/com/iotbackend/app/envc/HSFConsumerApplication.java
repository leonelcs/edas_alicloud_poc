package com.iotbackend.app.envc;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.iotbackend.commons.envc.service.IMessageDrivenService;
import com.taobao.pandora.boot.PandoraBootstrap;

@SpringBootApplication
public class HSFConsumerApplication implements CommandLineRunner {
	
	@Autowired
	ApplicationContext context;
	
	public static void main(String[] args) {
		PandoraBootstrap.run(args);
		SpringApplication.run(HSFConsumerApplication.class, args);
		PandoraBootstrap.markStartupAndWait();
	}

	@Override
	public void run(String... arg0) throws Exception {
		String[] beanNames = context.getBeanNamesForType(IMessageDrivenService.class);
		for (String beanName : beanNames) {
			IMessageDrivenService service = context.getBean(beanName,IMessageDrivenService.class);
			service.startAllSubscriptions();
		}
		
	}

}