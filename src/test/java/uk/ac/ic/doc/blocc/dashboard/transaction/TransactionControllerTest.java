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
}