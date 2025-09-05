package com.example.backend.repository;

import com.example.backend.models.JobPosting;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface JobPostingRepository extends MongoRepository<JobPosting, String> {
    List<JobPosting> findByUserId(String userId);
}
