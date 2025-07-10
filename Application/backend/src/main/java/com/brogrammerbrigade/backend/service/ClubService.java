package com.brogrammerbrigade.backend.service;

import com.brogrammerbrigade.backend.datasource.ClubMapper;
import com.brogrammerbrigade.backend.datasource.StudentMapper;
import com.brogrammerbrigade.backend.domain.Club;
import com.brogrammerbrigade.backend.domain.Event;
import com.brogrammerbrigade.backend.domain.Rsvp;
import com.brogrammerbrigade.backend.domain.Student;
import com.brogrammerbrigade.backend.dto.ClubRequest;
import com.brogrammerbrigade.backend.exception.VisibleException;
import jakarta.servlet.http.HttpServletResponse;

import java.math.BigInteger;
import java.util.List;

public class ClubService {
    private static ClubService instance;

    private final ClubMapper clubMapper;
    private ClubService() {
        this.clubMapper = ClubMapper.getInstance();
    }

    // Public method to provide access to the singleton instance
    public static synchronized ClubService getInstance() {
        if (instance == null) {
            instance = new ClubService();
        }
        return instance;
    }
    public List<Club> getAllClubs(){
        return clubMapper.getAllClubs();

    }

    // Public method to fetch a club by the club id
    public Club getClubById(BigInteger id){
        return clubMapper.getClubById(id);
    }

    public List<Club> getAdminClubsForStudent(BigInteger studentId) {
        return clubMapper.getAdminClubsForStudent(new Student(studentId));
    }

    // Method to create a new Club with validation
    public Club createClub(String name, Double balance, BigInteger adminId) {
        // Validate the input parameters
        if (name == null || name.isEmpty()) {
            throw new VisibleException("Club name cannot be null or empty.", HttpServletResponse.SC_BAD_REQUEST);
        }
        if (adminId == null) {
            throw new VisibleException("There must be exactly one admin.", HttpServletResponse.SC_BAD_REQUEST);
        }
        if (balance == null) {
            throw new VisibleException("Balance cannot be null", HttpServletResponse.SC_BAD_REQUEST);
        }

        // Create the Club object
        Club newClub = new Club();
        newClub.setName(name);
        newClub.setBalance(balance);

        // Use the ClubMapper to insert the club into the database
        Club createdClub = clubMapper.insert(newClub);
        Student admin = new Student(adminId);
        clubMapper.addAdmin(createdClub, admin);

        return createdClub;
    }

    public Club updateClub(ClubRequest clubRequest) {
        BigInteger id = clubRequest.getId();
        String clubName = clubRequest.getName();
        Double clubBalance = clubRequest.getBalance();

        if (id == null) {
            throw new VisibleException("Club id cannot be null.", HttpServletResponse.SC_BAD_REQUEST);
        }

        Club updatedClub = new Club(id);
        updatedClub.setName(clubName);
        updatedClub.setBalance(clubBalance);

        return clubMapper.update(updatedClub);
    }

    public void deleteClub(BigInteger clubId) {
        Club club = new Club();
        club.setId(clubId);

        clubMapper.delete(club);
    }
}
