package com.module3.ccafe.service;

import com.module3.ccafe.dto.request.CreateOrderByEmployeeRequest;
import com.module3.ccafe.entity.CafeTable;
import com.module3.ccafe.entity.Order;
import com.module3.ccafe.entity.User;
import com.module3.ccafe.repository.CafeTableRepository;
import com.module3.ccafe.repository.OrderRepository;
import com.module3.ccafe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrderService {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    CafeTableRepository cafeTableRepository;
    @Autowired
    UserRepository userRepository;

    public boolean createOrderByEmployee(CreateOrderByEmployeeRequest createOrderByEmployeeRequest){
        Order order = new Order();

        CafeTable cafeTable = cafeTableRepository.findById(createOrderByEmployeeRequest.getIdBan()).orElseThrow();
        User user = userRepository.findById(createOrderByEmployeeRequest.getIdEmployee()).orElseThrow();

        order.setTable(cafeTable);
        order.setCreatedAt(LocalDateTime.now());
        order.setUser(user);
        orderRepository.save(order);
        return true;
    }
}
