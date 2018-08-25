package com.iotbackend.producer.envc.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.iotbackend.producer.envc.domain.ArgosDeviceEntity;

@Repository
public interface ArgosDeviceRepository extends JpaRepository<ArgosDeviceEntity, String>{

}
