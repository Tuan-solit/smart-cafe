package com.module3.ccafe.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class OrderDetailId implements Serializable {
    Long orderId;
    Long productId;
}
