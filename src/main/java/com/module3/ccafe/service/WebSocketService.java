package com.module3.ccafe.service;

import com.module3.ccafe.dto.request.CashPaymentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketService {
    private final SimpMessagingTemplate messagingTemplate;
    
    public void sendCashPaymentRequest(CashPaymentRequest request){
        messagingTemplate.convertAndSend("/topic/payment/cash", request);
    }
}
