package com.myproject.repository;

import com.myproject.model.Area;
import com.myproject.model.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface AreaRepository extends JpaRepository<Area, Integer> {
    @Query(value = "select a from Area a join a.garden g " +
            "where lower(a.areaName) like :keyword and g.user = :user")
    Page<Area> searchByKeyword(String keyword, Pageable pageable, UserEntity user);

    Area findByAreaName(String areaName);

    @Query(value = "select a from Area a join a.garden g " +
            "where g.user = :user")
    Page<Area> searchByGarden(Pageable pageable, UserEntity user);

    @Query(value = "select a from Area a join a.garden g " +
            "where g.user = :userCheck and a.id = :id")
    Optional<Area> findByUser(Integer id, UserEntity userCheck);

    @Query(value = "select a from Area a join a.garden g " +
            "where g.user = :user")
    Set<Area> searchByUser(UserEntity user);
}
