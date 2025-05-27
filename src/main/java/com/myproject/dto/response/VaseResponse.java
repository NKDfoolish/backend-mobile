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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VaseResponse implements Serializable {
    private Integer id;
    private String vaseName;
    private Integer deviceId;
    private PlantResponse plant;
    private AreaResponse area;
    private Date createdAt;
    private Date updatedAt;
}
