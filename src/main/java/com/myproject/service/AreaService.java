package com.myproject.service;

import com.myproject.dto.request.AreaCreationRequest;
import com.myproject.dto.request.AreaUpdateRequest;
import com.myproject.dto.response.AreaPageResponse;
import com.myproject.dto.response.AreaResponse;

public interface AreaService {
    AreaPageResponse findAll(String keyword, String sort, int page, int size);

    AreaResponse findById(Integer id);

    Integer save(AreaCreationRequest req);

    void update(AreaUpdateRequest req);

    void delete(Integer areaId);
}
