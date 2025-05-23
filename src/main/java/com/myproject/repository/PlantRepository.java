package com.myproject.repository;

import com.myproject.model.Plant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlantRepository extends JpaRepository<Plant, Long> {
    
    @Query(value = "select p from Plant p " +
            "where (lower(p.plantName) like :keyword )")
    Page<Plant> searchByKeyword(String keyword, Pageable pageable);

    Plant findByPlantName(String plantName);
}
