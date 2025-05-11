package com.myproject.repository;

import com.myproject.model.PlantDetail;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlantDetailRepository extends MongoRepository<PlantDetail, String> {
    Optional<PlantDetail> findByPlantId(Long plantId);
    void deleteByPlantId(Long plantId);
}
