package uk.ac.ic.doc.blocc.dashboard.fabric;

import com.google.protobuf.InvalidProtocolBufferException;
import org.hyperledger.fabric.protos.common.ChannelHeader;
import org.hyperledger.fabric.protos.common.Envelope;
import org.hyperledger.fabric.protos.common.HeaderType;
import org.hyperledger.fabric.protos.common.Payload;
import org.hyperledger.fabric.protos.common.SignatureHeader;
import org.hyperledger.fabric.protos.msp.SerializedIdentity;
import org.hyperledger.fabric.protos.peer.ChaincodeAction;
import org.hyperledger.fabric.protos.peer.ChaincodeActionPayload;
import org.hyperledger.fabric.protos.peer.ChaincodeInvocationSpec;
import org.hyperledger.fabric.protos.peer.ChaincodeProposalPayload;
import org.hyperledger.fabric.protos.peer.ProposalResponsePayload;
import org.hyperledger.fabric.protos.peer.Transaction;
import org.hyperledger.fabric.protos.peer.TransactionAction;
import uk.ac.ic.doc.blocc.dashboard.fabric.model.TemperatureHumidityReading;

public class BloccTransactionProtoUtils {

  private static ChaincodeInvocationSpec getChaincodeInvocationSpec(Envelope envelope)
      throws InvalidProtocolBufferException {
    Payload payload = Payload.parseFrom(envelope.getPayload());
    Transaction transaction = Transaction.parseFrom(payload.getData());
    ChaincodeActionPayload chaincodeActionPayload =
        ChaincodeActionPayload.parseFrom(transaction.getActions(0).getPayload());
    ChaincodeProposalPayload chaincodeProposalPayload =
        ChaincodeProposalPayload.parseFrom(chaincodeActionPayload.getChaincodeProposalPayload());
    return ChaincodeInvocationSpec.parseFrom(chaincodeProposalPayload.getInput());
  }

  /**
   * Extract the Transaction ID ({@code TxId}) of a transaction.
   *
   * @param envelope a transaction envelope
   * @return the Transaction ID ({@code TxId}) of {@code envelope}
   */
  public static String extractTransactionId(Envelope envelope)
      throws InvalidProtocolBufferException {
    Payload payload = Payload.parseFrom(envelope.getPayload());
    ChannelHeader channelHeader = ChannelHeader.parseFrom(payload.getHeader().getChannelHeader());
    return channelHeader.getTxId();
  }

  /**
   * Extract the Transaction ID of the {@code sensor_chaincode} transaction.
   *
   * @param envelope a transaction envelope of a BSCC approval
   * @return the Transaction ID ({@code TxId}) of the sensor_chaincode transaction this transaction
   * is approving
   */
  public static String extractApprovedTransactionId(Envelope envelope)
      throws InvalidProtocolBufferException {
    ChaincodeInvocationSpec chaincodeInvocationSpec = getChaincodeInvocationSpec(envelope);
    return chaincodeInvocationSpec
        .getChaincodeSpec()
        .getInput()
        .getArgs(1)
        .toStringUtf8()
        .substring(2);
  }

  public static TemperatureHumidityReading extractTemperatureHumidityReading(Envelope envelope)
      throws InvalidProtocolBufferException {
    ChaincodeInvocationSpec chaincodeInvocationSpec = getChaincodeInvocationSpec(envelope);

    float temperature =
        Float.parseFloat(
            chaincodeInvocationSpec.getChaincodeSpec().getInput().getArgs(1).toStringUtf8());
    float relativeHumidity =
        Float.parseFloat(
            chaincodeInvocationSpec.getChaincodeSpec().getInput().getArgs(2).toStringUtf8());
    long timestamp =
        Long.parseLong(
            chaincodeInvocationSpec.getChaincodeSpec().getInput().getArgs(3).toStringUtf8());

    return new TemperatureHumidityReading(temperature, relativeHumidity, timestamp);
  }

  public static boolean isBscc(Envelope envelope) throws InvalidProtocolBufferException {
    return isTransactionOfChaincode(envelope, "bscc");
  }

  public static boolean isSensorChaincode(Envelope envelope) throws InvalidProtocolBufferException {
    return isTransactionOfChaincode(envelope, "sensor_chaincode");
  }

  private static boolean isTransactionOfChaincode(Envelope envelope, String chaincodeName)
      throws InvalidProtocolBufferException {
    Payload payload = Payload.parseFrom(envelope.getPayload());
    ChannelHeader channelHeader = ChannelHeader.parseFrom(payload.getHeader().getChannelHeader());

    if (HeaderType.forNumber(channelHeader.getType()) != HeaderType.ENDORSER_TRANSACTION) {
      return false;
    }

    Transaction transaction = Transaction.parseFrom(payload.getData());

    for (TransactionAction action : transaction.getActionsList()) {
      ChaincodeActionPayload chaincodeActionPayload =
          ChaincodeActionPayload.parseFrom(action.getPayload());

      if (!chaincodeActionPayload.hasAction()) {
        continue;
      }

      ProposalResponsePayload proposalResponsePayload =
          ProposalResponsePayload.parseFrom(
              chaincodeActionPayload.getAction().getProposalResponsePayload());

      ChaincodeAction chaincodeAction =
          ChaincodeAction.parseFrom(proposalResponsePayload.getExtension());

      if (chaincodeAction.getChaincodeId().getName().equals(chaincodeName)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Extract the MSP ID that created the transaction.
   *
   * @param envelope a transaction envelope
   * @return the MSP ID that created the transaction
   */
  public static String extractCreatorMspId(Envelope envelope)
      throws InvalidProtocolBufferException {
    Payload payload = Payload.parseFrom(envelope.getPayload());
    SignatureHeader signatureHeader = SignatureHeader.parseFrom(
        payload.getHeader().getSignatureHeader());
    SerializedIdentity serializedIdentity = SerializedIdentity.parseFrom(
        signatureHeader.getCreator());
    return serializedIdentity.getMspid();
  }

  /**
   * Extract the timestamp of a transaction.
   *
   * @param envelope a transaction envelope
   * @return the timestamp of the transaction
   */
  public static long extractTransactionTimestamp(Envelope envelope)
      throws InvalidProtocolBufferException {
    Payload payload = Payload.parseFrom(envelope.getPayload());
    ChannelHeader channelHeader = ChannelHeader.parseFrom(payload.getHeader().getChannelHeader());
    return channelHeader.getTimestamp().getSeconds();
  }
}
