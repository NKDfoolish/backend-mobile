package com.myproject.repository;

import com.myproject.model.Plant;
import com.myproject.model.Vase;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VaseRepository extends JpaRepository<Vase, Integer> {

    @Query(value = "select v from Vase v " +
            "where lower(v.vaseName) like :keyword ")
    Page<Vase> searchByKeyword(String keyword, Pageable pageable);

    Vase findByVaseName(String vaseName);
}
