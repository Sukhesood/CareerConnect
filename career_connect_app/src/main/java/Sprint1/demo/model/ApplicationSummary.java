package Sprint1.demo.model;


import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDate;

@Entity
@Table(name = "ApplicationSummary")
public class ApplicationSummary {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private long id_application;
    @Column(nullable = false)

    private String email;

    private String company;
    private String title;

    private LocalDate creationDate;
    private LocalDate appliedDate;
    private LocalDate closingDate;

    private String status;
    private String state;

    public String getState(){
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getEmail() {
        return email;
    }

    public void setId_application(long id_application) {
        this.id_application = id_application;
    }

    public long getId_application() {
        return id_application;
    }


    public void setEmail(String email) {
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public LocalDate getAppliedDate() {
        return appliedDate;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public String getCompany() {
        return company;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getClosingDate() {
        return closingDate;
    }

    public String getStatus() {
        return status;
    }

    public void setAppliedDate(LocalDate appliedDate) {
        this.appliedDate = appliedDate;
    }

    public void setClosingDate(LocalDate closingDate) {
        this.closingDate = closingDate;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}

