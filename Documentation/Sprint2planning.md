# CareerConnect – Sprint 2 Planning

## Sprint Goal

The goal of Sprint 2 is to continue developing the CareerConnect platform by improving existing Sprint 1 features and implementing the main job search, job posting, application submission, and application tracking features.

The team will focus on:

- Improving the existing user registration, authentication, and profile functionality
- Improving resume upload and management
- Creating recruiter job posting management
- Creating job search and filtering functionality
- Implementing job application submission
- Implementing application status tracking
- Continuing development of the project foundation and navigation
- Fixing known issues from Sprint 1
- Creating acceptance tests
- Setting up Continuous Integration
- Maintaining Sprint 2 documentation and contribution tracking

---

## Sprint 2 Feature Backlog

### Feature #0 – Setting Up the Foundation

This feature provides the base structure required for the rest of Sprint 2 development.

Planned work:

- Create and improve the main application navigation
- Maintain the home page structure
- Support navigation between the different CareerConnect features
- Maintain the backend and frontend structure required for Sprint 2 features

Status: In Progress

---

### Feature #1 – User Registration, Authentication, and Profile Management

Sprint 2 will continue improving the user account and profile functionality developed during Sprint 1.

Planned work:

- Maintain user registration functionality
- Maintain login and authentication functionality
- Allow users to update profile information
- Improve profile management
- Support profile information required by later Sprint 2 features

Status: In Progress

---

### Feature #2 – Resume Upload and Management

This feature focuses on improving resume and document management for job seekers.

Planned work:

- Allow users to upload resumes
- Allow users to upload more than one resume
- Allow users to select the correct resume when applying
- Allow users to upload cover letters
- Display uploaded resumes
- Improve resume storage and retrieval
- Fix existing resume storage issues

Related User Stories:

- Issue #29 – User Story 2.1  
  As a job seeker, I wish to upload my resume only once.

- Issue #30 – User Story 2.2  
  As a job seeker, I wish to select the correct resume from a list when applying.

- Issue #31 – User Story 2.3  
  As a job seeker, I wish to upload more than one resume and view the different resumes.

- Issue #32 – User Story 2.4  
  As a job seeker, I wish to upload resumes and cover letters.

Related Bug:

- Issue #67 – Faulty storage method of resume

Status: In Progress

---

### Feature #3 – Job Posting Management for Recruiters

This feature allows recruiters to create and manage job postings.

Planned work:

- Allow recruiters to create job postings
- Allow recruiters to modify existing job postings
- Restrict job posting management to the recruiter who created the posting
- Create a dedicated recruiter job posting page
- Allow recruiters to view and manage their current postings

Related User Stories:

- Issue #33 – User Story 3.1  
  As a recruiter, I wish to have exclusive job posting capabilities.

- Issue #34 – User Story 3.2  
  As a recruiter, I wish to create new job postings.

- Issue #35 – User Story 3.3  
  As a recruiter, I wish to modify job postings that have already been created.

- Issue #36 – User Story 3.4  
  As a recruiter, I wish to have a pop-up window to create a new job posting.

- Issue #37 – User Story 3.5  
  As a recruiter, I wish to have a dedicated web page that holds all job postings created by a recruiter or company.

Status: Not Started

---

### Feature #4 – Job Search and Filtering Capabilities

This feature allows job seekers to browse, search, and filter available job postings.

Planned work:

- Create a dedicated web page for job postings
- Display available job opportunities
- Search job postings by title
- Search job postings by company
- Search job postings by field of work
- Filter jobs using different criteria
- Display basic job information such as title, company, location, and category

Related User Stories:

- Issue #38 – User Story 4.1  
  As a job seeker, I wish to search job postings by title, company, and field of work.

- Issue #39 – User Story 4.2  
  As a job seeker, I wish to filter job postings based on different criteria.

- Issue #40 – User Story 4.3  
  As a job seeker, I wish to have a dedicated web page for job postings.

Status: In Progress

---

### Feature #5 – Job Application Submission

