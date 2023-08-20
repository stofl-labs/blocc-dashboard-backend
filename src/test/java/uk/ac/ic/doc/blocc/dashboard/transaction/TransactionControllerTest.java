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
import uk.ac.ic.doc.blocc.dashboard.transaction.model.ApprovedTempReading;

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
            .param("containerNum", String.valueOf(containerNum)))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(0));
  }

}