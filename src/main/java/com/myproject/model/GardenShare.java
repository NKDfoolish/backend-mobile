package com.myproject.model;

import com.myproject.common.GardenPermission;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "tbl_garden_share",
        uniqueConstraints = @UniqueConstraint(columnNames = {"garden_id", "user_id"})
)
@Getter
@Setter
public class GardenShare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "garden_id", nullable = false)
    private Integer gardenId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GardenPermission permission;

    @Column(name = "shared_by", nullable = false)
    private Long sharedBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

