package com.example.backend.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Document(collection = "jobpostings")
public class JobPosting {

    @Id
    @JsonProperty("_id")
    private String id;
    private String userId;
    private String category;
    private String name;
    private String location;
    private String employmentType;
    private Double salary;
    private String description;
    private String responsibilities;
    private String requirements;
    private String benefits;

    @CreatedDate
    private LocalDateTime createdAt;

    // Add __v manually
    private Integer __v = 0;
}
