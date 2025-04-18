package com.myproject.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "tbl_garden")
public class Garden extends AbstractEntity<Integer> implements Serializable {

    @Column(name = "garden_name", length = 255)
    private String gardenName;

    @Column(name = "area_size")
    private int areaSize;

    @OneToMany(mappedBy = "garden")
    private Set<Area> areas = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
