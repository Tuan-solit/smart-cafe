package com.module3.ccafe.repository;

import com.module3.ccafe.entity.CafeTable;
import com.module3.ccafe.entity.enums.TableStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CafeTableRepository extends JpaRepository<CafeTable,Integer> {

    @Query("""
        SELECT t FROM CafeTable t
        WHERE (:tableNumber is null or t.tableNumber = :tableNumber)
            AND (:status is null or t.status = :status)
""")
    Page<CafeTable> search(@Param("tableNumber") String tableNumber,
                           @Param("status") TableStatus status, Pageable pageable);

    @Query("""
    SELECT t
    FROM CafeTable t
    WHERE
        (:keyword = ''
         OR LOWER(t.tableNumber) LIKE LOWER(CONCAT('%', :keyword, '%')))
    AND
        (:status IS NULL
         OR t.status = :status)
""")
    Page<CafeTable> searchTables(
            @Param("keyword") String keyword,
            @Param("status") TableStatus status,
            Pageable pageable
    );
}
