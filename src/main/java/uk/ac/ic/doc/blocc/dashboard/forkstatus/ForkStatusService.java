package uk.ac.ic.doc.blocc.dashboard.forkstatus;

import com.google.gson.Gson;
import io.grpc.Status.Code;
import org.hyperledger.fabric.client.Contract;
import org.hyperledger.fabric.client.GatewayException;
import org.hyperledger.fabric.client.Network;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import uk.ac.ic.doc.blocc.dashboard.fabric.BloccConnections;
import uk.ac.ic.doc.blocc.dashboard.forkstatus.model.ForkStatus;

@Service
@Profile("prod")
public class ForkStatusService {

  private final BloccConnections connections;
  private final Gson gson = new Gson();

  @Autowired
  public ForkStatusService(BloccConnections connections) {
    this.connections = connections;
  }

  public ForkStatus getForkStatus(int containerNum) throws GatewayException {
    Network channel = connections.getChannel(containerNum);
    Contract bscc = channel.getContract("bscc");
    try {
      byte[] checkForkStatuses = bscc.evaluateTransaction("CheckForkStatus",
          String.format("channel%d", containerNum));
      return gson.fromJson(new String(checkForkStatuses), Boolean.class) ? ForkStatus.FORKED
          : ForkStatus.NORMAL;
    } catch (GatewayException e) {
      if (e.getStatus().getCode() == Code.UNAVAILABLE) {
        return ForkStatus.NOT_AVAILABLE;
      } else {
        throw e;
      }
    }

  }
}
