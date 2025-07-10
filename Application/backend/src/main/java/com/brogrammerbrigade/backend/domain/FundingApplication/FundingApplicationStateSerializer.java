package com.brogrammerbrigade.backend.domain.FundingApplication;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class FundingApplicationStateSerializer extends StdSerializer<FundingApplicationState> {

    public FundingApplicationStateSerializer() {
        this(null);
    }

    public FundingApplicationStateSerializer(Class<FundingApplicationState> t) {
        super(t);
    }

    @Override
    public void serialize(FundingApplicationState state, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(state.getClass().getSimpleName());
    }
}