package Sprint1.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

import Sprint1.demo.model.JobListing;
import Sprint1.demo.service.JobRepository;

@Controller
public class JobController {

private final JobRepository jobRepository;

public JobController (JobRepository jobRepository){
this.jobRepository=jobRepository;

} 

@GetMapping("/jobs") 
public String showJobs(
    
    @RequestParam(name = "keyword", defaultValue = "") 
    String keyword,

    @RequestParam(name = "location", defaultValue = "") 
    String location,
                           
    @RequestParam(name = "jobType", defaultValue = "") 
    String jobType,
    
    Model model) {

        //Remove extra spaces to match job postings
        String cleanKeyword = keyword.trim();
        String cleanLocation = location.trim();
        String cleanJobType = jobType.trim();

        //Calls search query returning list of matching jobs
        List<JobListing> jobs = jobRepository.search(cleanKeyword, cleanLocation, cleanJobType);

        //Attributes from the database
        model.addAttribute("jobs", jobs);
        model.addAttribute("locations", jobRepository.findDistinctLocations());
        model.addAttribute("jobTypes", jobRepository.findDistinctJobTypes());

        //Attributes from the user 
        model.addAttribute("keyword", cleanKeyword);
        model.addAttribute("selectedLocation", cleanLocation);
        model.addAttribute("selectedJobType", cleanJobType);

        //Attributte from user selection 
        model.addAttribute("filtersActive",!cleanKeyword.isEmpty() || !cleanLocation.isEmpty() || !cleanJobType.isEmpty());

        return "jobs";
    

                           }

    
}
