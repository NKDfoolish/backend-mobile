package com.myproject.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
public class PlantDetailRequest {
    private String description;
    private String imageSource;
    private List<Map<String, String>> tableData;
    private List<Map<String, String>> careData;
    private String title;
    private String url;
}
