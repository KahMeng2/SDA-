package com.brogrammerbrigade.backend.tests;

import com.brogrammerbrigade.backend.domain.FundingApplication.FundingApplicationContext;
import com.brogrammerbrigade.backend.domain.Review;
import com.brogrammerbrigade.backend.domain.ReviewDecision;
import com.brogrammerbrigade.backend.exception.VisibleException;
import com.brogrammerbrigade.backend.port.postgres.ConnectionProvider;

import com.brogrammerbrigade.backend.service.FundingApplicationService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@WebServlet(name = "Test5", urlPatterns = {"/test5/*"})
public class ConcurrentTest5 extends HttpServlet {
    private ObjectMapper objectMapper;
    private FundingApplicationService fundingApplicationService = FundingApplicationService.getInstance();
    @Override
    public void init() throws ServletException {
        // Not sure if it should be singleton
        ConnectionProvider connectionProvider = new ConnectionProvider();
        connectionProvider.init();


//        authService = new AuthenticationService(userService, TokenServiceFactory.createTokenService());
        // Initialise object mapper for json
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);  // Ensures dates are serialized as ISO-8601 strings
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        super.init();
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            testConcurrentEditFundApp();
        } else if (pathInfo.equals("/runtests")) {
            //getEventsForClub(req, resp);
        } else {
            throw new VisibleException("Invalid path", HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * Simulates multiple admins trying to review the same funding application.
     */
    private void testConcurrentEditFundApp() {
        int numThreads = 3;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);


        BigInteger applicationId = BigInteger.valueOf(35); // ID of the Funding app


        System.out.println("Test 5 starting..");
        for (int i = 0; i < numThreads; i++) {
            final int threadNum = i;
            executor.submit(() -> {
                try {
                    // Attempt to acquire the lock for faculty member
                    BigInteger facultyId = BigInteger.valueOf(threadNum); // ID of the faculty member
                    // We use the thread number as the owner
                    fundingApplicationService.startEditFundingApplication(applicationId,facultyId.toString());

                    // Creates the review data for faculty member
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("description", "New update");
                    updates.put("amount",3000);

                    // If lock is acquired, attempt the update
                    FundingApplicationContext fundApp = FundingApplicationService.getInstance().commitEditFundingApplication(applicationId,updates,facultyId.toString());

                    if (fundApp != null && fundApp.getDescription().equals("New update")) {
                        successCount.incrementAndGet();
                        System.out.println("Thread " + threadNum + " successfully updated Funding App.");
                    } else {
                        failCount.incrementAndGet();
                        System.out.println("Thread " + threadNum + " failed to update Funding App.");
                    }
                } catch (Exception e) {
                    failCount.incrementAndGet();
                    System.out.println("Thread " + threadNum + " failed: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            latch.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executor.shutdown();
        System.out.println("Concurrent Funding App edit test completed.");
        System.out.println("Successful edit: " + successCount.get());
        System.out.println("Failed edit: " + failCount.get());
    }

}

