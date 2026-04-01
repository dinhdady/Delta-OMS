package com.project.management_system.repository;

import com.project.management_system.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);
    Optional<Category> findByName(String name);
    @Query("SELECT DISTINCT c.name FROM Category c")
    List<String> findAllDistinctNames();

}

