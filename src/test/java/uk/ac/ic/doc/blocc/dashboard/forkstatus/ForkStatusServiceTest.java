package uk.ac.ic.doc.blocc.dashboard.forkstatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.hyperledger.fabric.client.Contract;
import org.hyperledger.fabric.client.GatewayException;
import org.hyperledger.fabric.client.Network;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.ac.ic.doc.blocc.dashboard.fabric.BloccConnections;
import uk.ac.ic.doc.blocc.dashboard.forkstatus.model.ForkStatus;

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

    ForkStatus result = service.getForkStatus(containerNum);

    assertSame(ForkStatus.FORKED, result);
  }

  @Test
  public void returnsFalseIfForkNotDetected() throws GatewayException {
    int containerNum = 5;
    when(connections.getChannel(containerNum)).thenReturn(channel);
    when(channel.getContract("bscc")).thenReturn(bscc);
    when(bscc.evaluateTransaction("CheckForkStatus", "channel5"))
        .thenReturn("false".getBytes());

    ForkStatus result = service.getForkStatus(containerNum);

    assertSame(ForkStatus.NORMAL, result);
  }

  @Test
  public void returnsNotAvailableIfChannelIsUnAvailable() throws GatewayException {
    int containerNum = 5;
    when(connections.getChannel(containerNum)).thenReturn(channel);
    when(channel.getContract("bscc")).thenReturn(bscc);
    when(bscc.evaluateTransaction("CheckForkStatus", "channel5"))
        .thenThrow(new GatewayException(new StatusRuntimeException(Status.UNAVAILABLE)));

    ForkStatus result = service.getForkStatus(containerNum);

    assertSame(ForkStatus.NOT_AVAILABLE, result);
  }

  @Test
  public void throwExceptionForOtherStatusRuntimeException() throws GatewayException {
    int containerNum = 5;
    GatewayException e = new GatewayException(new StatusRuntimeException(Status.UNKNOWN));
    when(connections.getChannel(containerNum)).thenReturn(channel);
    when(channel.getContract("bscc")).thenReturn(bscc);
    when(bscc.evaluateTransaction("CheckForkStatus", "channel5"))
        .thenThrow(e);

    Throwable thrownException = assertThrows(GatewayException.class,
        () -> service.getForkStatus(containerNum));

    assertEquals(e, thrownException);
  }
}
