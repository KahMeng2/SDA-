package com.brogrammerbrigade.backend.tests;

import com.brogrammerbrigade.backend.domain.Event;
import com.brogrammerbrigade.backend.dto.EventRequest;
import com.brogrammerbrigade.backend.exception.VisibleException;
import com.brogrammerbrigade.backend.port.postgres.ConnectionProvider;
import com.brogrammerbrigade.backend.service.AuthenticationService;
import com.brogrammerbrigade.backend.service.EventService;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@WebServlet(name = "Test2", urlPatterns = {"/test2/*"})
public class ConcurrentTest2 extends HttpServlet {
    private ObjectMapper objectMapper;
    private EventService eventService = EventService.getInstance();
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
            testConcurrentEventUpdate();
        } else if (pathInfo.equals("/runtests")) {
            //getEventsForClub(req, resp);
        } else {
            throw new VisibleException("Invalid path", HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void testConcurrentEventUpdate() {
        int numThreads = 3;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        BigInteger eventId = BigInteger.valueOf(1); // ID of the existing event

        // First set of updates (should succeed)
        EventRequest successRequest = new EventRequest();
        successRequest.setName("Updated Annual Hackathon");
        successRequest.setDescription("Join us for a 48-hour coding challenge!");
        successRequest.setCost(10.0);
        successRequest.setCapacity(10);
        successRequest.setStartTime(OffsetDateTime.now().plusDays(1));
        successRequest.setEndTime(OffsetDateTime.now().plusDays(1).plusHours(2));

        successRequest.setClubId(BigInteger.valueOf(1));
        successRequest.setVenueId(BigInteger.valueOf(1));
        successRequest.setIsOnline(false);
        successRequest.setIsCancelled(false);

        // Second set of updates (should fail due to lock)
        EventRequest failRequest = new EventRequest();
        failRequest.setName("Another Annual Hackathon");
        failRequest.setDescription("Second update");
        failRequest.setCost(5.0);
        failRequest.setCapacity(20);
        failRequest.setClubId(BigInteger.valueOf(1));
        failRequest.setVenueId(BigInteger.valueOf(1));
        failRequest.setStartTime(OffsetDateTime.now().plusDays(1));
        failRequest.setEndTime(OffsetDateTime.now().plusDays(1).plusHours(2));
        failRequest.setIsOnline(false);
        failRequest.setIsCancelled(false);

        System.out.println("Test 2 starting..");
        for (int i = 0; i < numThreads; i++) {
            final int threadNum = i;
            executor.submit(() -> {
                try {
                    EventRequest request = (threadNum == 0) ? successRequest : failRequest;
                    // Attempt to acquire the lock
                    eventService.getUpdateEventResource(eventId, request, String.valueOf(threadNum));

                    // If lock is acquired, attempt the update
                    Event updatedEvent = eventService.updateEvent(eventId, request, String.valueOf(threadNum));
                    System.out.println("Event has been updated to :" + updatedEvent.getName());

                    if (updatedEvent != null && updatedEvent.getName().equals(request.getName())) {
                        successCount.incrementAndGet();
                        System.out.println("Thread " + threadNum + " successfully updated the event.");
                    } else {
                        failCount.incrementAndGet();
                        System.out.println("Thread " + threadNum + " failed to update the event.");
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
        System.out.println("Concurrent event update test completed.");
        System.out.println("Successful updates: " + successCount.get());
        System.out.println("Failed updates: " + failCount.get());

        // Verify the final state of the event
        Event finalEvent = eventService.getEventById(eventId);
        System.out.println("Final event state: " + finalEvent.getDescription());
    }

}
