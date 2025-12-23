package com.myproject.service;

import com.myproject.dto.request.ShareGardenRequest;
import com.myproject.dto.response.SharedGardenResponse;

import java.util.List;

public interface GardenShareService {

    void shareGarden(Long ownerId, ShareGardenRequest req);

    List<SharedGardenResponse> getSharedGardens(Long userId);

    void revokeShare(Long ownerId, Integer gardenId, Long userId);

    boolean hasViewPermission(Long userId, Integer gardenId);

    boolean hasControlPermission(Long userId, Integer gardenId);
}
