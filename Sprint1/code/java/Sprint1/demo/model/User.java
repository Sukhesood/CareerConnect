 package Sprint1.demo.model;

 import jakarta.persistence.*;
 import jakarta.validation.constraints.Email;
 import jakarta.validation.constraints.NotBlank;
 import jakarta.validation.constraints.Pattern;
 import jakarta.validation.constraints.Size;

 @Entity
 @Table(name = "users")
 public class User {

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;

     @NotBlank(message = "First name is mandatory")
     private String firstName;

     @NotBlank(message = "Last name is mandatory")
     private String lastName;

     @NotBlank(message = "Phone number is mandatory")
     @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Invalid phone number format")
     private String phoneNumber;

     @NotBlank(message = "Address is mandatory")
     private String address;

     @NotBlank(message = "Email is mandatory")
     @Email(message = "Invalid email format")
     @Column(unique = true)
     private String email;

     @NotBlank(message = "Password is mandatory")
     @Size(min = 6, message = "Password must be at least 6 characters long")
     private String password;

     @NotBlank(message = "Role is mandatory")
     private String role; // "JOB_SEEKER" or "RECRUITER"

     // Default Constructor
     public User() {
     }

     // Getters and Setters
     public Long getId() {
         return id;
     }

     public void setId(Long id) {
         this.id = id;
     }

     public String getFirstName() {
         return firstName;
     }

     public void setFirstName(String firstName) {
         this.firstName = firstName;
     }

     public String getLastName() {
         return lastName;
     }

     public void setLastName(String lastName) {
         this.lastName = lastName;
     }

     public String getPhoneNumber() {
         return phoneNumber;
     }

     public void setPhoneNumber(String phoneNumber) {
         this.phoneNumber = phoneNumber;
     }

     public String getAddress() {
         return address;
     }

     public void setAddress(String address) {
         this.address = address;
     }

     public String getEmail() {
         return email;
     }

     public void setEmail(String email) {
         this.email = email;
     }

     public String getPassword() {
         return password;
     }

     public void setPassword(String password) {
         this.password = password;
     }

     public String getRole() {
         return role;
     }

     public void setRole(String role) {
         this.role = role;
     }
     private String resumeFilename;

  // Add getter and setter:
  public String getResumeFilename() {
      return resumeFilename;
  }

  public void setResumeFilename(String resumeFilename) {
      this.resumeFilename = resumeFilename;
  }
 }