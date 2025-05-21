package com.myproject.repository;

import com.myproject.model.Garden;
import com.myproject.model.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface GardenRepository extends JpaRepository<Garden, Integer> {

    @Query(value = "select g from Garden g " +
            "where lower(g.gardenName) like :keyword and g.user = :user")
    Page<Garden> searchByKeyword(String keyword, Pageable pageable, UserEntity user);

    Garden findByGardenName(String gardenName);

    @Query(value = "select g from Garden g " +
            "where g.user = :user")
    Page<Garden> searchByUser(Pageable pageable, UserEntity user);

    @Query(value = "select g from Garden g " +
            "where g.user = :user")
    Set<Garden> findByUser(UserEntity user);
}
