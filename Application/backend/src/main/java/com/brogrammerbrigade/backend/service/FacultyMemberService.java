package com.brogrammerbrigade.backend.service;

import com.brogrammerbrigade.backend.domain.FacultyMember;
import com.brogrammerbrigade.backend.datasource.FacultyMemberMapper;
import com.brogrammerbrigade.backend.domain.User;

import java.math.BigInteger;

public class FacultyMemberService {
    private static FacultyMemberService instance;
    private final FacultyMemberMapper facultyMemberMapper;

    private FacultyMemberService() {
        this.facultyMemberMapper = FacultyMemberMapper.getInstance();
    }

    public static synchronized FacultyMemberService getInstance() {
        if (instance == null) {
            instance = new FacultyMemberService();
        }
        return instance;
    }

    public FacultyMember getFacultyMemberById(BigInteger id) {
        return facultyMemberMapper.getUser(new FacultyMember(id));
    }

    // Add other faculty member-specific methods here
}