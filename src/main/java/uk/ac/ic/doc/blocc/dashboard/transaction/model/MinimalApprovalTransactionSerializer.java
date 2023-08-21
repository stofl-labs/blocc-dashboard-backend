package uk.ac.ic.doc.blocc.dashboard.transaction.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

public class MinimalApprovalTransactionSerializer extends JsonSerializer<ApprovalTransaction> {

  @Override
  public void serialize(ApprovalTransaction value, JsonGenerator gen,
      SerializerProvider serializers) throws IOException {
    gen.writeStartObject();
    gen.writeStringField("txId", value.getTxId());
    gen.writeStringField("creator", value.getCreator());
    gen.writeNumberField("createdTimestamp", value.getCreatedTimestamp());
    gen.writeEndObject();
  }
}
