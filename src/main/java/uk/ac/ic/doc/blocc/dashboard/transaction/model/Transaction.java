package uk.ac.ic.doc.blocc.dashboard.transaction.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import java.time.Instant;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Transaction {

  public CompositeKey getKey() {
    return key;
  }

  @EmbeddedId
  protected CompositeKey key;
  protected String creator;
  protected long createdTimestamp;

  protected Transaction() {
  }

  protected Transaction(String txId, int containerNum, String creator, long createdTimestamp) {
    key = new CompositeKey(txId, containerNum);
    this.creator = creator;
    this.createdTimestamp = createdTimestamp;
  }

  public String getTxId() {
    return key.getTxId();
  }

  public int getContainerNum() {
    return key.getContainerNum();
  }

  public String getCreator() {
    return creator;
  }

  public String toString() {
    return String.format("Transaction (txId=%s, container=%d, creator=%s, time=%s)", getTxId(),
        getContainerNum(), creator,
        Instant.ofEpochSecond(createdTimestamp));
  }
}
