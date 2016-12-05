package asee.giiis.unex.es.mysporttraining.Objects;


public class User {

    private Integer age;
    private String email;
    private String firstName;
    private Integer height;
    private String lastName;
    private String password;
    private String physicalCondition;
    private Integer score;
    private String sex;
    private String username;
    private Integer weight;

    public User() {

    }

    public User(Integer age, String email, String firstName, Integer height, String lastName,
                String password, Integer score, String username, Integer weight) {
        this.age = age;
        this.email = email;
        this.firstName = firstName;
        this.height = height;
        this.lastName = lastName;
        this.password = password;
        this.score = score;
        this.username = username;
        this.weight = weight;
    }

    public User(Integer age, String email, String firstName, Integer height, String lastName,
                String password, String physicalCondition, Integer score, String sex, String username,
                Integer weight) {
        this.age = age;
        this.email = email;
        this.firstName = firstName;
        this.height = height;
        this.lastName = lastName;
        this.password = password;
        this.physicalCondition = physicalCondition;
        this.score = score;
        this.sex = sex;
        this.username = username;
        this.weight = weight;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhysicalCondition() {
        return physicalCondition;
    }

    public void setPhysicalCondition(String physicalCondition) {
        this.physicalCondition = physicalCondition;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }



}
