package com.brogrammerbrigade.backend.datasource;

import com.brogrammerbrigade.backend.domain.DomainObject;
import com.brogrammerbrigade.backend.domain.Rsvp;
import com.brogrammerbrigade.backend.domain.Ticket;

public class DataMapperFactory {
    private static DataMapperFactory instance;

    private DataMapperFactory() {}

    public static synchronized DataMapperFactory getInstance() {
        if (instance == null) {
            instance = new DataMapperFactory();
        }
        return instance;
    }

    @SuppressWarnings("unchecked")
    public <T extends DomainObject> Mapper<T> getMapper(Class<T> cls) {

        if (cls.equals(Rsvp.class)) {
            return (Mapper<T>) RsvpMapper.getInstance();
        }
        if (cls.equals(Ticket.class)) {
            return (Mapper<T>) TicketMapper.getInstance();
        }
        // Add other mappers here...
        throw new IllegalArgumentException("No mapper found for class: " + cls.getName());
    }
}
