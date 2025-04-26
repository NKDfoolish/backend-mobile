package com.myproject.service;

import com.myproject.dto.request.GardenCreationRequest;
import com.myproject.dto.request.GardenUpdateRequest;
import com.myproject.dto.response.GardenPageResponse;
import com.myproject.dto.response.GardenResponse;

public interface GardenService {
    GardenPageResponse findAll(String keyword, String sort, int page, int size);

    GardenResponse findById(Integer id);

    Integer save(GardenCreationRequest req);

    void update(GardenUpdateRequest req);

    void delete(Integer gardenId);
}
