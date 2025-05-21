package com.myproject.service;

import com.myproject.dto.request.VaseCreationRequest;
import com.myproject.dto.request.VaseUpdateRequest;
import com.myproject.dto.response.VasePageResponse;
import com.myproject.dto.response.VaseResponse;

public interface VaseService {

    VasePageResponse findAll(String keyword, String sort, int page, int size);

    VaseResponse findById(Integer id);

    Integer save(VaseCreationRequest req);

    void update(VaseUpdateRequest req);

    void delete(Integer vaseId);
}
