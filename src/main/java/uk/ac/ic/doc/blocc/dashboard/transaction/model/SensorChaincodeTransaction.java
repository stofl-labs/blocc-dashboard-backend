package uk.ac.ic.doc.blocc.dashboard.transaction.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import uk.ac.ic.doc.blocc.dashboard.fabric.model.TemperatureHumidityReading;

@Entity
public class SensorChaincodeTransaction extends Transaction {

  @OneToMany(mappedBy = "sensorChaincodeTransaction")
  @JsonSerialize(contentUsing = MinimalApprovalTransactionSerializer.class)
  private List<ApprovalTransaction> approvals = new ArrayList<>();
  private TemperatureHumidityReading reading;

  protected SensorChaincodeTransaction() {
    super();
  }

  public SensorChaincodeTransaction(String txId, int containerNum, String creator, long timestamp,
      TemperatureHumidityReading reading) {
    super(txId, containerNum, creator, timestamp, "sensor_chaincode");
    key = new CompositeKey(txId, containerNum);
    this.reading = reading;
  }

  public SensorChaincodeTransaction(String txId, int containerNum, String creator, long timestamp) {
    super(txId, containerNum, creator, timestamp, "sensor_chaincode");
  }

  public TemperatureHumidityReading getReading() {
    return reading;
  }

  @Override
  public String toString() {
    return "Sensor Chaincode " + super.toString();
  }

  @JsonIgnore
  public int getApprovalCount() {
    return approvals.size();
  }

  @JsonIgnore
  public List<ApprovalTransaction> getApprovals() {
    return approvals;
  }

  public void setApprovals(List<ApprovalTransaction> approvals) {
    this.approvals = approvals;
  }
}
