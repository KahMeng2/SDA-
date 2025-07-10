package com.brogrammerbrigade.backend.domain;
import com.brogrammerbrigade.backend.datasource.UserMapper;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public abstract class User extends DomainObject {
    @JsonProperty("id")
    private BigInteger id;

    @JsonProperty("email")
    private String email;

    @JsonProperty("username")
    private String username;

    @JsonProperty("password")
    private String password;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("middleName")
    private String middleName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("dob")
    private Date dob;

    @JsonProperty("role")
    private String role;

    protected User() {
    }

    abstract void load();

    protected void loadParent(User user) {

        if (email == null) {
            email = user.getEmail();
        }

        if (username == null) {
            username = user.getUsername();
        }

        if (password == null) {
            password = user.getPassword();
        }

        if (firstName == null) {
            firstName = user.getFirstName();
        }

        if (middleName == null) {
            middleName = user.getMiddleName();
        }

        if (lastName == null) {
            lastName = user.getLastName();
        }

        if (dob == null) {
            dob = user.getDob();
        }

        if (role == null) {
            role = user.getRole();
        }
    }

    // Constructors, getters, and setters

    @JsonProperty("id")
    public BigInteger getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        // null values mess with lazy load
        if (middleName == null) {
            this.middleName = "";
        } else {
            this.middleName = middleName;
        }
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<String> getAuthorities() {
        List<String> authorities = new ArrayList<>();
        authorities.add("ROLE_" + getRole().toUpperCase());
        return authorities;
    }
}