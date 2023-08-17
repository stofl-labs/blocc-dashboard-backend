package uk.ac.ic.doc.blocc.dashboard.approvedtempreading.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

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
  @Transient
  private int approvals;

  public ApprovedTransaction(String txId, int containerNum, List<String> approvingMspIds,
                             TemperatureHumidityReading reading) {
    this.txId = txId;
    this.containerNum = containerNum;
    this.approvingMspIds = approvingMspIds;
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
}
