package uk.ac.ic.doc.blocc.dashboard.transaction.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TransactionTest {

  private Transaction transaction;
  private final String txId = "tx12345";
  private final int containerNum = 1;
  private final String creator = "creatorName";
  private final long createdTimestamp = System.currentTimeMillis() / 1000L; // Assuming seconds

  @BeforeEach
  public void setUp() {
    transaction = new Transaction(txId, containerNum, creator, createdTimestamp) {
      // This is an anonymous subclass since Transaction is abstract
    };
  }

  @Test
  public void getsTxId() {
    assertEquals(txId, transaction.getTxId());
  }

  @Test
  public void getsContainerNum() {
    assertEquals(containerNum, transaction.getContainerNum());
  }

  @Test
  public void getsCreator() {
    assertEquals(creator, transaction.getCreator());
  }

  @Test
  public void getsKey() {
    CompositeKey key = transaction.getKey();
    assertNotNull(key);
    assertEquals(txId, key.getTxId());
    assertEquals(containerNum, key.getContainerNum());
  }

  @Test
  public void convertsToString() {
    String expectedString = String.format(
        "Transaction (txId=%s, container=%d, creator=%s, time=%s)",
        txId, containerNum, creator, Instant.ofEpochSecond(createdTimestamp));
    assertEquals(expectedString, transaction.toString());
  }
}
