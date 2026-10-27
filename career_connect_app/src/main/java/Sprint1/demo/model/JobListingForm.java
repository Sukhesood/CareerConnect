package Sprint1.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public class JobListingForm {

    @NotBlank(message = "Please provide a title to the job posting")
    private String title;
    @NotBlank(message = "Company name must be included") private String companyName;
    @NotBlank(message = "Please provide the general location of the job") private String location;
    private String workMode;
    @NotBlank(message = "Please include the employment type") private String jobType; // e.g., Full-time, Internship, Part time
    private List<String> tags;

    @Column(length = 5000)
    @NotBlank(message = "Please provide a description of the job") private String description;

    @Column(length = 2000)
    private String requirement;
    private String skills;
    private Integer salaryMin;
    private Integer salaryMax;
    private String applyUrl;
    private boolean coverLetterRequired;
    @FutureOrPresent
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate deadline;
    private String salaryPeriod;

    // Constructors
    public JobListingForm() {
    }

    public JobListingForm(String title, String companyName, String location, String workMode,
                      String jobType, List<String> tags, String description, String skills,
                      Integer salaryMin, Integer salaryMax, String applyUrl,
                      boolean coverLetterRequired, LocalDate deadline,
                      String salaryPeriod) {
        this.title = title;
        this.companyName = companyName;
        this.location = location;
        this.workMode = workMode;
        this.jobType = jobType;
        this.tags = tags;
        this.description = description;
        this.skills = skills;
        this.salaryMin = salaryMin;
        this.salaryMax = salaryMax;
        this.applyUrl = applyUrl;
        this.coverLetterRequired = coverLetterRequired;
        this.deadline = deadline;
        this.salaryPeriod = salaryPeriod;
    }

    public String getRequirement(){
        return requirement;
    }

    public void setRequirement(String requirement){
        this.requirement=requirement;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWorkMode() {
        return workMode;
    }

    public void setWorkMode(String workMode) {
        this.workMode = workMode;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public Integer getSalaryMin() {
        return salaryMin;
    }

    public void setSalaryMin(Integer salaryMin) {
        this.salaryMin = salaryMin;
    }

    public Integer getSalaryMax() {
        return salaryMax;
    }

    public void setSalaryMax(Integer salaryMax) {
        this.salaryMax = salaryMax;
    }

    public String getApplyUrl() {
        return applyUrl;
    }

    public void setApplyUrl(String applyUrl) {
        this.applyUrl = applyUrl;
    }

    public boolean isCoverLetterRequired() {
        return coverLetterRequired;
    }

    public void setCoverLetterRequired(boolean coverLetterRequired) {
        this.coverLetterRequired = coverLetterRequired;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }
    public String getSalaryPeriod() {
        return salaryPeriod;
    }

    public void setSalaryPeriod(String salaryPeriod) {
        this.salaryPeriod = salaryPeriod;
    }

}

