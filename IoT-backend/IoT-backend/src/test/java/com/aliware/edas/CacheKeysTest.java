package com.aliware.edas;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.iotbackend.commons.envc.dto.MappingJsonDto.LocalType;
import com.iotbackend.producer.envc.HSFProviderApplication;
import com.iotbackend.producer.envc.cache.repository.impl.DeviceFunctionRepositoryImpl;
import com.taobao.pandora.boot.test.junit4.DelegateTo;
import com.taobao.pandora.boot.test.junit4.PandoraBootRunner;

@RunWith(PandoraBootRunner.class)
@DelegateTo(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {HSFProviderApplication.class, CacheKeysTest.class })
@Component
public class CacheKeysTest {
	
	@Autowired
	DeviceFunctionRepositoryImpl repository;
	
	@Test
	public void testCreateCustomKey() {
		LocalType groupIdType = LocalType.BUILDING;
		LocalType location = LocalType.DEPARTMENT;
		String groupId = "1";
		String controlType = "Ventilation";
		
		String result;
		try {
			result = repository.createCustomKey(groupIdType, groupId, location, controlType);
			System.out.println(result);
			assertEquals(result,"dc:*:1:*:*::Ventilation:*:*");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertEquals(0, 1);
		}

	}
}
