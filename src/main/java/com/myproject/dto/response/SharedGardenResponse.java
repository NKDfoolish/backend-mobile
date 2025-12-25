package com.myproject.dto.response;

import java.time.LocalDateTime;

public class SharedGardenResponse {
    private Integer gardenId;
    private String gardenName;
    private String ownerName;
    private String permission;
    private LocalDateTime sharedAt;

    public SharedGardenResponse(
            Integer gardenId,
            String gardenName,
            String ownerName,
            String permission,
            LocalDateTime sharedAt
    ) {
        this.gardenId = gardenId;
        this.gardenName = gardenName;
        this.ownerName = ownerName;
        this.permission = permission;
        this.sharedAt = sharedAt;
    }

    // getters (bắt buộc)
    public Integer getGardenId() {
        return gardenId;
    }

    public String getGardenName() {
        return gardenName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getPermission() {
        return permission;
    }

    public LocalDateTime getSharedAt() {
        return sharedAt;
    }
}
