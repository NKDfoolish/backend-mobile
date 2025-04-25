package com.myproject.service;

import com.myproject.dto.request.AreaCreationRequest;
import com.myproject.dto.request.AreaUpdateRequest;
import com.myproject.dto.response.AreaPageResponse;
import com.myproject.dto.response.AreaResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

public interface AreaService {
    AreaPageResponse findAll(String keyword, String sort, int page, int size);

    AreaResponse findById(Integer id);

    Integer save(@Valid AreaCreationRequest req);

    void update(@Valid AreaUpdateRequest req);

    void delete(Integer areaId);
}
