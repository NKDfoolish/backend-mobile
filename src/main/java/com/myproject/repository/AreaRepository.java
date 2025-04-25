package com.myproject.repository;

import com.myproject.model.Area;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AreaRepository extends JpaRepository<Area, Integer> {
    @Query(value = "select a from Area a " +
            "where lower(a.areaName) like :keyword ")
    Page<Area> searchByKeyword(String keyword, Pageable pageable);

    Area findByAreaName(String areaName);
}
