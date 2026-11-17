package Sprint1.demo.service;

import Sprint1.demo.model.JobListing;
import Sprint1.demo.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// US 10.1 (#140): unit tests for the AI cover letter prompt
class CoverLetterPromptBuilderTest {

    private final CoverLetterPromptBuilder builder = new CoverLetterPromptBuilder();

    @Test
    void promptIncludesTheJobAndTheApplicant() {
        String prompt = builder.buildPrompt(applicant(), job(), "Built a Spring Boot app at school.");

        assertTrue(prompt.contains("Junior Java Developer"));
        assertTrue(prompt.contains("Acme Inc."));
        assertTrue(prompt.contains("Java, Spring Boot"));
        assertTrue(prompt.contains("Rabih El-Marouk"));
        assertTrue(prompt.contains("Built a Spring Boot app at school."));
    }

    @Test
    void promptTellsTheAiNotToInventExperience() {
        String prompt = builder.buildPrompt(applicant(), job(), "Some resume text");

        assertTrue(prompt.contains("Do not invent any."));
    }

    @Test
    void missingResumeIsHandledWithoutFailing() {
        String prompt = builder.buildPrompt(applicant(), job(), null);

        assertTrue(prompt.contains("Resume: Not provided."));
    }

    @Test
    void emptyJobFieldsAreShownAsNotSpecified() {
        JobListing job = job();
        job.setRequirement(null);
        job.setSkills("   ");

        String prompt = builder.buildPrompt(applicant(), job, "Some resume text");

        assertTrue(prompt.contains("Requirements: Not specified"));
        assertTrue(prompt.contains("Skills: Not specified"));
    }

    @Test
    void veryLongResumeIsShortened() {
        String longResume = "a".repeat(CoverLetterPromptBuilder.MAX_RESUME_CHARACTERS + 500);

        String prompt = builder.buildPrompt(applicant(), job(), longResume);

        assertFalse(prompt.contains(longResume));
        assertTrue(prompt.contains("a".repeat(CoverLetterPromptBuilder.MAX_RESUME_CHARACTERS) + "..."));
    }

    @Test
    void applicantWithoutNameUsesAGenericName() {
        User applicant = new User();

        String prompt = builder.buildPrompt(applicant, job(), null);

        assertTrue(prompt.contains("Name: The applicant"));
    }

    @Test
    void missingJobOrApplicantIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> builder.buildPrompt(applicant(), null, null));
        assertThrows(IllegalArgumentException.class, () -> builder.buildPrompt(null, job(), null));
    }

    private User applicant() {
        User user = new User();
        user.setFirstName("Rabih");
        user.setLastName("El-Marouk");
        return user;
    }

    private JobListing job() {
        JobListing job = new JobListing();
        job.setTitle("Junior Java Developer");
        job.setCompanyName("Acme Inc.");
        job.setLocation("Montreal, QC");
        job.setJobType("FULL_TIME");
        job.setWorkMode("HYBRID");
        job.setDescription("Build and maintain web applications.");
        job.setRequirement("Knowledge of Java");
        job.setSkills("Java, Spring Boot");
        return job;
    }
}