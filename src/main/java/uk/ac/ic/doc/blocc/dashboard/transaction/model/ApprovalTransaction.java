package uk.ac.ic.doc.blocc.dashboard.transaction.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class ApprovalTransaction extends Transaction {

  @ManyToOne
  @JsonSerialize(using = MinimalSensorChaincodeTransactionSerializer.class)
  @JsonProperty("approvedTransaction")
  private SensorChaincodeTransaction sensorChaincodeTransaction;

  public ApprovalTransaction(String txId, int containerNum, String creator, long timestamp,
      SensorChaincodeTransaction sensorChaincodeTransaction) {
    super(txId, containerNum, creator, timestamp, "bscc");
    this.sensorChaincodeTransaction = sensorChaincodeTransaction;
  }

  protected ApprovalTransaction() {

  }

  @Override
  public String toString() {
    return "Approval " + super.toString();
  }
}
