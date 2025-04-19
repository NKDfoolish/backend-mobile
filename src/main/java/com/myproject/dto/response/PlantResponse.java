package com.myproject.dto.response;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlantResponse implements Serializable {
    private Long id;
    private String plantName;
    private String image;
    private String description;
}
