package com.project.management_system.repository;

import com.project.management_system.model.CustomerAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerAddressRepository
        extends JpaRepository<CustomerAddress, Long> {

    List<CustomerAddress> findByCustomerId(Long customerId);

}