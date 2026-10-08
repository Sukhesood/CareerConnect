# CareerConnect – Sprint 2 Planning

## Sprint Goal

The goal of Sprint 2 is to continue developing the CareerConnect platform by improving existing Sprint 1 features and implementing the main job search and application features.

The team will focus on:
- User profile and resume management
- Job search and job listing interface
- Job application submission and tracking
- Initial recruiter dashboard and job posting management
- Acceptance testing
- Continuous Integration
- Sprint documentation and contribution tracking

## Sprint 2 Required Features

### 1. User Profile and Resume Management
- Allow job seekers to create and edit their professional profile
- Allow users to upload, replace, and manage resumes
- Display uploaded resumes and profile information

### 2. Job Search and Job Listing Interface
- Display available job opportunities
- Search jobs using keywords
- Filter jobs by location, category, or other criteria
- View detailed job descriptions

### 3. Job Application Submission and Tracking
- Allow job seekers to apply to job postings
- Track application status
- Display confirmation after successful submission

Application statuses:
- Applied
- Under Review
- Interview
- Offered
- Rejected

### 4. Recruiter Dashboard and Posting Management
Sprint 2 will begin development of this feature.

- Allow recruiters to create job postings
- Allow recruiters to manage their job postings
- Display applications received for a posting

## Sprint 2 User Stories

### Feature 2 – Resume Management

- Issue #29 – User Story 2.1  
  As a job seeker, I wish to upload my resume only once.

- Issue #30 – User Story 2.2  
  As a job seeker, I wish to select the correct resume when applying.

- Issue #31 – User Story 2.3  
  As a job seeker, I wish to upload more than one resume and view the different resumes.

- Issue #32 – User Story 2.4  
  As a job seeker, I wish to upload resumes and cover letters.

### Feature 4 – Job Search and Filtering

- Issue #38 – User Story 4.1  
  As a job seeker, I wish to search job postings by title, company, and field of work.

- Issue #39 – User Story 4.2  
  As a job seeker, I wish to filter job postings using different criteria.

- Issue #40 – User Story 4.3  
  As a job seeker, I wish to have a dedicated web page for job postings.

### Feature 5 – Job Applications

- Issue #42 – User Story 5.1  
  As a job seeker, I wish to have a specific page that shows all my applications.

- Issue #43 – User Story 5.2  
  As a recruiter, I wish to receive job applications with the submitted documents.

- Issue #44 – User Story 5.3  
  As a job seeker, I do not wish to see job postings that I have already applied to.

- Issue #70 – User Story 5.4  
  As a recruiter, I wish to have received applications grouped by job posting.

### Feature 6 – Application Status Tracking

- Issue #45 – User Story 6.1  
  As a job seeker, I wish to see all applications I have submitted.

- Issue #46 – User Story 6.2  
  As a job seeker, I wish to have a real-time status tracker for each application.

- Issue #68 – User Story 6.3  
  As a recruiter, I wish to change the recruitment status of an applicant.

- Issue #69 – User Story 6.4  
  As a recruiter, I wish to see all current job listings I have posted.

## Task Breakdown

Each user story should be divided into implementation tasks.

Example for User Story 4.3:

- Task 4.3.1 – Create controller route for job postings page
- Task 4.3.2 – Create job postings HTML page
- Task 4.3.3 – Display job information such as title, company, location, and category

## Acceptance Criteria

Each user story must include clear acceptance criteria.

Example for User Story 4.3:

- The user can access the job postings page
- Available jobs are displayed
- Each job shows at least title, company, and location
- The page loads without errors

## Acceptance Tests

At least two acceptance tests must be created for every user story from Sprint 1 and Sprint 2.

Acceptance tests should:
- Be stored as GitHub issues
- Be labeled as acceptance tests
- Be linked to the related user story
- Be linked to the implementation tasks when applicable

## Sprint Backlog

| Issue | User Story / Task | Assigned Member | Priority | Story Points | Status |
|---|---|---|---|---|---|
| #29 | Resume upload once | TBD | High | TBD | Not Started |
| #30 | Resume selection | TBD | Medium | TBD | Not Started |
| #31 | Multiple resumes | TBD | High | TBD | Not Started |
| #32 | Resume and cover letter upload | TBD | Medium | TBD | Not Started |
| #38 | Job search | TBD | High | TBD | Not Started |
| #39 | Job filtering | TBD | High | TBD | Not Started |
| #40 | Job postings page | Mark | High | 5 | In Progress |
| #42 | Applications page | TBD | High | TBD | Not Started |
| #43 | Recruiter application view | TBD | High | TBD | Not Started |
| #44 | Hide already-applied jobs | TBD | Medium | TBD | Not Started |
| #45 | View submitted applications | TBD | High | TBD | Not Started |
| #46 | Application status tracker | TBD | High | TBD | Not Started |
| #68 | Recruiter updates application status | TBD | High | TBD | Not Started |
| #69 | Recruiter job listings | TBD | Medium | TBD | Not Started |
| #70 | Recruiter grouped applications | TBD | Medium | TBD | Not Started |

## Continuous Integration

The team will configure a CI pipeline to automatically build and test the project when changes are pushed to the repository.

The pipeline should help verify that new changes do not break existing functionality.

## Project Management

During Sprint 2, the team will:

- Maintain the GitHub Project Board
- Update issue statuses
- Assign tasks to team members
- Create branches for implementation work
- Use pull requests for merging changes
- Review completed work before merging

## AI Usage

All AI-assisted work will continue to be documented in the AI_Log directory.

Each entry should include:
- Task
- Purpose of AI use
- Prompt or chat link
- AI-generated suggestion
- Validation
- Final decision
- Reflection
- Responsible team member

## Meeting Minutes

Meeting minutes will continue to document:
- Attendance
- Decisions
- Tasks assigned
- Progress
- Problems encountered
- Next steps

## Individual Contribution Log

Each team member will maintain a contribution log containing:

- Task completed
- Time spent
- GitHub issue link
- Commit link
- Pull request link
- Documentation contribution
- Testing contribution

## Definition of Done

A Sprint 2 task is considered complete when:

- The implementation is finished
- The feature has been tested
- Acceptance criteria are satisfied
- Required acceptance tests are completed
- Code is committed to GitHub
- A pull request is created and reviewed
- Documentation is updated
- The GitHub issue is updated or closed
