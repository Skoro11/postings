package com.example.backend.controllers;

import com.example.backend.models.JobPosting;
import com.example.backend.models.User;
import com.example.backend.models.Category;
import com.example.backend.repository.JobPostingRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.repository.CategoryRepository;
import com.mongodb.MongoException;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "${frontend.url}", allowCredentials = "true")
public class ApiController {
    private final MongoTemplate mongoTemplate;

    // Endpoint to check MongoDB connection
    @GetMapping("/mongo-health")
    public String checkMongoConnection() {
        try {
            MongoDatabase db = mongoTemplate.getDb();
            MongoIterable<String> collections = db.listCollectionNames();
            collections.first(); // Try reading first collection to confirm connectivity
            return "✅ MongoDB is connected! Database: " + db.getName();
        } catch (MongoException e) {
            return "❌ MongoDB connection failed: " + e.getMessage();
        }
    }


        private final JobPostingRepository jobRepo;
        private final UserRepository userRepo;
        private final CategoryRepository categoryRepo;





        // ===================== Job Posting APIs =====================
        @PostMapping("/postings/id")
        public ResponseEntity<List<JobPosting>> getPostingsByUserId(@RequestBody UserIdRequest request) {
            List<JobPosting> postings = jobRepo.findByUserId(request.getUserId());
            return ResponseEntity.ok(postings);
        }

        @GetMapping("/postings/all")
        public ResponseEntity<List<JobPosting>> getAllPostings() {
            return ResponseEntity.ok(jobRepo.findAll());
        }

    @PostMapping("/postings")
    public ResponseEntity<JobPosting> addPosting(@RequestBody JobPosting jobPosting) {
        // Save the job posting
        JobPosting saved = jobRepo.save(jobPosting);

        // Return the full object, including generated ID and timestamps
        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/postings")
    public ResponseEntity<JobPosting> updatePosting(@RequestBody JobPosting jobPosting) {
        Optional<JobPosting> existing = jobRepo.findById(jobPosting.getId());
        if (existing.isEmpty()) return ResponseEntity.status(404).build();

        JobPosting toUpdate = existing.get();

        // Only update fields that are non-null
        if (jobPosting.getUserId() != null) toUpdate.setUserId(jobPosting.getUserId());
        if (jobPosting.getCategory() != null) toUpdate.setCategory(jobPosting.getCategory());
        if (jobPosting.getName() != null) toUpdate.setName(jobPosting.getName());
        if (jobPosting.getLocation() != null) toUpdate.setLocation(jobPosting.getLocation());
        if (jobPosting.getEmploymentType() != null) toUpdate.setEmploymentType(jobPosting.getEmploymentType());
        if (jobPosting.getSalary() != null) toUpdate.setSalary(jobPosting.getSalary());
        if (jobPosting.getDescription() != null) toUpdate.setDescription(jobPosting.getDescription());
        if (jobPosting.getResponsibilities() != null) toUpdate.setResponsibilities(jobPosting.getResponsibilities());
        if (jobPosting.getRequirements() != null) toUpdate.setRequirements(jobPosting.getRequirements());
        if (jobPosting.getBenefits() != null) toUpdate.setBenefits(jobPosting.getBenefits());

        JobPosting updated = jobRepo.save(toUpdate);

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/postings/all")
        public ResponseEntity<String> deleteAllPostings() {
            long count = jobRepo.count();
            jobRepo.deleteAll();
            return ResponseEntity.ok("Deleted " + count + " postings");
        }



    // Controller method
    @DeleteMapping("/postings")
    public ResponseEntity<String> deletePosting(@RequestBody IdRequest request) {
        if (request.get_id() == null || request.get_id().isEmpty()) {
            return ResponseEntity.badRequest().body("ID cannot be null or empty");
        }

        Optional<JobPosting> existing = jobRepo.findById(request.get_id());
        if (existing.isEmpty()) {
            return ResponseEntity.status(404).body("Job not found");
        }

        jobRepo.deleteById(request.get_id());
        return ResponseEntity.ok("Job posting deleted");
    }


    // ===================== User APIs =====================
        @GetMapping("/users")
        public ResponseEntity<List<User>> getUsers() {
            return ResponseEntity.ok(userRepo.findAll());
        }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserEmailRequest request) {
        Optional<User> user = Optional.ofNullable(userRepo.findByEmail(request.getEmail()));

        if (user.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        // Return the response in the format Postman expects
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Logged in successfully");
        response.put("user", user.get());

        return ResponseEntity.ok(response);
    }

        @PostMapping("/user")
        public ResponseEntity<User> addUser(@RequestBody UserEmailRequest request) {
            User user = new User();
            user.setEmail(request.getEmail());
            User saved = userRepo.save(user);
            return ResponseEntity.status(201).body(saved);
        }

        @DeleteMapping("/users")
        public ResponseEntity<String> deleteAllUsers() {
            long count = userRepo.count();
            userRepo.deleteAll();
            return ResponseEntity.ok("Deleted " + count + " users");
        }

        @DeleteMapping("/user")
        public ResponseEntity<User> deleteUser(@RequestBody IdRequest request) {
            Optional<User> existing = userRepo.findById(request.getId());
            if (existing.isEmpty()) return ResponseEntity.status(404).build();

            userRepo.deleteById(request.getId());
            return ResponseEntity.ok(existing.get());
        }

        // ===================== Category APIs =====================
        @PostMapping("/categories")
        public ResponseEntity<Category> addCategory(@RequestBody Category category) {
            Category saved = categoryRepo.save(category);
            return ResponseEntity.status(201).body(saved);
        }

        @GetMapping("/categories")
        public ResponseEntity<List<Category>> getCategories() {
            return ResponseEntity.ok(categoryRepo.findAll());
        }

        // ===================== Request Body Classes =====================
        public static class UserIdRequest {
            private String userId;
            public String getUserId() { return userId; }
            public void setUserId(String userId) { this.userId = userId; }
        }

    // Request class for deleting by _id
    public static class IdRequest {
        private String _id;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getId() {
            return _id;
        }
    }

        public static class UserEmailRequest {
            private String email;
            public String getEmail() { return email; }
            public void setEmail(String email) { this.email = email; }
        }
}

