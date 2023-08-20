package uk.ac.ic.doc.blocc.dashboard.transaction.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import uk.ac.ic.doc.blocc.dashboard.fabric.model.TemperatureHumidityReading;

@Entity
@Table
public class ApprovedTransaction {

  @EmbeddedId
  private CompositeKey key;

  @ElementCollection
  @CollectionTable
  private List<String> approvingMspIds = new ArrayList<>();
  private TemperatureHumidityReading reading;

  public ApprovedTransaction(String txId, int containerNum, List<String> approvingMspIds,
      TemperatureHumidityReading reading) {
    key = new CompositeKey(txId, containerNum);
    this.approvingMspIds = approvingMspIds;
    this.reading = reading;
  }

  public ApprovedTransaction(String txId, int containerNum,
      TemperatureHumidityReading reading) {
    key = new CompositeKey(txId, containerNum);
    this.reading = reading;
  }

  public ApprovedTransaction() {
  }


  public String getTxId() {
    return key.getTxId();
  }

  public List<String> getApprovingMspIds() {
    return approvingMspIds;
  }

  public TemperatureHumidityReading getReading() {
    return reading;
  }

  public int getApprovals() {
    return approvingMspIds.size();
  }

  public int getContainerNum() {
    return key.getContainerNum();
  }

  public void approve(String approvingMspId) {
    if (approvingMspIds.contains(approvingMspId)) {
      throw new IllegalArgumentException(
          String.format("%s has already approved this transaction", approvingMspId));
    }
    approvingMspIds.add(approvingMspId);
  }
}
