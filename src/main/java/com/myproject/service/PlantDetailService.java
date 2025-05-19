package com.myproject.service;

import com.myproject.dto.request.PlantDetailRequest;
import com.myproject.dto.request.PlantDetailUpdateRequest;
import com.myproject.dto.response.PlantDetailResponse;

public interface PlantDetailService {

    String save(PlantDetailRequest req);

    PlantDetailResponse findByPlantId(Long plantId);

    void update(PlantDetailUpdateRequest req);

    void delete(Long plantId);
}
