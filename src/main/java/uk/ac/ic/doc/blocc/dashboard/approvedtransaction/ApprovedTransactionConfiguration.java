package uk.ac.ic.doc.blocc.dashboard.approvedtransaction;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.hyperledger.fabric.client.CloseableIterator;
import org.hyperledger.fabric.client.Network;
import org.hyperledger.fabric.protos.common.Block;
import org.hyperledger.fabric.protos.common.Envelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import uk.ac.ic.doc.blocc.dashboard.fabric.BloccConnections;
import uk.ac.ic.doc.blocc.dashboard.fabric.BloccTransactionProtoUtils;
import uk.ac.ic.doc.blocc.dashboard.fabric.model.TemperatureHumidityReading;

@Configuration
@Profile("prod")
public class ApprovedTransactionConfiguration {

  private final ApprovedTransactionService service;
  private final BloccConnections connections;
  private static final Logger logger =
      LoggerFactory.getLogger(ApprovedTransactionConfiguration.class);

  @Autowired
  public ApprovedTransactionConfiguration(ApprovedTransactionService service,
      BloccConnections connections) {
    this.service = service;
    this.connections = connections;
  }


  @Bean
  CommandLineRunner commandLineRunner() {
    return args -> {

      String channelNumsEnv = System.getenv().getOrDefault("FABRIC_AVAILABLE_CONTAINERS", "5,6");
      List<Integer> availableChannelNums = Arrays.stream(channelNumsEnv.split(","))
          .map(Integer::parseInt)
          .collect(Collectors.toList());

      connections.connectToChannels(availableChannelNums);

      availableChannelNums.forEach(availableChannelNum -> {
        Network channel = connections.getChannel(availableChannelNum);
        CloseableIterator<Block> blockEvents =
            channel.newBlockEventsRequest().startBlock(0).build().getEvents();

        CompletableFuture.runAsync(() -> blockEvents.forEachRemaining(block -> {
              try {
                processBlockEvent(block, availableChannelNum);
              } catch (InvalidProtocolBufferException e) {
                throw new RuntimeException(e);
              }
            })
        );
      });
    };
  }

  private void processBlockEvent(Block block, int availableChannelNum)
      throws InvalidProtocolBufferException {
    for (ByteString envelopeBytes : block.getData().getDataList()) {
      Envelope envelope = Envelope.parseFrom(envelopeBytes);

      if (BloccTransactionProtoUtils.isBscc(envelope)) {
        processBsccTransaction(envelope, availableChannelNum);
      } else if (BloccTransactionProtoUtils.isSensorChaincode(envelope)) {
        processSensorChaincodeTransaction(envelope, availableChannelNum);
      } else {
        logger.info("Other transaction received, ignoring");
      }
    }
  }

  private void processSensorChaincodeTransaction(Envelope envelope, int availableChannelNum)
      throws InvalidProtocolBufferException {
    String txId = BloccTransactionProtoUtils.extractTransactionId(envelope);
    TemperatureHumidityReading reading =
        BloccTransactionProtoUtils.extractTemperatureHumidityReading(envelope);

    logger.info("Sensor Chaincode transaction {} ({}) encountered", txId, reading);

    service.addTempReading(txId, availableChannelNum, reading);
  }

  private void processBsccTransaction(Envelope envelope, int containerNum)
      throws InvalidProtocolBufferException {
    logger.info("BSCC transaction received");
    String approvingMspId = BloccTransactionProtoUtils.extractApprovingMspId(envelope);
    String txId = BloccTransactionProtoUtils.extractApprovedTransactionId(envelope);

    service.approveTransaction(txId, containerNum, approvingMspId);
  }

}
