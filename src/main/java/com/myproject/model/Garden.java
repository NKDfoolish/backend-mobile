package com.myproject.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @OneToMany(mappedBy = "garden")
    @JsonIgnore
    private Set<Area> areas = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private UserEntity user;
}
