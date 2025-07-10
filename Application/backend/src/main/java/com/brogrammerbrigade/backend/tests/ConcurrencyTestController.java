package com.brogrammerbrigade.backend.tests;

import com.brogrammerbrigade.backend.port.postgres.ConnectionProvider;
import com.brogrammerbrigade.backend.service.EventService;
import com.brogrammerbrigade.backend.dto.EventRequest;
import com.brogrammerbrigade.backend.domain.Event;
import com.brogrammerbrigade.backend.util.JsonResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.math.BigInteger;
import java.time.OffsetDateTime;

@WebServlet(name = "Test", urlPatterns = {"/tests/*"})
public class ConcurrencyTestController extends HttpServlet {
    private EventService eventService;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        // Not sure if it should be singleton
        ConnectionProvider connectionProvider = new ConnectionProvider();
        connectionProvider.init();
        // load the driver
        eventService = EventService.getInstance();

        // Initialise object mapper for json
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);  // Ensures dates are serialized as ISO-8601 strings

        super.init();
    }

    public ConcurrencyTestController() {
        this.eventService = EventService.getInstance();
    }

    public void runConcurrencyTests() {
        testConcurrentEventCreation();
        testConcurrentEventUpdate();
        // Add more test methods as needed
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        testConcurrentEventCreation();
        JsonResponseUtil.sendJsonResponse(resp, null, HttpServletResponse.SC_OK);
    }

    private void testConcurrentEventCreation() {
        int numThreads = 3;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    EventRequest request = createSampleEventRequest();
                    Event event = eventService.createEvent(request);
                    if (event != null && event.getId() != null) {
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    System.out.println("Event creation failed: " + e.getMessage());
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
        System.out.println("Concurrent event creation test completed. Successful creations: " + successCount.get() + " out of " + numThreads);
    }

    private void testConcurrentEventUpdate() {
        // First, create an event to update
        EventRequest createRequest = createSampleEventRequest();
        Event event = eventService.createEvent(createRequest);
        BigInteger eventId = event.getId();

        int numThreads = 3;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        String username = "Dave";

        for (int i = 0; i < numThreads; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    EventRequest updateRequest = createSampleEventRequest();
                    updateRequest.setName("Updated Event " + index);
                    // One get the resource

                    eventService.getUpdateEventResource(eventId,updateRequest,username);
                    Event updatedEvent = eventService.updateEvent(eventId, updateRequest, username);
                    if (updatedEvent != null && updatedEvent.getName().equals("Updated Event " + index)) {
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    System.out.println("Event update failed: " + e.getMessage());
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
        System.out.println("Concurrent event update test completed. Successful updates: " + successCount.get() + " out of " + numThreads);
    }

    private EventRequest createSampleEventRequest() {
        EventRequest request = new EventRequest();
        request.setName("Test Event");
        // Change the ids to whatever ids exist in your database
        request.setDescription("Test Description");
        request.setClubId(BigInteger.valueOf(32));  // Assume club with ID 1 exists
        request.setVenueId(BigInteger.valueOf(31));  // Assume venue with ID 1 exists
        request.setCost(10.0);
        request.setCapacity(100);
        request.setStartTime(OffsetDateTime.now().plusDays(1));
        request.setEndTime(OffsetDateTime.now().plusDays(1).plusHours(2));
        request.setIsOnline(true);
        request.setIsCancelled(false);
        return request;
    }
}