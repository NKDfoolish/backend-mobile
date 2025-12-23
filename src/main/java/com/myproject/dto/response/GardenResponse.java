package com.myproject.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.myproject.model.Garden;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GardenResponse implements Serializable {
    private Integer id;
    private String gardenName;
    private Long userId;
    private Date createdAt;
    private Date updatedAt;
}
