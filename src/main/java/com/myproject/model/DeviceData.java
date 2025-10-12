package com.myproject.model;

import lombok.Data;

@Data
public class DeviceData {
    private Integer sensor1;
    private Integer sensor2;
    private String relay1;
    private String relay2;
    private String clientId;
    private Long timestamp;

    // Default constructor and getters/setters
}
