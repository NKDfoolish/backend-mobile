package com.myproject.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

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
    private Date createdAt;
    private Date updatedAt;
}
