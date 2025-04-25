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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AreaResponse implements Serializable {
    private Integer id;
    private String areaName;
    private String image;
    private Garden garden;
    private Date createdAt;
    private Date updatedAt;
}
