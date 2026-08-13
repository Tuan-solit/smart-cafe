package com.module3.ccafe.repository;

import com.module3.ccafe.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository <Order,Integer>{
}
