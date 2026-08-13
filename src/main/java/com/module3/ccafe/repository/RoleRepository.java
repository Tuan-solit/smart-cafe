package com.module3.ccafe.repository;

import com.module3.ccafe.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository  extends JpaRepository<Role,Integer> {
}
