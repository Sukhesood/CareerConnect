package Sprint1.demo.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String companyName;
    private String location;
    private String jobType; // e.g., Full-time, Internship, Remote
    
    @Column(length = 2000)
    private String description;
    
    private LocalDateTime postedDate;

    @PrePersist
    protected void onCreate() {
        this.postedDate = LocalDateTime.now();
    }

    // Constructors
    public Job() {}

    public Job(String title, String companyName, String location, String jobType, String description) {
        this.title = title;
        this.companyName = companyName;
        this.location = location;
        this.jobType = jobType;
        this.description = description;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getLocation() { return location; }
    public void getLocation(String location) { this.location = location; }

    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getPostedDate() { return postedDate; }
    public void setPostedDate(LocalDateTime postedDate) { this.postedDate = postedDate; }
}