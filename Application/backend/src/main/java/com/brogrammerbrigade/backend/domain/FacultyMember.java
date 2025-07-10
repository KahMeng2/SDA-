package com.brogrammerbrigade.backend.domain;

import com.brogrammerbrigade.backend.datasource.FacultyMemberMapper;
import com.brogrammerbrigade.backend.datasource.ReviewMapper;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigInteger;
import java.util.List;

public class FacultyMember extends User{
    @JsonIgnoreProperties("facultyMember")
    private String department;
    private List<Review> reviews;

    protected void load() {
        FacultyMemberMapper facultyMemberMapper = FacultyMemberMapper.getInstance();
        FacultyMember facultyMemberData = facultyMemberMapper.getUser(this);

        if (department == null) {
            this.department = facultyMemberData.getDepartment();
        }
        super.loadParent(facultyMemberData);
    }

    public FacultyMember() {
        setRole("FACULTY");
    }

    public FacultyMember(BigInteger id) {
        setId(id);
        setRole("FACULTY");
    }

    public String getDepartment() {
        if (department == null) {
            load();
        }
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public List<Review> getReviews() {
        if (reviews == null) {
            ReviewMapper reviewMapper = ReviewMapper.getInstance();
            reviews = reviewMapper.getReviewsForFacultyMember(this);
        }
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}