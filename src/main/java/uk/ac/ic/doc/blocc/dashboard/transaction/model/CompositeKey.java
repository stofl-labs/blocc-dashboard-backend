package uk.ac.ic.doc.blocc.dashboard.transaction.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CompositeKey implements Serializable {

  private String txId;
  private int containerNum;

  public CompositeKey() {
  }

  public CompositeKey(String txId, int containerNum) {
    this.txId = txId;
    this.containerNum = containerNum;
  }

  public String getTxId() {
    return txId;
  }

  public void setTxId(String txId) {
    this.txId = txId;
  }

  public int getContainerNum() {
    return containerNum;
  }

  public void setContainerNum(int containerNum) {
    this.containerNum = containerNum;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CompositeKey that = (CompositeKey) o;
    return containerNum == that.containerNum
        && Objects.equals(txId, that.txId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(txId, containerNum);
  }
}