package uk.ac.ic.doc.blocc.dashboard.transaction.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

public class MinimalSensorChaincodeTransactionSerializer extends
    JsonSerializer<SensorChaincodeTransaction> {

  @Override
  public void serialize(SensorChaincodeTransaction transaction, JsonGenerator gen,
      SerializerProvider serializers) throws IOException {
    gen.writeStartObject();
    gen.writeStringField("txId", transaction.getTxId());
    gen.writeStringField("creator", transaction.getCreator());
    gen.writeNumberField("createdTimestamp", transaction.getCreatedTimestamp());
    gen.writeObjectField("reading", transaction.getReading());
    gen.writeEndObject();
  }
}
