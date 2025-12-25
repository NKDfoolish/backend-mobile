package com.myproject.dto.response;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SharedGardenDetailResponse implements Serializable {
    private Integer gardenId;
    private String gardenName;
    private String ownerName;
    private String permission;
    private LocalDateTime sharedAt;
    private List<AreaDetailResponse> areas;
}
