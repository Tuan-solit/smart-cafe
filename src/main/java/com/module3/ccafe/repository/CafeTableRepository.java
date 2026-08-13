package com.module3.ccafe.repository;

import com.module3.ccafe.entity.CafeTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CafeTableRepository extends JpaRepository<CafeTable,Integer> {
}
