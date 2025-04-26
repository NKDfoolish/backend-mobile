package com.myproject.repository;

import com.myproject.model.Garden;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GardenRepository extends JpaRepository<Garden, Integer> {

    @Query(value = "select g from Garden g " +
            "where lower(g.gardenName) like :keyword ")
    Page<Garden> searchByKeyword(String keyword, Pageable pageable);

    Garden findByGardenName(String gardenName);
}
