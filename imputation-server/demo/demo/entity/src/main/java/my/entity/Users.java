package my.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class Users {
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
	
	@NotBlank(message = "please enter your username")
    private String username;

//	@NotBlank(message = "please enter your password")
    private String password;

    private String passwordSalt;
    
    @NotBlank(message = "please enter your email")
	@Email(message = "the email is incorrect")
    private String email;

    private String fullname;

    private String userRegTime;

    private String userStatus;

    private String userDesc;

    @NotBlank(message = "please enter your first name")
    private String firstName;
    
    @NotBlank(message = "please enter your last name")
    private String lastName;

    @NotBlank(message = "please enter your job title")
    private String jobTitle;

    @NotBlank(message = "please enter your organisation name")
    private String organisation;

    private String country;

    private String province;

    private String city;

    private String userUnregTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt == null ? null : passwordSalt.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname == null ? null : fullname.trim();
    }

    public String getUserRegTime() {
        return userRegTime;
    }

    public void setUserRegTime(String userRegTime) {
        this.userRegTime = userRegTime;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus == null ? null : userStatus.trim();
    }

    public String getUserDesc() {
        return userDesc;
    }

    public void setUserDesc(String userDesc) {
        this.userDesc = userDesc == null ? null : userDesc.trim();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName == null ? null : firstName.trim();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName == null ? null : lastName.trim();
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle == null ? null : jobTitle.trim();
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation == null ? null : organisation.trim();
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country == null ? null : country.trim();
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province == null ? null : province.trim();
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city == null ? null : city.trim();
    }

    public String getUserUnregTime() {
        return userUnregTime;
    }

    public void setUserUnregTime(String userUnregTime) {
        this.userUnregTime = userUnregTime;
    }
}