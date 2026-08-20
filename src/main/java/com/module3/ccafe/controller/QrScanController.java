package com.module3.ccafe.controller;

import com.module3.ccafe.entity.CafeTable;
import com.module3.ccafe.entity.enums.TableStatus;
import com.module3.ccafe.repository.CafeTableRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/qr-scan")
@RequiredArgsConstructor
public class QrScanController {
    private final CafeTableRepository cafeTableRepository;

    @GetMapping
    public String scanQr(@RequestParam Integer tableId, HttpSession session){
        CafeTable table = cafeTableRepository.findById(tableId).orElseThrow(()->new IllegalArgumentException("Bàn không tồn tại"));
        if (table.getStatus() == TableStatus.IN_SERVICE) {
            throw new IllegalStateException("Bàn đang được sử dụng");
        }
        table.setStatus(TableStatus.IN_SERVICE);
        cafeTableRepository.save(table);
        session.removeAttribute("ORDER_ID");
        session.removeAttribute("CART");
        session.setAttribute("TABLE_ID", tableId);
        return "redirect:/menu";
    }
}
