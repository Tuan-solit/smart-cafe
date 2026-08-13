package com.module3.ccafe.repository;

import com.module3.ccafe.entity.CafeTable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CafeTableRepository extends JpaRepository<CafeTable,Integer> {

    @Query("""
        SELECT t FROM CafeTable t
        WHERE (:tableNumber is null or t.tableNumber = :tableNumber)
            AND (:status is null or t.status = :status)
""")
    Page<CafeTable> search(String tableNumber, String status, Pageable pageable);
}
