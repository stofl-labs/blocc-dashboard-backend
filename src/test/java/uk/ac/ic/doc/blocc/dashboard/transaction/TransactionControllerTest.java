package uk.ac.ic.doc.blocc.dashboard.transaction;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import uk.ac.ic.doc.blocc.dashboard.fabric.model.TemperatureHumidityReading;
import uk.ac.ic.doc.blocc.dashboard.transaction.model.ApprovalTransaction;
import uk.ac.ic.doc.blocc.dashboard.transaction.model.ApprovedTempReading;
import uk.ac.ic.doc.blocc.dashboard.transaction.model.SensorChaincodeTransaction;
import uk.ac.ic.doc.blocc.dashboard.transaction.model.Transaction;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private TransactionService transactionService;

  @Test
  public void getApprovedTransactionsIfExist() throws Exception {
    int containerNum = 1;
    List<ApprovedTempReading> mockData = Arrays.asList(
        new ApprovedTempReading(100L, 0.5F, 3, "tx123"),
        new ApprovedTempReading(102L, 20F, 2, "tx456")
    );

    when(transactionService.getApprovedTempReadings(containerNum)).thenReturn(mockData);

    mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/transaction/approvedTempReadings")
            .header("Origin", "http://localhost:3000")
            .param("containerNum", String.valueOf(containerNum)))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(2));
  }

  @Test
  public void getEmptyListIfApprovedTransactionsNotExist() throws Exception {
    int containerNum = 3;
    List<ApprovedTempReading> mockData = new ArrayList<>();

    when(transactionService.getApprovedTempReadings(containerNum)).thenReturn(mockData);

    mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/transaction/approvedTempReadings")
            .header("Origin", "http://localhost:4000")
            .param("containerNum", String.valueOf(containerNum)))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(0));
  }

  @Test
  public void getAllExistingTransactionsForSpecifiedContainer() throws Exception {
    int containerNum = 1;

    // Given
    SensorChaincodeTransaction tx1 = new SensorChaincodeTransaction(
        "tx123",
        1,
        "Container5MSP",
        100L,
        new TemperatureHumidityReading(25, 0.3F, 100L));

    SensorChaincodeTransaction tx2 = new SensorChaincodeTransaction(
        "tx456",
        1,
        "Container5MSP",
        103L,
        new TemperatureHumidityReading(30, 0.2F, 103L));

    SensorChaincodeTransaction tx3 = new SensorChaincodeTransaction(
        "tx1299",
        1,
        "Container5MSP",
        100L,
        new TemperatureHumidityReading(22, 0.3F, 100L));

    ApprovalTransaction approval1 = new ApprovalTransaction(
        "app1", 1, "Container6MSP", 101L, tx1);
    ApprovalTransaction approval2 = new ApprovalTransaction(
        "app2", 1, "Container7MSP", 102L, tx1);

    List<Transaction> transactions = List.of(tx1, tx2, tx3, approval1, approval2);

    when(transactionService.getTransactions(containerNum)).thenReturn(transactions);

    mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/transaction/all")
            .header("Origin", "https://example.com:8080")
            .param("containerNum", String.valueOf(containerNum)))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(5));
  }

  @Test
  public void getAllExistingTransactions() throws Exception {
    // Given
    SensorChaincodeTransaction tx1 = new SensorChaincodeTransaction(
        "tx123",
        1,
        "Container5MSP",
        100L,
        new TemperatureHumidityReading(25, 0.3F, 100L));

    SensorChaincodeTransaction tx2 = new SensorChaincodeTransaction(
        "tx456",
        2,
        "Container5MSP",
        103L,
        new TemperatureHumidityReading(30, 0.2F, 103L));

    SensorChaincodeTransaction tx3 = new SensorChaincodeTransaction(
        "tx1299",
        3,
        "Container5MSP",
        100L,
        new TemperatureHumidityReading(22, 0.3F, 100L));

    ApprovalTransaction approval1 = new ApprovalTransaction(
        "app1", 1, "Container6MSP", 101L, tx1);
    ApprovalTransaction approval2 = new ApprovalTransaction(
        "app2", 2, "Container7MSP", 102L, tx1);

    List<Transaction> transactions = List.of(tx1, tx2, tx3, approval1, approval2);

    when(transactionService.getTransactions()).thenReturn(transactions);

    mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/transaction/all")
            .header("Origin", "https://example.com:8080"))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(5));
  }

  @Test
  public void getApprovedTransactionsSinceSpecificTimestamp() throws Exception {
    int containerNum = 1;
    long sinceTimestamp = 101L; // Example timestamp
    List<ApprovedTempReading> mockData = Arrays.asList(
        new ApprovedTempReading(102L, 0.5F, 3, "tx123"),
        new ApprovedTempReading(103L, 20F, 2, "tx456")
    );

    // Mocking the new method you might have added to your service
    when(transactionService.getApprovedTempReadingsSince(containerNum, sinceTimestamp)).thenReturn(
        mockData);

    mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/transaction/approvedTempReadings")
            .header("Origin", "http://localhost:3000")
            .param("containerNum", String.valueOf(containerNum))
            .param("sinceTimestamp", String.valueOf(sinceTimestamp)))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(2));
  }

  // Inside TransactionControllerTest class

  @Test
  public void getSensorChaincodeTransactionsWithoutParameters() throws Exception {
    List<SensorChaincodeTransaction> mockData = Arrays.asList(
        new SensorChaincodeTransaction("tx123", 1, "Container1MSP", 100L,
            new TemperatureHumidityReading(25, 0.3F, 105L)),
        new SensorChaincodeTransaction("tx456", 2, "Container2MSP", 110L,
            new TemperatureHumidityReading(26, 0.4F, 115L))
    );

    when(transactionService.getSensorChaincodeTransactions(null, null, null, null)).thenReturn(
        mockData);

    mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/transaction/sensorChaincodeTransactions"))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(mockData.size()));
  }

  @Test
  public void getSensorChaincodeTransactionsWithContainerNum() throws Exception {
    int containerNum = 1;
    List<SensorChaincodeTransaction> mockData = List.of(
        new SensorChaincodeTransaction("tx123", 1, "Container1MSP", 100L,
            new TemperatureHumidityReading(25, 0.3F, 105L))
    );

    when(transactionService.getSensorChaincodeTransactions(containerNum, null, null,
        null)).thenReturn(mockData);

    mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/transaction/sensorChaincodeTransactions")
            .param("containerNum", String.valueOf(containerNum)))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(mockData.size()));
  }

  @Test
  public void getSensorChaincodeTransactionsWithContainerNumAndSinceTimestamp() throws Exception {
    int containerNum = 1;
    long sinceTimestamp = 100L;
    List<SensorChaincodeTransaction> mockData = List.of(
        new SensorChaincodeTransaction("tx123", 1, "Container1MSP", 100L,
            new TemperatureHumidityReading(25, 0.3F, 105L))
    );

    when(transactionService.getSensorChaincodeTransactions(containerNum, sinceTimestamp, null,
        null)).thenReturn(mockData);

    mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/transaction/sensorChaincodeTransactions")
            .param("containerNum", String.valueOf(containerNum))
            .param("sinceTimestamp", String.valueOf(sinceTimestamp)))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(mockData.size()));
  }

  @Test
  public void getSensorChaincodeTransactionsWithAllParameters() throws Exception {
    int containerNum = 1;
    long sinceTimestamp = 90L;
    long untilTimestamp = 120L;
    long approvalWindowSeconds = 50L;
    List<SensorChaincodeTransaction> mockData = List.of(
        new SensorChaincodeTransaction("tx123", 1, "Container1MSP", 100L,
            new TemperatureHumidityReading(25, 0.3F, 105L))
    );

    when(transactionService.getSensorChaincodeTransactions(containerNum, sinceTimestamp,
        untilTimestamp, approvalWindowSeconds)).thenReturn(mockData);

    mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/transaction/sensorChaincodeTransactions")
            .param("containerNum", String.valueOf(containerNum))
            .param("sinceTimestamp", String.valueOf(sinceTimestamp))
            .param("untilTimestamp", String.valueOf(untilTimestamp))
            .param("approvalWindowSeconds", String.valueOf(approvalWindowSeconds)))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(mockData.size()));
  }

}