package Sprint1.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "job_applications")
public class JobApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "applicant_id", referencedColumnName = "id")
    private User applicant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_listing_id", referencedColumnName = "id")
    private JobListing jobListing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", referencedColumnName = "id")
    private Resume selectedResume;

    private String status = "Applied";
    private LocalDateTime submittedAt;

    private String coverLetterFilename;
    private String coverLetterContentType;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] coverLetterData;

    public JobApplication() { }

    @PrePersist
    protected void setSubmissionTime() {
        if (submittedAt == null) submittedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public User getApplicant() { return applicant; }
    public void setApplicant(User applicant) { this.applicant = applicant; }
    public JobListing getJobListing() { return jobListing; }
    public void setJobListing(JobListing jobListing) { this.jobListing = jobListing; }
    public Resume getSelectedResume() { return selectedResume; }
    public void setSelectedResume(Resume selectedResume) { this.selectedResume = selectedResume; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    public String getCoverLetterFilename() { return coverLetterFilename; }
    public void setCoverLetterFilename(String coverLetterFilename) { this.coverLetterFilename = coverLetterFilename; }
    public String getCoverLetterContentType() { return coverLetterContentType; }
    public void setCoverLetterContentType(String coverLetterContentType) { this.coverLetterContentType = coverLetterContentType; }
    public byte[] getCoverLetterData() { return coverLetterData; }
    public void setCoverLetterData(byte[] coverLetterData) { this.coverLetterData = coverLetterData; }
    public boolean hasCoverLetter() { return coverLetterData != null && coverLetterData.length > 0; }
}
