# Sprint 4 Test and Demo Guide

This guide covers the Job Application Submission and Tracking flow (Task 3)
and the Recruiter Dashboard and Job Posting Management flow (Task 4).

## Before the demo

1. Run the application with Java 21 and start the database used by the project.
2. Have one job seeker account and one recruiter account available.
3. Prepare at least one open job posting, one application, and one resume.
4. Open the repository's **Actions** page in a second browser tab so the green
   CI workflow can be shown.

## Task 3: Job seeker application tracking

1. Sign in as a job seeker and open **Browse Jobs**.
2. Open an available job posting, select a resume, and submit the application.
   - Expected: a submission confirmation appears.
3. Open **My Applications**.
   - Expected: the application appears with status **Applied** and its timeline.
4. Sign in as the recruiter that owns the posting. Open **Applications**, change
   the candidate's status to **Under Review**, then save.
5. Sign back in as the job seeker and refresh **My Applications**.
   - Expected: the same application now shows **Under Review** in both the badge
     and timeline.
6. Optionally repeat with **Interview**, **Offered**, and **Rejected**.
   - Expected: rejected applications are retained in history and shown as closed.

## Task 4: Recruiter dashboard and applications

1. Sign in as a recruiter and open **My Job Postings**.
2. Create a new posting, then edit a field and save it.
   - Expected: the posting appears with the updated information.
3. Enter a minimum salary greater than the maximum salary and submit.
   - Expected: validation prevents publication and shows the salary-range error.
4. Open **Applications** from the recruiter navigation.
5. Use the status filter and, separately, the submitted-date filters.
   - Expected: only matching applications are shown and the shown count changes.
6. Enter a submitted-from date later than the submitted-to date.
   - Expected: no applications are shown and the page explains that the start
     date must be on or before the end date.
7. Change one candidate's status and save.
   - Expected: the status badge updates, and the job seeker sees the same status
     in **My Applications**.
8. Delete an expired/closed posting that has applications.
   - Expected: the posting is removed from active recruiter postings, while its
     applications remain available in the recruiter reference/history area.

## Automated checks and CI evidence

- Run `mvn test` from `career_connect_app` when the local Maven/JDK setup is
  available.
- The GitHub Actions workflow runs the Maven tests, builds the application with
  Java 21, and uploads the PMD static-analysis report.
- For the demo, show a recent green **CareerConnect CI** run and open the
  `pmd-static-analysis-report` artifact if requested.

## Suggested presentation order

1. Show the green CI run and briefly explain automated tests plus PMD.
2. Demonstrate the Task 3 job seeker flow from application through tracking.
3. Switch to the recruiter account and demonstrate filtering plus status update.
4. Return to the job seeker view to prove the status is synchronized.
5. Show the expired-posting archive behavior and explain that application
   history is preserved.