This feature allows job seekers to submit applications and allows recruiters to receive and review them.

Planned work:

- Allow job seekers to submit applications
- Create a dedicated page for submitted applications
- Allow recruiters to receive applications
- Display applicant information and submitted documents
- Prevent duplicate applications to the same job posting
- Group recruiter applications by job posting

Related User Stories:

- Issue #42 – User Story 5.1  
  As a job seeker, I wish to have a specific web page that holds all my applications.

- Issue #43 – User Story 5.2  
  As a recruiter, I wish to receive job seeker applications with their submitted documents.

- Issue #44 – User Story 5.3  
  As a job seeker, I do not wish to see job postings that I have already applied to.

- Issue #70 – User Story 5.4  
  As a recruiter, I wish to have received applications displayed on a specific web page and grouped by job posting.

Status: Not Started

---

### Feature #6 – Application Status Tracking

This feature allows job seekers and recruiters to follow the progress of submitted applications.

Planned work:

- Allow job seekers to view submitted applications
- Display the current status of each application
- Allow recruiters to update application status
- Allow recruiters to view their current job listings
- Maintain application status information

Application statuses may include:

- Applied
- Under Review
- Interview
- Offered
- Rejected

Related User Stories:

- Issue #45 – User Story 6.1  
  As a job seeker, I wish to see all applications I have submitted.

- Issue #46 – User Story 6.2  
  As a job seeker, I wish to have a real-time status tracker for each of my applications.

- Issue #68 – User Story 6.3  
  As a recruiter, I wish to be able to change the recruitment status of an applicant.

- Issue #69 – User Story 6.4  
  As a recruiter, I wish to see all current job listings that I have posted.

Status: Not Started

---

## Sprint 2 User Story Format

Each Sprint 2 user story should include:

- User Story description
- Implementation tasks
- Acceptance criteria
- Priority
- Story points
- Status
- Assigned team member

User stories should be divided into small implementation tasks so that the work can be estimated, assigned, implemented, and tested.

---

## Example Task Breakdown

### User Story 4.3 – Dedicated Job Postings Page

As a job seeker, I want a dedicated web page for job postings so that I can easily view available job opportunities in one place.

Tasks:

- Task 4.3.1 – Create a controller route for the job postings page
- Task 4.3.2 – Create a dedicated HTML page for job postings
- Task 4.3.3 – Display basic job information such as title, company, location, and category

Acceptance Criteria:

- The user can access the job postings page from the application
- Available job postings are displayed on the page
- Each job posting shows at least the job title, company, and location
- The page loads without errors

Priority: High

Story Points: 5

Status: In Progress

---

## Feature #5 Task Breakdown

### User Story 5.1 – Submitted Applications Page

As a job seeker, I want a dedicated web page that displays all of my submitted applications so that I can easily review and manage them.

Tasks:

- Create a controller route for the applications page
- Create a dedicated HTML page for submitted applications
- Retrieve and display applications submitted by the logged-in job seeker

Acceptance Criteria:

- The job seeker can access the applications page
- Only applications submitted by the logged-in user are displayed
- Each application shows at least the job title, company, and application status
- The page loads without errors

Priority: High

Story Points: 5

Status: Not Started

---

### User Story 5.2 – Recruiter Receives Applications

As a recruiter, I want to receive job applications together with the applicant's submitted documents so that I can review candidates for my job postings.

Tasks:

- Link submitted applications to the correct job posting
- Allow recruiters to view applicants for their job postings
- Display applicant information and submitted resume or documents

Acceptance Criteria:

- A recruiter can view applications received for their job postings
- Each application identifies the applicant and related job posting
- Submitted documents are available to the recruiter
- Recruiters cannot view applications belonging to another recruiter's job postings

Priority: High

Story Points: 8

Status: Not Started

---

### User Story 5.3 – Prevent Duplicate Applications

As a job seeker, I do not want to see job postings that I have already applied to so that I do not accidentally submit duplicate applications.

