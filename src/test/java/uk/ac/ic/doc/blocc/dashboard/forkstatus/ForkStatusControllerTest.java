package uk.ac.ic.doc.blocc.dashboard.forkstatus;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import uk.ac.ic.doc.blocc.dashboard.forkstatus.model.ForkStatus;

@WebMvcTest(ForkStatusController.class)
@ActiveProfiles("fork-status-controller-test")
public class ForkStatusControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ForkStatusService service;

  @Test
  public void returnsTrueIfForkDetected() throws Exception {
    int containerNum = 5;
    when(service.getForkStatus(containerNum)).thenReturn(ForkStatus.FORKED);

    mockMvc.perform(get("/api/v1/forkStatus")
            .header("Origin", "http://localhost:3000")
            .param("containerNum", String.valueOf(containerNum)))
        .andExpect(status().isOk())
        .andExpect(content().json("{\"status\":\"FORKED\"}"));
  }

  @Test
  public void returnsFalseIfForkNotDetected() throws Exception {
    int containerNum = 5;
    when(service.getForkStatus(containerNum)).thenReturn(ForkStatus.NORMAL);

    mockMvc.perform(get("/api/v1/forkStatus")
            .header("Origin", "http://localhost:4000")
            .param("containerNum", String.valueOf(containerNum)))
        .andExpect(status().isOk())
        .andExpect(content().json("{\"status\":\"NORMAL\"}"));
  }

  @Test
  public void returnsNotAvailableWhenChannelIsNotAvailable() throws Exception {
    int containerNum = 5;
    when(service.getForkStatus(containerNum)).thenReturn(ForkStatus.NOT_AVAILABLE);

    mockMvc.perform(get("/api/v1/forkStatus")
            .header("Origin", "https://example.com:8000")
            .param("containerNum", String.valueOf(containerNum)))
        .andExpect(status().isOk())
        .andExpect(content().json("{\"status\":\"NOT_AVAILABLE\"}"));
  }
}
