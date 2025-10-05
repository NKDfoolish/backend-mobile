//package com.myproject.model;
//
//import lombok.*;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.mapping.Document;
//import org.springframework.data.mongodb.core.mapping.Field;
//
//import java.util.List;
//import java.util.Map;
//
//@Getter
//@Setter
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//@Document(collection = "plant_details")
//public class PlantDetail {
//    @Id
//    private String id;
//
//    @Field(name = "plant_id")
//    private Long plantId;
//
//    private String introduction;
//
//    @Field(name = "image_source")
//    private String imageSource;
//
//    @Field(name = "table_data")
//    private List<Map<String, String>> tableData;
//
//    @Field(name = "care_data")
//    private List<Map<String, String>> careData;
//
//    private String title;
//
//    private String url;
//}
