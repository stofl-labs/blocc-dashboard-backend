package uk.ac.ic.doc.blocc.dashboard.transaction.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.ic.doc.blocc.dashboard.fabric.model.TemperatureHumidityReading;

public class SensorChaincodeTransactionTest {

  private SensorChaincodeTransaction sensorChaincodeTransaction;
  private final String txId = "tx12345";
  private final int containerNum = 1;
  private final String creator = "creatorName";
  private final long createdTimestamp = System.currentTimeMillis() / 1000L; // Assuming seconds

  @BeforeEach
  public void setUp() {
    TemperatureHumidityReading reading = new TemperatureHumidityReading(25, 0.5F, createdTimestamp);
    sensorChaincodeTransaction = new SensorChaincodeTransaction(txId, containerNum, creator,
        createdTimestamp, reading);
  }

  @Test
  public void getsReading() {
    TemperatureHumidityReading retrievedReading = sensorChaincodeTransaction.getReading();
    assertEquals(25, retrievedReading.getTemperature());
    assertEquals(0.5F, retrievedReading.getRelativeHumidity());
    assertEquals(createdTimestamp, retrievedReading.getTimestamp());
  }

  @Test
  public void convertsToString() {
    String expectedString =
        "Sensor Chaincode Transaction (txId=" + txId + ", container=" + containerNum + ", creator="
            + creator + ", time=" + Instant.ofEpochSecond(createdTimestamp) + ")";
    assertEquals(expectedString, sensorChaincodeTransaction.toString());
  }
}
