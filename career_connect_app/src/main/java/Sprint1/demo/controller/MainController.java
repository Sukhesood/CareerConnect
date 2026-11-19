
package Sprint1.demo.controller;

import Sprint1.demo.model.*;
import Sprint1.demo.repository.*;
import Sprint1.demo.service.*;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.jspecify.annotations.NonNull;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.time.LocalDate;
import java.net.MalformedURLException;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class MainController {
	 private final UserService userService ;
     private final NotificationService notificationService;
     private final ResumeService resumeService;
     private final JobPostingService jobPostingService;
	 private final JobApplicationRepository jobApplicationRepository;
     private final JobRepository jobRepository;
     private final ApplicationSummaryRepository applicationSummaryRepository;
	   public MainController(UserService userService,
                             JobPostingService jobPostingService,
                             JobApplicationRepository jobApplicationRepository,
                             JobRepository jobRepository,
                             ResumeService resumeService,
                             ApplicationSummaryRepository applicationSummaryRepository,
                             NotificationService notificationService
                             ) {

	       this.userService = userService;
           this.jobPostingService=jobPostingService;
	       this.jobApplicationRepository = jobApplicationRepository;
           this.jobRepository=jobRepository;
           this.resumeService=resumeService;
           this.applicationSummaryRepository=applicationSummaryRepository;
           this.notificationService = notificationService;

       }

    private boolean isAuth(HttpSession session) {
        return (null != session.getAttribute("loggedInUserEmail"));
    }

    private boolean isRecruiter(HttpSession session) {
        String email =(String) session.getAttribute("loggedInUserEmail");
        Optional<User> result = userService.findByEmail(email);
        if (result.isPresent()){
            User temp = result.get();
            return temp.getRole().equals("RECRUITER");
        }
        return false;
    }



    @GetMapping("/")
    public String showHomePage(HttpSession session, Model model) {
        model.addAttribute("isAuth", isAuth(session));
        return "index";
    }


    @GetMapping({"/job_posting_creation"})
    public String showJobPostingCreation(HttpSession session, Model model){
        if (!isAuth(session)) return "redirect:/login";
        if (!isRecruiter(session)) return "redirect:/dashboard";
        model.addAttribute("jobListing", new JobListing());
        return "job_posting_creation";
    }

    @PostMapping({"/job_posting_creation"})
    public String submitJobPosting(@Valid @ModelAttribute("jobListing") JobListingForm form, BindingResult result, HttpSession session,
                                   RedirectAttributes redirect) {
        if (!isAuth(session)) return "redirect:/login";
        if (!isRecruiter(session)) return "redirect:/dashboard";


        if (result.hasErrors()) {
            return "job_posting_creation";
        }
        String email = (String) session.getAttribute(("loggedInUserEmail"));
        if (email == null) {
            return "redirect:/login";
        }


        jobPostingService.createJobListing(form, email);


        redirect.addFlashAttribute("success", "The job listing was posted successfully.");
        return "redirect:/job_posting_creation";
    }

    // US 3.5 (#37): dedicated page holding every job posting created by the logged-in recruiter
    @GetMapping({"/my_job_postings"})
    public String showMyJobPostings(HttpSession session, Model model) {
        if (!isAuth(session)) return "redirect:/login";
        if (!isRecruiter(session)) return "redirect:/dashboard";

        String email = (String) session.getAttribute("loggedInUserEmail");
        List<JobListing> jobListings = jobPostingService.getJobListingsForRecruiter(email);
        Map<Long, Long> applicationCounts = new LinkedHashMap<>();
        for (JobListing jobListing : jobListings) {
            applicationCounts.put(jobListing.getId(), jobApplicationRepository.countByJobListing_Id(jobListing.getId()));
        }
        model.addAttribute("jobListings", jobListings);
        model.addAttribute("applicationCounts", applicationCounts);
        model.addAttribute("today", LocalDate.now());
        return "my_job_postings";
    }

    // US 3.3 (#35): open the job posting form pre-filled, so a recruiter can edit one of their own postings
    @GetMapping("/job_posting/{id}/edit")
    public String showEditJobPosting(@PathVariable Long id, HttpSession session, Model model,
                                     RedirectAttributes redirect) {
        if (!isAuth(session)) return "redirect:/login";
        if (!isRecruiter(session)) return "redirect:/dashboard";

        String email = (String) session.getAttribute("loggedInUserEmail");
        Optional<JobListing> job = jobPostingService.getOwnedJobListing(id, email);
        if (job.isEmpty()) {
            redirect.addFlashAttribute("error", "That job posting could not be found.");
            return "redirect:/my_job_postings";
        }

        model.addAttribute("jobListing", jobPostingService.toForm(job.get()));
        model.addAttribute("editingJobId", id);
        return "job_posting_creation";
    }

    // US 3.3 (#35): save the changes; a recruiter can only update a posting they created
    @PostMapping("/job_posting/{id}/edit")
    public String submitEditJobPosting(@PathVariable Long id,
                                       @Valid @ModelAttribute("jobListing") JobListingForm form, BindingResult result,
                                       HttpSession session, Model model, RedirectAttributes redirect) {
        if (!isAuth(session)) return "redirect:/login";
        if (!isRecruiter(session)) return "redirect:/dashboard";

        String email = (String) session.getAttribute("loggedInUserEmail");
        Optional<JobListing> job = jobPostingService.getOwnedJobListing(id, email);
        if (job.isEmpty()) {
            redirect.addFlashAttribute("error", "That job posting could not be found.");
            return "redirect:/my_job_postings";
        }

        if (result.hasErrors()) {
            model.addAttribute("editingJobId", id);
            return "job_posting_creation";
        }

        jobPostingService.updateJobListing(job.get(), form);
        redirect.addFlashAttribute("success", "Your job posting was updated.");
        return "redirect:/my_job_postings";
    }

    @PostMapping("/job_posting/{id}/delete")
    public String deleteJobPosting(@PathVariable Long id, HttpSession session, RedirectAttributes redirect) {
        if (!isAuth(session)) return "redirect:/login";
        if (!isRecruiter(session)) return "redirect:/dashboard";

        String email = (String) session.getAttribute("loggedInUserEmail");
        Optional<JobListing> job = jobPostingService.getOwnedJobListing(id, email);
        if (job.isEmpty()) {
            redirect.addFlashAttribute("error", "That job posting could not be found.");
            return "redirect:/my_job_postings";
        }

        jobPostingService.deleteJobListing(job.get());
        redirect.addFlashAttribute("success", "The job posting was deleted. Existing applications were marked as Rejected.");
        return "redirect:/my_job_postings";
    }



    @GetMapping({"/login"})
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession session,
            Model model) {

        boolean isAuthenticated =
                userService.authenticate(email, password);

        if (isAuthenticated) {

            session.setAttribute(
                    "loggedInUserEmail",
                    email);

            return "redirect:/dashboard";

        } else {

            model.addAttribute(
                    "loginError",
                    "Invalid email or password!");

            return "login";
        }
    }

    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        model.addAttribute("isRecruiter",isRecruiter(session));
        String email = (String) session.getAttribute("loggedInUserEmail");
        if (email == null) return "redirect:/login";

        Optional<User> user = userService.findByEmail(email);
        user.ifPresent(value -> model.addAttribute("user", value));

        List<Map<String, Object>> countStatus =sankeyData(session);
        Map<String, Long> statusCount = new LinkedHashMap<>();
        for (String stage : STAGES) {
            statusCount.put(stage, 0L);                    // fixed order, zeros included
        }
        long total = 0;
        for (Map<String, Object> l : countStatus) {
            String source = (String) l.get("source");
            String target = (String) l.get("target");
            long value = ((Number) l.get("value")).longValue();

            // everything leaving the first stage = all applications
            if (source.equals("stage:" + STAGES.get(0))) {
                total += value;
            }
            // links ending in Active/Closed = applications whose furthest stage is `source`
            if (target.startsWith("active:") || target.startsWith("closed:")) {
                statusCount.merge(source.substring("stage:".length()), value, Long::sum);
            }
        }

        user.ifPresent(value -> model.addAttribute("user", value));
        model.addAttribute("statusCount", statusCount);
        model.addAttribute("totalApplications", total);

        return "dashboard";
    }


    @GetMapping("/job_postings")
    public String showJobPostings(HttpSession session, Model model) {
        if (!isAuth(session)) return "redirect:/login";
        if (isRecruiter(session)) return "redirect:/dashboard";

        String email = (String) session.getAttribute("loggedInUserEmail");
        model.addAttribute("jobListings", jobPostingService.getAvailableJobListings(email));
        model.addAttribute("resumes", resumeService.list(email));



        return "job-postings";
    }

    @GetMapping("/applications")
    public String showSubmittedApplications(HttpSession session, Model model) {
        if (!isAuth(session)) return "redirect:/login";
        if (isRecruiter(session)) return "redirect:/dashboard";

        String email = (String) session.getAttribute("loggedInUserEmail");
        model.addAttribute("applications", jobApplicationRepository.findByApplicant_EmailOrderBySubmittedAtDesc(email));
        model.addAttribute("applicationStatuses", APPLICATION_STATUSES);
        return "submitted-applications";
    }

    @GetMapping("/job_postings/{jobId}")
    public String showJobDetails(@PathVariable Long jobId, HttpSession session, Model model,
                                 RedirectAttributes redirect) {
        if (!isAuth(session)) return "redirect:/login";
        if (isRecruiter(session)) return "redirect:/dashboard";

        String email = (String) session.getAttribute("loggedInUserEmail");
        Optional<JobListing> job = jobPostingService.getJobListing(jobId);
        if (job.isEmpty()) {
            redirect.addFlashAttribute("error", "The job posting could not be found.");
            return "redirect:/job_postings";
        }
        if (jobApplicationRepository.existsByApplicant_EmailAndJobListing_Id(email, jobId)) {
            redirect.addFlashAttribute("error", "You have already applied to this job.");
            return "redirect:/job_postings";
        }

        model.addAttribute("job", job.get());
        model.addAttribute("resumes", resumeService.list(email));
        return "job-details";
    }

    @PostMapping("/job_postings/{jobId}/apply")
    public String applyToJob(@PathVariable Long jobId,
                             @RequestParam(value = "resumeId", required = false) Long resumeId,
                             @RequestParam(value = "coverLetter", required = false) MultipartFile coverLetter,
                             HttpSession session, RedirectAttributes redirect) {
        if (!isAuth(session)) return "redirect:/login";
        if (isRecruiter(session)) return "redirect:/dashboard";

        String email = (String) session.getAttribute("loggedInUserEmail");
        Optional<User> applicant = userService.findByEmail(email);
        Optional<JobListing> jobListing = jobPostingService.getJobListing(jobId);
        if (applicant.isEmpty() || jobListing.isEmpty()) {
            redirect.addFlashAttribute("error", "The job posting could not be found.");
            return "redirect:/job_postings";
        }
        if (jobApplicationRepository.existsByApplicant_EmailAndJobListing_Id(email, jobId)) {
            redirect.addFlashAttribute("error", "You have already applied to this job.");
            return "redirect:/job_postings";
        }
        List<Resume> resumes = resumeService.list(email);
        Optional<Resume> selectedResume = resumes.stream()
                .filter(resume -> resume.getId().equals(resumeId))
                .findFirst();
        if (selectedResume.isEmpty()) {
            redirect.addFlashAttribute("error", resumes.isEmpty()
                    ? "Upload a resume on your profile before applying."
                    : "Please choose one of your uploaded resumes before applying.");
            return "redirect:/job_postings";
        }
        if (jobListing.get().isCoverLetterRequired() && (coverLetter == null || coverLetter.isEmpty())) {
            redirect.addFlashAttribute("error", "This job requires a cover letter. Please choose a PDF or DOCX file.");
            return "redirect:/job_postings";
        }

        JobApplication application = new JobApplication();
        application.setApplicant(applicant.get());
        application.setJobListing(jobListing.get());
        application.setSelectedResume(selectedResume.get());
        if (jobListing.get().isCoverLetterRequired()) {
            try {
                ResumeService.UploadedDocument uploadedLetter =
                        resumeService.prepareApplicationDocument(coverLetter, "cover letter");
                application.setCoverLetterFilename(uploadedLetter.filename());
                application.setCoverLetterContentType(uploadedLetter.contentType());
                application.setCoverLetterData(uploadedLetter.data());
            } catch (IllegalArgumentException | IllegalStateException exception) {
                redirect.addFlashAttribute("error", exception.getMessage());
                return "redirect:/job_postings";
            }
        }
        jobApplicationRepository.save(application);
        redirect.addFlashAttribute("success", "Your application was submitted.");
        ApplicationSummary applicationSummary = createApplicationSummary(application, jobListing, session);
        applicationSummaryRepository.save(applicationSummary);
        notificationService.newApplicationSubmitted(application);

        return "redirect:/job_postings";
    }

    private static @NonNull ApplicationSummary createApplicationSummary(JobApplication application, Optional<JobListing> jobListing, HttpSession session) {
        String email = (String) session.getAttribute("loggedInUserEmail");
        LocalDate date;
        if (jobListing.get().getDeadline()==null){
            date= LocalDate.of(0, 1, 1);
        }
        else {
            date=jobListing.get().getDeadline();
        }

        ApplicationSummary applicationSummary = new ApplicationSummary();
        applicationSummary.setAppliedDate(application.getSubmittedAt());
        applicationSummary.setClosingDate(date);
        applicationSummary.setCompany(jobListing.get().getCompanyName());
        applicationSummary.setCreationDate(jobListing.get().getPostedDate());
        applicationSummary.setTitle(jobListing.get().getTitle());
        applicationSummary.setStatus(application.getStatus());
        applicationSummary.setId_application(application.getId());
        applicationSummary.setEmail(email);
        applicationSummary.setState("Active");
        return applicationSummary;
    }

    @GetMapping ("/applications")
    public String myApplications(@RequestParam(required = false) String status,
                                 @RequestParam(required = false) String keyword,
                                 @RequestParam(required = false) String state,
                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
                                 HttpSession session, Model model){

        String email = (String) session.getAttribute("loggedInUserEmail");
        if (email == null) return "redirect:/login";
        if (isRecruiter(session)) return "redirect:/dashboard";
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            model.addAttribute("filterError", "The start date must be on or before the end date.");
            fromDate = toDate = null;
        }

        model.addAttribute("applications", jobApplicationRepository.findByApplicant_EmailOrderBySubmittedAtDesc(email));
        model.addAttribute("applicationStatuses", APPLICATION_STATUSES);


        String statusParam  = (status == null || status.isBlank()) ? null : status;
        String keywordParam = (keyword == null) ? "" : keyword.trim();
        String stateParam = (state == null || state.isBlank()) ? null : state;



        List<ApplicationSummary> applicationSummary = applicationSummaryRepository.search(email, statusParam, keywordParam, fromDate, toDate, stateParam);


        model.addAttribute("applicationSummaries", applicationSummary);
        model.addAttribute("applicationStatuses", APPLICATION_STATUSES);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedStatus", statusParam);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("state", state);


        return "/submitted-applications";
    }

    private static final List<String> STAGES = List.of("Applied", "Under Review", "Interview", "Offered");
    private Map<String, Object> link(String source, String target, long value) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("source", source);
        m.put("target", target);
        m.put("value", value);
        return m;
    }
    @GetMapping("/graph/applications")
    @ResponseBody
    public List<Map<String, Object>> sankeyData(HttpSession session) {
        String email = (String) session.getAttribute("loggedInUserEmail");
        if (email == null) return List.of();

        // status -> (state -> count)
        Map<String, Map<String, Long>> counts = new HashMap<>();
        for (Object[] row : applicationSummaryRepository.countByStatusAndState(email)) {
            if (row[0] == null) continue;
            String state = row[1] != null ? (String) row[1] : "Active";   // null state = still active
            counts.computeIfAbsent((String) row[0], k -> new HashMap<>())
                    .merge(state, ((Number) row[2]).longValue(), Long::sum);
        }

        int n = STAGES.size();

        long[] atStage = new long[n];   // applications whose furthest stage is i
        long[] reached = new long[n];   // applications that got to stage i or further
        for (int i = n - 1; i >= 0; i--) {
            atStage[i] = counts.getOrDefault(STAGES.get(i), Map.of())
                    .values().stream().mapToLong(Long::longValue).sum();
            reached[i] = atStage[i] + (i < n - 1 ? reached[i + 1] : 0);
        }

        List<Map<String, Object>> links = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            String stage = STAGES.get(i);

            if (i < n - 1 && reached[i + 1] > 0) {
                links.add(link("stage:" + stage, "stage:" + STAGES.get(i + 1), reached[i + 1]));
            }
            long closed = counts.getOrDefault(stage, Map.of()).getOrDefault("Closed", 0L);
            long active = atStage[i] - closed;   // everything not Closed is still in progress

            if (closed > 0) links.add(link("stage:" + stage, "closed:" + stage, closed));
            if (active > 0) links.add(link("stage:" + stage, "active:" + stage, active));
        }
        return links;
    }


    @GetMapping("/recruiter/applications")
    public String showRecruiterApplications(@RequestParam(value = "status", defaultValue = "") String status,
                                            @RequestParam(value = "jobId", required = false) Long jobId,
                                            @RequestParam(value = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                            @RequestParam(value = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
                                            HttpSession session, Model model) {
        if (!isAuth(session)) return "redirect:/login";
        if (!isRecruiter(session)) return "redirect:/dashboard";

        String email = (String) session.getAttribute("loggedInUserEmail");
        List<JobApplication> allApplications = jobApplicationRepository.findByJobListing_Recruiter_EmailOrderBySubmittedAtDesc(email);
        List<JobApplication> activeApplications = allApplications.stream()
                .filter(application -> !application.getJobListing().isDeleted())
                .toList();
        List<JobApplication> deletedApplications = allApplications.stream()
                .filter(application -> application.getJobListing().isDeleted())
                .toList();
        String selectedStatus = APPLICATION_STATUSES.contains(status) ? status : "";
        boolean invalidDateRange = fromDate != null && toDate != null && fromDate.isAfter(toDate);
        List<JobApplication> applications = invalidDateRange ? List.of() : activeApplications.stream()
                .filter(application -> selectedStatus.isEmpty() || selectedStatus.equals(application.getStatus()))
                .filter(application -> jobId == null || jobId.equals(application.getJobListing().getId()))
                .filter(application -> fromDate == null || (application.getSubmittedAt() != null && !application.getSubmittedAt().isBefore(fromDate)))
                .filter(application -> toDate == null || (application.getSubmittedAt() != null && !application.getSubmittedAt().isAfter(toDate)))
                .toList();
        Map<JobListing, List<JobApplication>> applicationsByJob = applications.stream().collect(Collectors.groupingBy(JobApplication::getJobListing, LinkedHashMap::new, Collectors.toList()));
        Map<JobListing, List<JobApplication>> deletedApplicationsByJob = deletedApplications.stream().collect(Collectors.groupingBy(JobApplication::getJobListing, LinkedHashMap::new, Collectors.toList()));
        Map<String, Long> statusCounts = new LinkedHashMap<>();
        for (String applicationStatus : APPLICATION_STATUSES) {
            statusCounts.put(applicationStatus, activeApplications.stream()
                    .filter(application -> applicationStatus.equals(application.getStatus()))
                    .count());
        }
        model.addAttribute("applicationsByJob", applicationsByJob);
        model.addAttribute("deletedApplicationsByJob", deletedApplicationsByJob);
        model.addAttribute("applicationStatuses", APPLICATION_STATUSES);
        model.addAttribute("selectedStatus", selectedStatus);
        model.addAttribute("statusCounts", statusCounts);
        model.addAttribute("totalApplications", activeApplications.size());
        model.addAttribute("displayedApplications", applications.size());
        model.addAttribute("deletedApplicationsCount", deletedApplications.size());
        model.addAttribute("jobPostings", jobPostingService.getJobListingsForRecruiter(email));
        model.addAttribute("selectedJobId", jobId);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("filterError", invalidDateRange ? "The start date must be on or before the end date." : null);
        return "recruiter-applications";
    }

    // US 6.3 (#68): the statuses a recruiter can give an application
    private static final List<String> APPLICATION_STATUSES =
            List.of("Applied", "Under Review", "Interview", "Offered", "Rejected");

    // US 6.3 (#68): recruiter changes the status of an application to one of their own job postings
    @PostMapping("/recruiter/applications/{applicationId}/status")
    public String updateApplicationStatus(@PathVariable Long applicationId, @RequestParam("status") String status,
                                          HttpSession session, RedirectAttributes redirect) {
        if (!isAuth(session)) return "redirect:/login";
        if (!isRecruiter(session)) return "redirect:/dashboard";

        String email = (String) session.getAttribute("loggedInUserEmail");
        Optional<JobApplication> application = jobApplicationRepository.findByIdAndJobListing_Recruiter_Email(applicationId, email);
        applicationSummaryRepository.findByApplicationId(applicationId).ifPresent(
                applicationSummary -> {
                    if (status.equals("Rejected")) {
                        applicationSummary.setState("Closed");
                    }
                    else{
                        applicationSummary.setStatus(status);
                        applicationSummaryRepository.save(applicationSummary);
                    }

                });

/*
        JobApplication selectedApplication = application.get();

        // Remember the previous status
        String previousStatus = selectedApplication.getStatus();

        // Update the application
        selectedApplication.setStatus(status);

        // Save changes
        jobApplicationRepository.save(selectedApplication);

        // Notify the job seeker only if the status changed
        notificationService.applicationStatusChanged(
                selectedApplication,
                previousStatus
        );
       */ //from sukh's version, need review










        if (application.isEmpty()) {
            redirect.addFlashAttribute("error", "That application could not be found.");
            return "redirect:/recruiter/applications";
        }
        if (!APPLICATION_STATUSES.contains(status)) {
            redirect.addFlashAttribute("error", "Please choose a valid status.");
            return "redirect:/recruiter/applications";
        }

        application.get().setStatus(status);
        jobApplicationRepository.save(application.get());
        redirect.addFlashAttribute("success", "Status updated to \"" + status + "\".");
        return "redirect:/recruiter/applications";
    }

    @GetMapping("/recruiter/applications/{applicationId}/resume")
    public ResponseEntity<Resource> downloadApplicantResume(@PathVariable Long applicationId, HttpSession session) throws MalformedURLException {
        if (!isAuth(session) || !isRecruiter(session)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        String email = (String) session.getAttribute("loggedInUserEmail");
        Optional<JobApplication> application = jobApplicationRepository.findByIdAndJobListing_Recruiter_Email(applicationId, email);
        if (application.isEmpty() || application.get().getSelectedResume() == null) return ResponseEntity.notFound().build();

        Resume resume = application.get().getSelectedResume();
        Resource resumeFile = new ByteArrayResource(resume.getData());
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resume.getFilename() + "\"").body(resumeFile);
    }

    @GetMapping("/recruiter/applications/{applicationId}/cover-letter")
    public ResponseEntity<Resource> downloadApplicantCoverLetter(@PathVariable Long applicationId, HttpSession session) {
        if (!isAuth(session) || !isRecruiter(session)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        String email = (String) session.getAttribute("loggedInUserEmail");
        Optional<JobApplication> application = jobApplicationRepository.findByIdAndJobListing_Recruiter_Email(applicationId, email);
        if (application.isEmpty() || !application.get().hasCoverLetter()) return ResponseEntity.notFound().build();

        Resource coverLetter = new ByteArrayResource(application.get().getCoverLetterData());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + application.get().getCoverLetterFilename() + "\"")
                .body(coverLetter);
    }
    // Updated profile page
    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        model.addAttribute("isRecruiter",isRecruiter(session));


        String email = (String) session.getAttribute("loggedInUserEmail");
        if (email == null) return "redirect:/login";

        Optional<User> user = userService.findByEmail(email);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());


            // Load all resumes for this user
            model.addAttribute(
                    "resumes",
                    resumeService.list(email));

            return "profile";
        }

        return "redirect:/login";
    }

    @PostMapping("/profile/update")
    public String updateProfile(
            @ModelAttribute("user") User updatedUser,
            HttpSession session) {

        String email = (String) session.getAttribute(
                "loggedInUserEmail");

        if (email == null) {
            return "redirect:/login";
        }

        userService.updateProfile(
                email,
                updatedUser);

        return "redirect:/profile?success=true";
    }


    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("user") User user,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "register";
        }
        try {
            userService.registerUser(user);
        } catch (RuntimeException e) {

            model.addAttribute(
                    "emailError",
                    e.getMessage());

            return "register";
        }
        return "redirect:/register?success";
    }

    @GetMapping("/jobs")
    public String showJobs(

            @RequestParam(name = "keyword", defaultValue = "")
            String keyword,

            @RequestParam(name = "location", defaultValue = "")
            String location,

            @RequestParam(name = "jobType", defaultValue = "")
            String jobType,

            Model model,
            HttpSession session,
            RedirectAttributes redirect) {

        model.addAttribute("isAuth", isAuth(session));
        if (!isAuth(session)){
            return "redirect:/";
        }


        //Remove extra spaces to match job postings
        String cleanKeyword = keyword.trim();
        String cleanLocation = location.trim();
        String cleanJobType = jobType.trim();
        String email = (String) session.getAttribute("loggedInUserEmail");

        //Calls search query returning list of matching jobs
        List<JobListing> jobs = jobRepository.search(cleanKeyword, cleanLocation, cleanJobType, email);

        //Attributes from the database
        model.addAttribute("jobs", jobs);
        model.addAttribute("locations", jobRepository.findDistinctLocations());
        model.addAttribute("jobTypes", jobRepository.findDistinctJobTypes());
        model.addAttribute("resumes", resumeService.list(email));

        //Attributes from the user
        model.addAttribute("keyword", cleanKeyword);
        model.addAttribute("selectedLocation", cleanLocation);
        model.addAttribute("selectedJobType", cleanJobType);

        //Attribute from user selection
        model.addAttribute("filtersActive",!cleanKeyword.isEmpty() || !cleanLocation.isEmpty() || !cleanJobType.isEmpty());

        return "jobs";


    }






}