Tasks:

- Check whether the logged-in user has already applied to a job
- Prevent duplicate applications to the same job posting
- Hide or clearly identify job postings that have already been applied to

Acceptance Criteria:

- A user cannot submit more than one application to the same job posting
- Jobs already applied to are not shown as available for application
- Other job postings remain available normally
- The application history still contains the submitted application

Priority: Medium

Story Points: 5

Status: Not Started

---

### User Story 5.4 – Recruiter Applications Grouped by Job Posting

As a recruiter, I want a dedicated page where received applications are grouped by job posting so that I can review candidates efficiently.

Tasks:

- Create a recruiter applications page
- Group received applications by job posting
- Display applicant information and application status for each posting

Acceptance Criteria:

- The recruiter can access a page containing received applications
- Applications are grouped under the correct job posting
- Each application shows the applicant and current application status
- Only applications related to the recruiter's own postings are displayed

Priority: High

Story Points: 5

Status: Not Started

---

## Acceptance Tests

At least two acceptance tests should be created for every user story from Sprint 1 and Sprint 2.

Acceptance tests should:

- Be stored as GitHub issues
- Be labeled appropriately
- Be linked to the corresponding user story
- Be linked to implementation tasks when applicable
- Clearly state the expected behavior
- Be updated when the related feature changes

---

## Sprint Backlog

| Feature / Issue | Description | Priority | Status |
|---|---|---|---|
| Feature #0 | Setting up the foundation | High | In Progress |
| Feature #1 | User registration, authentication, and profile management | High | In Progress |
| Feature #2 | Resume upload and management | High | In Progress |
| Bug #67 | Faulty storage method of resume | High | In Progress |
| Feature #3 | Job posting management for recruiters | High | Not Started |
| Feature #4 | Job search and filtering capabilities | High | In Progress |
| Feature #5 | Job application submission | High | Not Started |
| Feature #6 | Application status tracking | High | Not Started |

---

## Sprint 2 Project Management

During Sprint 2, the team will:

- Maintain the GitHub Project Board
- Keep Sprint 2 issues updated
- Assign user stories and tasks to team members
- Update issue statuses as work progresses
- Use separate branches for implementation work
- Use pull requests before merging changes
- Link pull requests and commits to related issues
- Review completed work before merging
- Maintain the sprint backlog and planning documentation

---

## Continuous Integration

The team will configure and demonstrate a Continuous Integration pipeline.

The CI pipeline should:

- Run automatically when appropriate repository changes are made
- Build the project
- Run available automated tests
- Help identify integration or compilation problems
- Help ensure that new changes do not break existing functionality

---

## AI Usage

All AI-assisted work must continue to be documented in the `AI_Logs` directory.

Each AI usage entry should include:

- Task ID or title
- Purpose of AI use
- Chat link or prompt and response
- AI-suggested content
- Validation performed by the team member
- Final decision
- Reflection
- Responsible team member

AI-assisted work may include:

- Requirements analysis
- User story refinement
- Planning
- Coding
- Debugging
- Testing
- Documentation
- Design suggestions

---

## Meeting Minutes

Meeting minutes will continue to document:

- Attendance
- Decisions made
- Tasks assigned
- Work completed
- Problems encountered
- Action items
- Next steps

All meeting minutes should be uploaded to the repository.

---

## Individual Contribution Log

Each team member must maintain a detailed contribution log.

The contribution log should include:

- Task completed
- Description of contribution
- Time spent
- GitHub issue link
- Commit link
- Pull request link
- Documentation contribution
- Testing contribution
- AI usage when applicable

---

## Definition of Done

A Sprint 2 task is considered complete when:

- The implementation is finished
- The feature works as expected
- The acceptance criteria are satisfied
- Required acceptance tests are completed
- The code has been tested
- The code is committed to GitHub
- A pull request is created when applicable
- The pull request is reviewed before merging
- Documentation is updated
- The GitHub issue status is updated
- The contribution is included in the responsible team member's contribution log
