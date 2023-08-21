package uk.ac.ic.doc.blocc.dashboard.transaction.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class ApprovalTransaction extends Transaction {

  @ManyToOne
  private SensorChaincodeTransaction sensorChaincodeTransaction;

  public ApprovalTransaction(String txId, int containerNum, String creator, long timestamp,
      SensorChaincodeTransaction sensorChaincodeTransaction) {
    super(txId, containerNum, creator, timestamp);
    this.sensorChaincodeTransaction = sensorChaincodeTransaction;
  }

  protected ApprovalTransaction() {

  }

  public SensorChaincodeTransaction getApprovedTransaction() {
    return sensorChaincodeTransaction;
  }

  @Override
  public String toString() {
    return "Approval " + super.toString();
  }
}
