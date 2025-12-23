package com.myproject.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlantDetailResponse {
    private Long plantId;
    private String description;
    private String imageSource;
    private List<Map<String, String>> tableData;
    private List<Map<String, String>> careData;
    private String title;
    private String url;
}
