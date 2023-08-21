package uk.ac.ic.doc.blocc.dashboard.transaction.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ApprovalTransactionTest {

  private ApprovalTransaction approvalTransaction;
  private final String txId = "tx12345";
  private final int containerNum = 1;
  private final String creator = "creatorName";
  private final long createdTimestamp = System.currentTimeMillis() / 1000L; // Assuming seconds

  @BeforeEach
  public void setUp() {
    SensorChaincodeTransaction mockSensorTransaction = mock(SensorChaincodeTransaction.class);
    approvalTransaction = new ApprovalTransaction(txId, containerNum, creator, createdTimestamp,
        mockSensorTransaction);
  }

  @Test
  public void convertsToString() {
    String expectedString =
        "Approval Transaction (txId=" + txId + ", container=" + containerNum + ", creator="
            + creator + ", time=" + Instant.ofEpochSecond(createdTimestamp) + ")";
    assertEquals(expectedString, approvalTransaction.toString());
  }
}
