package com.brogrammerbrigade.backend.service;

import com.brogrammerbrigade.backend.datasource.UserMapper;
import com.brogrammerbrigade.backend.domain.User;
import com.brogrammerbrigade.backend.domain.Student;
import com.brogrammerbrigade.backend.domain.FacultyMember;
import com.brogrammerbrigade.backend.datasource.StudentMapper;
import com.brogrammerbrigade.backend.datasource.FacultyMemberMapper;
import com.brogrammerbrigade.backend.exception.VisibleException;
import com.brogrammerbrigade.backend.security.PasswordEncoder;
import jakarta.servlet.http.HttpServletResponse;

import java.math.BigInteger;

public class UserService {
    private static UserService instance;
    private final StudentMapper studentMapper;
    private final FacultyMemberMapper facultyMemberMapper;

    private UserService() {
        this.studentMapper = StudentMapper.getInstance();
        this.facultyMemberMapper = FacultyMemberMapper.getInstance();
    }

    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    public User createUser(User user, String department) {
        String hashedPassword = PasswordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        if (user instanceof Student) {
            studentMapper.insert(user);
        } else if (user instanceof FacultyMember facultyMember) {
            facultyMember.setDepartment(department);
            System.out.println("This is getting called " + department);
            facultyMemberMapper.insert(user);
        } else {
            throw new VisibleException("Unsupported user type", HttpServletResponse.SC_BAD_REQUEST);
        }
        return user;
    }

    public boolean userExists(String username, String email) {
        return studentMapper.userExists(username, email) || facultyMemberMapper.userExists(username, email);
    }
        public String getUserRole(BigInteger userId) {
        User user = studentMapper.getUser(new Student(userId));
        if (user == null) {
            user = facultyMemberMapper.getUser(new FacultyMember(userId));
        }
        return user != null ? user.getRole() : null;
    }
    public User getUserByUsername(String username) {
        // First, try to find a student with the given username
        Student student = studentMapper.getUserByUsername(username);
        if (student != null) {
            return student;
        }

        // If no student is found, try to find a faculty member
        FacultyMember facultyMember = facultyMemberMapper.getUserByUsername(username);
        if (facultyMember != null) {
            return facultyMember;
        }

        // If no user is found, return null
        return null;
    }
}