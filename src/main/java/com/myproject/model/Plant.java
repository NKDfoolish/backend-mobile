package com.myproject.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "tbl_plant")
public class Plant extends AbstractEntity<Long> implements Serializable {

    @Column(name = "plant_name", length = 255, unique = true, nullable = false)
    private String plantName;

    @Column(name = "image", length = 255)
    private String image;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "title", length = 255)
    private String title;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "table_data", columnDefinition = "jsonb")
    private List<Map<String, String>> tableData;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "care_data", columnDefinition = "jsonb")
    private List<Map<String, String>> careData;

    @OneToMany(mappedBy = "plant")
    @JsonIgnore
    private Set<Vase> vases = new HashSet<>();
}
