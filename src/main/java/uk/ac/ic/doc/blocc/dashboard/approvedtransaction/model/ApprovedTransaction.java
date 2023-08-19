package uk.ac.ic.doc.blocc.dashboard.approvedtransaction.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import uk.ac.ic.doc.blocc.dashboard.fabric.model.TemperatureHumidityReading;

@Entity
@Table
public class ApprovedTransaction {

  @Id
  private String txId;

  private int containerNum;

  @ElementCollection
  @CollectionTable
  private List<String> approvingMspIds = new ArrayList<>();
  private TemperatureHumidityReading reading;

  public ApprovedTransaction(String txId, int containerNum, List<String> approvingMspIds,
                             TemperatureHumidityReading reading) {
    this.txId = txId;
    this.containerNum = containerNum;
    this.approvingMspIds = approvingMspIds;
    this.reading = reading;
  }

  public ApprovedTransaction(String txId, int containerNum,
                             TemperatureHumidityReading reading) {
    this.txId = txId;
    this.containerNum = containerNum;
    this.reading = reading;
  }

  public ApprovedTransaction() {
  }


  public String getTxId() {
    return txId;
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
    return containerNum;
  }

  public void approve(String approvingMspId) {
    if (approvingMspIds.contains(approvingMspId)) {
      throw new IllegalArgumentException(
          String.format("%s has already approved this transaction", approvingMspId));
    }
    approvingMspIds.add(approvingMspId);
  }
}
