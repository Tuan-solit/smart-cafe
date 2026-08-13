package com.module3.ccafe.service;

import com.module3.ccafe.dto.request.SearchCafeTableRequest;
import com.module3.ccafe.dto.response.SearchCafeTableResponse;
import com.module3.ccafe.entity.CafeTable;
import com.module3.ccafe.repository.CafeTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CafeTableService {
    @Autowired
    CafeTableRepository cafeTableRepository;



    public Page<SearchCafeTableResponse> search(SearchCafeTableRequest searchCafeTableRequest, Pageable pageable){
        Page<CafeTable> cafeTables = cafeTableRepository.search(searchCafeTableRequest.getTableNumber(),searchCafeTableRequest.getStatus(),pageable);
        return cafeTables.map(cafeTable -> SearchCafeTableResponse.builder()
                        .tableId(cafeTable.getTableId())
                        .tableNumber(cafeTable.getTableNumber())
                        .urlQr(cafeTable.getUrlQr())
                        .status(cafeTable.getStatus().toString())
                        .build()
                );
    }
}
