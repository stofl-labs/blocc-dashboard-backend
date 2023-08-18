package uk.ac.ic.doc.blocc.dashboard.approvedtransaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.ac.ic.doc.blocc.dashboard.approvedtransaction.model.ApprovedTempReading;
import uk.ac.ic.doc.blocc.dashboard.approvedtransaction.model.ApprovedTransaction;
import uk.ac.ic.doc.blocc.dashboard.fabric.model.TemperatureHumidityReading;

public class ApprovedTransactionServiceTest {

  @InjectMocks
  private ApprovedTransactionService approvedTransactionService;

  @Mock
  private ApprovedTransactionRepository repository;

  @Test
  public void getApprovedTempReadingsTest() {
    MockitoAnnotations.openMocks(this);

    int containerNum = 1;
    List<ApprovedTransaction> mockData = Arrays.asList(
        new ApprovedTransaction(
            "tx123",
            1, List.of("Container5MSP", "Container6MSP"),
            new TemperatureHumidityReading(25, 0.3F, 100L)),

        new ApprovedTransaction(
            "tx1299",
            1, List.of("Container5MSP"),
            new TemperatureHumidityReading(22, 0.3F, 100L))
    );

    when(repository.findAllByContainerNum(containerNum)).thenReturn(mockData);

    List<ApprovedTempReading> result =
        approvedTransactionService.getApprovedTempReadings(containerNum);

    assertEquals(2, result.size());

    // Expected ApprovedTempReadings based on the mock data
    ApprovedTempReading expectedFirstReading = new ApprovedTempReading(100L, 25, 2, "tx123");
    ApprovedTempReading expectedSecondReading = new ApprovedTempReading(100L, 22, 1, "tx1299");

    // Assertions
    assertEquals(expectedFirstReading, result.get(0));
    assertEquals(expectedSecondReading, result.get(1));
  }
}