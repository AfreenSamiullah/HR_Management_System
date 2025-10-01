public class Employee {
    private int empId;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private int age;
    private String gender;
    private int yearsExperience;
    private double salary;
    private String department;
    private Integer managerId;

    public Employee(int empId, String firstName, String lastName, String phone,
                    String email, int age, String gender, int yearsExperience,
                    double salary, String department, Integer managerId) {
        this.empId = empId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.age = age;
        this.gender = gender;
        this.yearsExperience = yearsExperience;
        this.salary = salary;
        this.department = department;
        this.managerId = managerId != null && managerId != 0 ? managerId : null;
    }

    // =================== Getters ===================
    public int getEmpId() { return empId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public int getAge() { return age; }
    public String getGender() { return gender; }
    public int getYearsExperience() { return yearsExperience; }
    public double getSalary() { return salary; }
    public String getDepartment() { return department; }
    public Integer getManagerId() { return managerId; }

    // =================== Setters ===================
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setAge(int age) { this.age = age; }
    public void setGender(String gender) { this.gender = gender; }
    public void setYearsExperience(int yearsExperience) { this.yearsExperience = yearsExperience; }
    public void setSalary(double salary) { this.salary = salary; }
    public void setDepartment(String department) { this.department = department; }
}
