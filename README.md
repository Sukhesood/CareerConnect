# Description of the project

CareerConnect is a web-based application built for both job seekers and recruiters. It centralizes job offers in a single page, allowing applicants to track and apply efficiently, while providing the recruiters with  
better filtering and visibility.


# Problem the project is solving:

There are multiple job seeking platforms, which fragments the list of open positions accross the market. This causes larger overhead and 
time loss in a market that may require hundreds of applications to get a single positive response. On the other side, recruiters are also required to spend a large amount of time
searching and veryfing an applicant for each position. 

# Solution offered :

Through CareerConnect, seekers will be able to centralize their demand in one plateform and upload their resume for faster application. Filtering will also streamline
their research process. AI service are also offered to accelerate the creation of cover letter to generate higher odds of results. 
On the other hand, for recruiter, CareerConnect offers methods to streamline the screening process by reviewing the information given to the recruiter to filter
based on the desired criteria for the role.


# Features :
System Features
- User registration, authentication, and profile management.
- Resume upload and management.
- Job posting management for recruiters.
- Job search and filtering capabilities.
- Job application submission.
- Application status tracking (Applied, Interview, Offered, Rejected).
- Application history dashboard.
- Notifications and reminders for application deadlines.
- Saved jobs and favourites.
- AI generated cover letter as default when applying as job seeker.
- AI review of information and projects linked in the job application.
- fetching application from other open database
- Method for seeker to add job application from other website to the tracking through manual input.
- Chat message available between recruiter and seeker


# Setup instruction: 

Join the github repo:  

https://github.com/Sukhesood/CareerConnect  

## Tools needed: 

Git, github, VS Code, Eclipse IDE

## External libraries:
Thymeleaf, Apache Tomcat, Spring Boot

# Technical description : 

## Technologies used :

**Architecture**: Model View Controller(MVC)
**Programming language**: Java, HTML
**Framework**: Springboot
**Database**: MySQL
**Artefacts**: Docker 
**Collaboration**: Discord, Git and GitHub

## Architecture overview :
```
career_connect_app/
├── src/
│    └── main/
│       └── java/
│            └── sprint1.demo/
│                ├── Controller
│                ├── model
│                ├── repository
│                ├── service
│                └── CareerConnectApplication
├── target/
│    ├── classes/
│        ├── Sprint1/
│            └── demo/
│                ├── Controller
│                ├── model
│                ├── repository
│                ├── service
│                └── CareerConnectApplication
│        ├── templates/
│            ├── dashboard.html
│            ├── login.html
│            ├── profil.html
│            └── register.html
│        ├── application.properties
│        └── application-local.properties
│     └── generated-sources/
│         └── annotations
├── view/
│     └── index.html                
└── pom.xml
```

## Organisational coding methodology :

We follow the basic principle of Agile developpement, with its use of branching, pull request and work items.


## Our team :

|      Name       |  Roles         |     Skills
| --------------- |  ------------- | -----------------
| ELOUAN BESNIER  | Scrum master   | Agile, programming, SDLC
| MARIA HERRERA   | Team member    | Minutes tracking, files organization
| SUKHJIT SINGH   | Team member    | Programming, testing, database managment
| RABIH EL MAROUK | Team member    | Programming, planning
| MARK KOUKA      | Team member    | Programming, admission critera definition



















