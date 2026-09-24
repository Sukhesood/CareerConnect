package Sprint1.demo.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Sprint1.demo.model.Job;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    // Spring Data JPA gives you findAll(), save(), findById(), etc. out of the box!
}