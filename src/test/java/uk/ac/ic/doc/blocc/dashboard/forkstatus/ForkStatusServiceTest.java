package uk.ac.ic.doc.blocc.dashboard.forkstatus;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.hyperledger.fabric.client.Contract;
import org.hyperledger.fabric.client.GatewayException;
import org.hyperledger.fabric.client.Network;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.ac.ic.doc.blocc.dashboard.fabric.BloccConnections;

public class ForkStatusServiceTest {

  @Mock
  private BloccConnections connections;

  @Mock
  private Network channel;

  @Mock
  private Contract bscc;

  @InjectMocks
  private ForkStatusService service;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void returnsTrueIfForkDetected() throws GatewayException {
    int containerNum = 5;
    when(connections.getChannel(containerNum)).thenReturn(channel);
    when(channel.getContract("bscc")).thenReturn(bscc);
    when(bscc.evaluateTransaction("CheckForkStatus", "channel5"))
        .thenReturn("true".getBytes());

    boolean result = service.getForkStatus(containerNum);

    assertTrue(result);
  }

  @Test
  public void returnsFalseIfForkNotDetected() throws GatewayException {
    int containerNum = 5;
    when(connections.getChannel(containerNum)).thenReturn(channel);
    when(channel.getContract("bscc")).thenReturn(bscc);
    when(bscc.evaluateTransaction("CheckForkStatus", "channel5"))
        .thenReturn("false".getBytes());

    boolean result = service.getForkStatus(containerNum);

    assertFalse(result);
  }
}
