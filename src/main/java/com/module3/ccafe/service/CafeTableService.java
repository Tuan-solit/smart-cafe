package com.module3.ccafe.service;

import com.module3.ccafe.dto.request.SearchCafeTableRequest;
import com.module3.ccafe.dto.response.SearchCafeTableResponse;
import com.module3.ccafe.entity.CafeTable;
import com.module3.ccafe.entity.Order;
import com.module3.ccafe.entity.enums.OrderStatus;
import com.module3.ccafe.entity.enums.TableStatus;
import com.module3.ccafe.repository.CafeTableRepository;
import com.module3.ccafe.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CafeTableService {
    final CafeTableRepository cafeTableRepository;
    final OrderRepository orderRepository;



    public Page<SearchCafeTableResponse> search(SearchCafeTableRequest searchCafeTableRequest, Pageable pageable){
        TableStatus tableStatus = null;
        if (searchCafeTableRequest.getStatus() != null && !searchCafeTableRequest.getStatus().isBlank()) {
            tableStatus = TableStatus.valueOf(searchCafeTableRequest.getStatus().toUpperCase());
        }

        String tableNumber = null;
        if (searchCafeTableRequest.getTableNumber() != null && !searchCafeTableRequest.getTableNumber().isBlank()) {
            tableNumber = searchCafeTableRequest.getTableNumber();
        }

        Page<CafeTable> cafeTables = cafeTableRepository.search(tableNumber, tableStatus, pageable);

        // Lấy hết order OPEN của các bàn trong trang hiện tại, chỉ 1 query
        List<Integer> tableIds = cafeTables.getContent().stream()
                .map(CafeTable::getTableId)
                .toList();

        Map<Integer, Integer> tableIdToOrderId = orderRepository
                .findByTable_TableIdInAndStatus(tableIds, OrderStatus.OPEN)
                .stream()
                .collect(Collectors.toMap(o -> o.getTable().getTableId(), Order::getOrderId));

        return cafeTables.map(cafeTable -> SearchCafeTableResponse.builder()
                .tableId(cafeTable.getTableId())
                .tableNumber(cafeTable.getTableNumber())
                .urlQr(cafeTable.getUrlQr())
                .status(cafeTable.getStatus().toString())
                .currentOrderId(tableIdToOrderId.get(cafeTable.getTableId()))
                .build()
        );
    }



    @Transactional
    public void closeTable(Integer orderId){
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại"));
        if(order.getStatus() != OrderStatus.PAID){
            throw new IllegalArgumentException("Đơn hàng chưa thanh toán, không thể đóng bàn");
        }
        CafeTable cafeTable = order.getTable();
        cafeTable.setStatus(TableStatus.AVAILABLE);
        cafeTableRepository.save(cafeTable);

    }
}
