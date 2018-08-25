package com.iotbackend.producer.envc;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.iotbackend.commons.envc.service.IMessageDrivenService;
import com.taobao.pandora.boot.PandoraBootstrap;



@SpringBootApplication
@EnableScheduling
public class HSFProviderApplication implements CommandLineRunner
{
	
	@Autowired
	ApplicationContext context;
	

    public static void main(String[] args) {
        // Start Pandora Boot for loading Pandora containers
        PandoraBootstrap.run(args);
        SpringApplication.run(HSFProviderApplication.class, args);
        // The marking service starts and sets the thread wait. Prevent the user's business code from running and exiting, causing the container to exit.
        PandoraBootstrap.markStartupAndWait();
        
    }


	@Override
	public void run(String... arg0) throws Exception {
		startSubscriptions();
	}
	
	@Scheduled(fixedDelayString = "${argos.subscribe.pingMilliseconds}")
	public void startSubscriptions() {
		String[] beanNames = context.getBeanNamesForType(IMessageDrivenService.class);
		System.out.println(beanNames.length);
		for (String beanName : beanNames) {
			System.out.println(beanName);
			IMessageDrivenService service = context.getBean(beanName, IMessageDrivenService.class);
			service.startAllSubscriptions();
		}		
	}

}
