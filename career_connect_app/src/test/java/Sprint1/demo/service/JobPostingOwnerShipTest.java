package Sprint1.demo.service;

import Sprint1.demo.model.JobListing;
import Sprint1.demo.model.JobListingForm;

import Sprint1.demo.model.User;
import Sprint1.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@Import(JobPostingService.class)
@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
public class JobPostingOwnerShipTest {

    @Autowired UserRepository userRepository;
    @Autowired JobPostingService jobPostingService;

    @Test
    void recruiterCannotGetAnotherRecruitersJobPosting() {

        User user = createRecruiter("user@gmail.com");
        User other = createRecruiter("other@gmail.com");
        userRepository.save(user);
        userRepository.save(other);

        JobListingForm form = new JobListingForm();

        form.setTitle("QA Automation Intern");
        form.setCompanyName("CareerConnect Labs");
        form.setLocation("Montreal");
        form.setWorkMode("Hybrid");
        form.setJobType("Internship");
        form.setDescription("Create automated tests.");
        form.setRequirement("Basic Java knowledge");
        form.setSkills("Java, JUnit");
        form.setSalaryMin(20);
        form.setSalaryMax(25);
        form.setSalaryPeriod("hourly");
        form.setApplyUrl("https://example.com/apply");
        form.setCoverLetterRequired(true);
        form.setDeadline(LocalDate.of(2026, 12, 31));

        JobListing job = jobPostingService.createJobListing(form, "user@gmail.com");

        assertThat(jobPostingService.getOwnedJobListing(job.getId(), "user@gmail.com")).isPresent();
        assertThat(jobPostingService.getOwnedJobListing(job.getId(), "other@gmail.com")).isEmpty();
    }


    private User createRecruiter(String email){
        User user = new User();
        user.setEmail(email);
        user.setRole("RECRUITER");
        user.setAddress("12345");
        user.setFirstName("aaa");
        user.setLastName("bbbbbbb");
        user.setPassword("123456");
        user.setPhoneNumber("515-151-1155");
        return user;
    }
}
