package com.brogrammerbrigade.backend.tests;

import com.brogrammerbrigade.backend.domain.Event;
import com.brogrammerbrigade.backend.domain.FundingApplication.FundingApplicationContext;
import com.brogrammerbrigade.backend.domain.Rsvp;
import com.brogrammerbrigade.backend.dto.EventRequest;
import com.brogrammerbrigade.backend.dto.RsvpRequest;
import com.brogrammerbrigade.backend.dto.TicketRequest;
import com.brogrammerbrigade.backend.exception.VisibleException;
import com.brogrammerbrigade.backend.port.postgres.ConnectionProvider;
import com.brogrammerbrigade.backend.service.AuthenticationService;
import com.brogrammerbrigade.backend.service.EventService;
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
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@WebServlet(name = "Test3", urlPatterns = {"/test3/*"})
public class ConcurrentTest3 extends HttpServlet {
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
            testConcurrentFundingAppCreation();
        } else if (pathInfo.equals("/runtests")) {
            //getEventsForClub(req, resp);
        } else {
            throw new VisibleException("Invalid path", HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void testConcurrentFundingAppCreation() {
        int numThreads = 3;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // Funding App Request
        BigInteger clubId = BigInteger.valueOf(31);
        String description = "Funding for coffee event";
        Double amount = 100.0;
        Integer semester = 2;
        Year year = Year.of(2027);
        System.out.println("Test 3 starting..");


        for (int i = 0; i < numThreads; i++) {
            final int threadNum = i;
            executor.submit(() -> {
                try {
                    // Attempts to create RSVP
                    FundingApplicationContext newFundApp = fundingApplicationService.createFundingApplication(
                            clubId, description, amount, semester, year
                    );

                    if (newFundApp != null && newFundApp.getDescription().equals(description)) {
                        successCount.incrementAndGet();
                        System.out.println("Thread " + threadNum + " successfully updated the RSVP.");
                    } else {
                        failCount.incrementAndGet();
                        System.out.println("Thread " + threadNum + " failed to update the RSVP.");
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
        System.out.println("Concurrent Funding app create test completed.");
        System.out.println("Successful creations: " + successCount.get());
        System.out.println("Failed creations: " + failCount.get());
    }

}
