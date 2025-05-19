package com.myproject.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class PlantDetailUpdateRequest {

    @NotNull(message = "Plant ID is required")
    private Long plantId;

    private String introduction;
    private String imageSource;
    private List<Map<String, String>> tableData;
    private List<Map<String, String>> careData;
    private String title;
}
