package com.santechture.api.repository;

import com.santechture.api.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {
    Admin findByUsernameIgnoreCase(String username);

}
