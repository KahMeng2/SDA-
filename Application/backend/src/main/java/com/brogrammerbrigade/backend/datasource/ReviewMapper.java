package com.brogrammerbrigade.backend.datasource;

import com.brogrammerbrigade.backend.domain.FacultyMember;
import com.brogrammerbrigade.backend.domain.FundingApplication.FundingApplicationContext;
import com.brogrammerbrigade.backend.domain.Review;
import com.brogrammerbrigade.backend.domain.ReviewDecision;
import com.brogrammerbrigade.backend.port.postgres.DatabaseManager;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReviewMapper implements Mapper<Review> {
    private static ReviewMapper instance;
    private final DatabaseManager databaseManager;

    private ReviewMapper() {
        this.databaseManager = DatabaseManager.getInstance(); // Gets the singleton instance
    }

    // Public method to provide access to the singleton instance
    public static synchronized ReviewMapper getInstance() {
        if (instance == null) {
            instance = new ReviewMapper();
        }
        return instance;
    }

    // Return all reviews
    public List<Review> getAllReviews() {
        String SQL_GET_ALL_REVIEWS = "SELECT * FROM app.review";
        try {
            ResultSet resultSet = databaseManager.executeQuery(SQL_GET_ALL_REVIEWS, List.of());
            List<Review> reviews = new ArrayList<>();
            while (resultSet.next()) {
                reviews.add(map(resultSet));
            }
            return reviews;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve all reviews: " + e.getMessage(), e);
        }
    }

    public Review getReview(FacultyMember facultyMember, FundingApplicationContext application) {
        String SQL_GET_REVIEW = "SELECT * FROM app.review WHERE faculty_id = ? AND application_id = ?";
        try {
            List<Object> parameters = List.of(facultyMember.getId(), application.getId());
            ResultSet resultSet = databaseManager.executeQuery(SQL_GET_REVIEW, parameters);
            if (resultSet.next()) {
                return map(resultSet);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve review: " + e.getMessage(), e);
        }
    }

    public List<Review> getReviewsForFacultyMember(FacultyMember facultyMember) {
        String SQL_GET_REVIEWS = "SELECT * FROM app.review WHERE faculty_id = ?";
        try {
            List<Object> parameters = List.of(facultyMember.getId());
            ResultSet resultSet = databaseManager.executeQuery(SQL_GET_REVIEWS, parameters);
            List<Review> reviews = new ArrayList<>();
            while (resultSet.next()) {
                reviews.add(map(resultSet));
            }
            return reviews;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve reviews for faculty member: " + e.getMessage(), e);
        }
    }

    public List<Review> getReviewsForFundingApplication(FundingApplicationContext application) {
        String SQL_GET_REVIEWS = "SELECT * FROM app.review WHERE application_id=?";
        try {
            List<Object> parameters = List.of(application.getId());
            ResultSet resultSet = databaseManager.executeQuery(SQL_GET_REVIEWS, parameters);
            List<Review> reviews = new ArrayList<>();
            while (resultSet.next()) {
                reviews.add(map(resultSet));
            }
            return reviews;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve reviews for the application: " + e.getMessage(), e);
        }
    }

    // Helper function to map database output to a Review object
    private Review map(ResultSet resultSet) throws SQLException {
        Review review = new Review();
        review.setFacultyMember(new FacultyMember(BigInteger.valueOf(resultSet.getLong("faculty_id"))));
        review.setFundingApplicationContext(new FundingApplicationContext(BigInteger.valueOf(resultSet.getLong("application_id"))));
        review.setStartDate(resultSet.getTimestamp("review_start_date").toLocalDateTime());
        review.setDecision(ReviewDecision.valueOf(resultSet.getString("decision"))); // This should work with 'Approved' or 'Rejected'
        review.setComments(resultSet.getString("comments"));
        return review;
    }

    // Extra CRUD operations
    public Review insert(Review review) {
        String SQL_INSERT_REVIEW = "INSERT INTO app.review (faculty_id, application_id, review_start_date, decision, comments) VALUES (?, ?, ?, ?::app.decision_enum, ?)";
        try {
            List<Object> parameters = List.of(
                    review.getFacultyMember().getId(),
                    review.getFundingApplicationContext().getId(),
                    review.getStartDate(),
                    review.getDecision().getValue(),
                    review.getComments()
            );
            databaseManager.executeUpdate(SQL_INSERT_REVIEW, parameters, false);

            return review;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert review: " + e.getMessage(), e);
        }
    }

    public Review update(Review review) {
        String SQL_UPDATE_REVIEW = "UPDATE app.review SET review_start_date = ?, decision = ?, comments = ? WHERE faculty_id = ? AND application_id = ?";
        try {
            List<Object> parameters = List.of(
                    review.getStartDate(),
                    review.getDecision().name(),
                    review.getComments(),
                    review.getFacultyMember().getId(),
                    review.getFundingApplicationContext().getId()
            );
            databaseManager.executeUpdate(SQL_UPDATE_REVIEW, parameters, false);

            return review;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update review: " + e.getMessage(), e);
        }
    }

    public void delete(Review review) {
        String SQL_DELETE_REVIEW = "DELETE FROM app.review WHERE faculty_id = ? AND application_id = ?";
        try {
            List<Object> parameters = List.of(
                    review.getFacultyMember().getId(),
                    review.getFundingApplicationContext().getId()
            );
            databaseManager.executeUpdate(SQL_DELETE_REVIEW, parameters, false);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete review: " + e.getMessage(), e);
        }
    }

    public Boolean exists(Review review) {
        // TODO implementation
        return false;
    }
}
