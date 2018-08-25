package com.iotbackend.producer.envc.domain.repository;

import org.springframework.data.repository.CrudRepository;

import com.iotbackend.producer.envc.domain.ArgosSlaveEntity;
import com.iotbackend.producer.envc.domain.SlaveId;

public interface ArgosSlaveRepository extends CrudRepository<ArgosSlaveEntity, SlaveId>{

}
