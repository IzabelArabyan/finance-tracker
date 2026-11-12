package com.example.finance.tracker.category.repository;

import com.example.finance.tracker.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("""
            select c from Category c
            left join c.owner o
            where o.id is null or o.id = :ownerId
            order by c.name
            """)
    List<Category> findAllVisible(@Param("ownerId") Long ownerId);

    // One category I can see
    @Query("""
            select c from Category c
            left join c.owner o
            where c.id = :id and (o.id is null or o.id = :ownerId)
            """)
    Optional<Category> findVisibleById(@Param("id") Long id, @Param("ownerId") Long ownerId);

    // One category I own no global ones
    Optional<Category> findByIdAndOwnerId(Long id, Long ownerId);

    // For the "name already exists" check
    boolean existsByNameAndOwnerIsNull(String name);

    boolean existsByNameAndOwnerId(String name, Long ownerId);

    boolean existsByName(String name);
}