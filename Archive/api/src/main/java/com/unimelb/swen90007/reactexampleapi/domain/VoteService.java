package com.unimelb.swen90007.reactexampleapi.domain;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

public class VoteService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$");

    private final VoteRepository repository;

    public VoteService(VoteRepository repository) {
        this.repository = repository;
    }

    public Vote submit(NewVoteRequest request) {
        if (request.getEmail() == null) {
            throw new ValidationException("email is required");
        } else if (!isEmailValid(request.getEmail())) {
            throw new ValidationException("email must be a valid email");
        }

        var vote = new Vote();
        vote.setId(UUID.randomUUID().toString());
        vote.setName(request.getName());
        vote.setEmail(request.getEmail());
        vote.setSupporting(request.isSupporting());
        vote.setStatus(Vote.Status.UNVERIFIED);
        vote.setCreated(OffsetDateTime.now());
        return repository.save(vote);
    }

    public Verdict calculateVerdict() {
        var votes = repository.getValid();
        var supporting = votes.stream()
                .filter(Vote::isSupporting)
                .count();

        return new Verdict(
                supporting,
                votes.size() - supporting
        );
    }

    public List<Vote> getAllVotes(long offset, long limit) {
        return repository.getAll(offset, limit);
    }

    public Optional<Vote> getVote(String id) {
        return repository.get(id);
    }

    public Vote updateVote(String id, UpdateVoteRequest updateRequest) throws NotFoundException {
        Vote vote = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Vote not found with id: " + id));

        if (updateRequest.getName() != null) {
            vote.setName(updateRequest.getName());
        }
        if (updateRequest.getEmail() != null) {
            vote.setEmail(updateRequest.getEmail());
        }
        if (updateRequest.getSupporting() != null) {
            vote.setSupporting(updateRequest.getSupporting());
        }
        if (updateRequest.getStatus() != null) {
            vote.setStatus(updateRequest.getStatus());
        }

        vote.setNew(false); // Ensure this is set to false for updates
        return repository.save(vote);
    }


    private boolean isEmailValid(String email) {
        return EMAIL_PATTERN.matcher(email).find();
    }

}
