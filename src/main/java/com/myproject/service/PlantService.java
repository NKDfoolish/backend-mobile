package com.myproject.service;

import com.myproject.dto.request.PlantCreationRequest;
import com.myproject.dto.request.PlantUpdateRequest;
import com.myproject.dto.response.PlantPageResponse;
import com.myproject.dto.response.PlantResponse;

public interface PlantService {

    PlantPageResponse findAll(String keyword, String sort, int page, int size);

    PlantResponse findById(Long id);

    long save(PlantCreationRequest req);

    void update(PlantUpdateRequest req);

    void delete(Long id);
}
