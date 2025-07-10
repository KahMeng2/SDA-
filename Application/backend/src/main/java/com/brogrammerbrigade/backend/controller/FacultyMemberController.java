package com.brogrammerbrigade.backend.controller;

import com.brogrammerbrigade.backend.domain.FacultyMember;
import com.brogrammerbrigade.backend.exception.VisibleException;
import com.brogrammerbrigade.backend.service.AuthenticationService;
import com.brogrammerbrigade.backend.service.UserService;
import com.brogrammerbrigade.backend.service.FacultyMemberService;
import com.brogrammerbrigade.backend.util.AuthUtil;
import com.brogrammerbrigade.backend.util.JsonResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigInteger;

@WebServlet(name = "FacultyMember", urlPatterns = "/faculty/*")
public class  FacultyMemberController extends HttpServlet {
    private final UserService userService;
    private final FacultyMemberService facultyMemberService;
    private final ObjectMapper objectMapper;
    private final AuthenticationService authService;

    public FacultyMemberController() {
        this.userService = UserService.getInstance();
        this.facultyMemberService = FacultyMemberService.getInstance();
        authService = AuthenticationService.getInstance();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo != null && pathInfo.startsWith("/")) {
            String[] parts = pathInfo.split("/");
            if (parts.length == 2) {
                getFacultyMember(req, resp);
            } else {
                throw new VisibleException("Not Found", HttpServletResponse.SC_NOT_FOUND);
            }
        } else {
            throw new VisibleException( "Not Found", HttpServletResponse.SC_NOT_FOUND);
        }
    }



    private void getFacultyMember(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String token = AuthUtil.extractToken(req);
        assert token != null;
        if (token.isBlank()) {
            throw new VisibleException("No token", HttpServletResponse.SC_FORBIDDEN);
        }

        FacultyMember facultyMember = facultyMemberService.getFacultyMemberById(AuthenticationService.getInstance().getIdFromToken(token));
        if (facultyMember != null) {
            JsonResponseUtil.sendJsonResponse(resp, facultyMember, HttpServletResponse.SC_OK);
        } else {
            throw new VisibleException("Faculty member not found", HttpServletResponse.SC_NOT_FOUND);
        }
    }

}