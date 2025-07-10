package com.brogrammerbrigade.backend.tests;

import com.brogrammerbrigade.backend.domain.Event;
import com.brogrammerbrigade.backend.domain.Rsvp;
import com.brogrammerbrigade.backend.dto.EventRequest;
import com.brogrammerbrigade.backend.dto.RsvpRequest;
import com.brogrammerbrigade.backend.dto.TicketRequest;
import com.brogrammerbrigade.backend.exception.VisibleException;
import com.brogrammerbrigade.backend.port.postgres.ConnectionProvider;
import com.brogrammerbrigade.backend.service.AuthenticationService;
import com.brogrammerbrigade.backend.service.EventService;
import com.brogrammerbrigade.backend.service.RsvpService;
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
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@WebServlet(name = "Test1", urlPatterns = {"/test1/*"})
public class ConcurrentTest1 extends HttpServlet {
    private ObjectMapper objectMapper;
    private RsvpService rsvpService = RsvpService.getInstance();

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
            testConcurrentCreateRSVP();
        } else if (pathInfo.equals("/test2")) {
            // TODO: another endpoint to test updateRSVP
            //getEventsForClub(req, resp);
        } else {
            throw new VisibleException("Invalid path", HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void testConcurrentCreateRSVP() {
        int numThreads = 3;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        BigInteger eventId = BigInteger.valueOf(2); // ID of the existing request
        ArrayList<TicketRequest> tickets = new ArrayList<>(); // Stores ticket requests

        // Ticket Request 1
        TicketRequest t1 = new TicketRequest(BigInteger.valueOf(31),"no cheese");

        // Ticket Request 2
        TicketRequest t2 = new TicketRequest(BigInteger.valueOf(30),"More cheese");

        tickets.add(t1);
        tickets.add(t2);

        // RSVP Request
        RsvpRequest r1 = new RsvpRequest();
        r1.setEventId(eventId);
        r1.setRsvpStudentId(BigInteger.valueOf(31));
        r1.setTicketRequests(tickets);


        System.out.println("Test 1 starting..");
        for (int i = 0; i < numThreads; i++) {
            final int threadNum = i;
            executor.submit(() -> {
                try {
                    // Attempts to create RSVP
                    Rsvp newRSVP = rsvpService.createRsvp(r1);

                    if (newRSVP != null && newRSVP.getRsvpStudentId().equals(r1.getRsvpStudentId())) {
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
        System.out.println("Concurrent RSVP create test completed.");
        System.out.println("Successful creations: " + successCount.get());
        System.out.println("Failed creations: " + failCount.get());

        // Verify the final state of the request
        Rsvp finalRSVP = rsvpService.getRsvp(r1.getRsvpStudentId(),r1.getEventId());
        System.out.println("Final RSVP state: " + finalRSVP.getRsvpStudentId());
    }

}
